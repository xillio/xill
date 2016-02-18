package nl.xillio.xill.components.expressions;

import com.google.inject.Inject;
import me.biesaart.utils.Log;
import nl.xillio.plugins.XillPlugin;
import nl.xillio.xill.Xill;
import nl.xillio.xill.XillProcessor;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.LogUtil;
import nl.xillio.xill.api.NullDebugger;
import nl.xillio.xill.api.StoppableDebugger;
import nl.xillio.xill.api.components.*;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.api.errors.XillParsingException;
import nl.xillio.xill.services.files.FileResolver;
import nl.xillio.xill.services.files.FileResolverImpl;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * This class represents calling another robot multiple times in a separate threads
 */
public class RunBulkExpression implements Processable {

    @Inject
    private LogUtil logUtil;

    private static final Logger LOGGER = Log.get();
    private final Logger robotLogger;
    private final Processable path;
    private final RobotID robotID;
    private final List<XillPlugin> plugins;
    private Processable argument;
    private final FileResolver resolver;
    private Processable options;
    private int maxThreadsVal = 0;
    private boolean stopOnError = false;

    private class Control {
        private int runCount = 0;
        private boolean stop = false;
        private final Debugger debugger;
        private final File calledRobotFile;

        public Control(final Debugger debugger, final File calledRobotFile) {
            this.debugger = debugger;
            this.calledRobotFile = calledRobotFile;
        }

        public Debugger getDebugger() {
            return debugger;
        }

        public File getCalledRobotFile() {
            return calledRobotFile;
        }

        public synchronized void incRunCount() {
            runCount++;
        }

        public int getRunCount() {
            return runCount;
        }

        public synchronized void signalStop() {
            stop = true;
        }

        public synchronized boolean shouldStop() {
            return stop;
        }
    }

    private class MasterThread extends Thread {
        private final Iterator<MetaExpression> source;
        private final BlockingQueue<MetaExpression> queue;
        private final Control control;

        public MasterThread(final Iterator<MetaExpression> source, final BlockingQueue<MetaExpression> queue, final Control control) {
            super("RunBulk MasterThread");
            this.source = source;
            this.queue = queue;
            this.control = control;
        }

        @Override
        public void run() {
            while (source.hasNext() && !control.shouldStop()) {
                try {
                    MetaExpression item = source.next();
                    while (!queue.offer(item, 100, TimeUnit.MILLISECONDS)) {
                        if (control.shouldStop()) {
                            return;
                        }
                    }
                } catch (InterruptedException e) {
                    LOGGER.error("Interrupted while waiting for queue item", e);
                    return;
                }
            }
        }
    }

    private class WorkerThread extends Thread {
        private final BlockingQueue<MetaExpression> queue;
        private final Control control;

        public WorkerThread(final BlockingQueue<MetaExpression> queue, final Control control) {
            super("RunBulk WorkerThread");
            this.queue = queue;
            this.control = control;
        }

        @Override
        public void run() {
            while (!control.shouldStop()) {
                try {
                    processQueueItem(queue.poll(100, TimeUnit.MILLISECONDS));
                } catch (InterruptedException e) {
                    LOGGER.error("Interrupted while processing queue item", e);
                    return;
                }
            }
        }

        private void processQueueItem(final MetaExpression item) {
            if (item != null) {
                if (!processRobot(control.getDebugger(), control.getCalledRobotFile(), item)) {
                    control.signalStop();
                } else {
                    if (control.getDebugger().shouldStop()) {
                        control.signalStop();
                    } else {
                        control.incRunCount();
                    }
                }
            }
        }

        /**
         * @return true if the robot ended up successfully, false if there was an error or interruption, etc.
         */
        private boolean processRobot(final Debugger debugger, final File calledRobotFile, final MetaExpression arg) {
            // Process the robot
            try {
                StoppableDebugger childDebugger = (StoppableDebugger) debugger.createChild();
                childDebugger.setStopOnError(stopOnError);

                XillProcessor processor = new XillProcessor(robotID.getProjectPath(), calledRobotFile, plugins, childDebugger);

                processor.compileAsSubRobot(robotID);

                try {
                    Robot robot = processor.getRobot();
                    robot.setArgument(arg);

                    processor.getRobot().process(childDebugger);
                    // Ignoring the returned value from the bot as it won't be processed anyway

                    return !(stopOnError && childDebugger.hasErrorOccurred());

                } catch (Exception e) {
                    if (e instanceof RobotRuntimeException) {
                        throw (RobotRuntimeException) e;
                    }
                    throw new RobotRuntimeException("An exception occurred while evaluating " + calledRobotFile.getAbsolutePath(), e);
                } finally {
                    debugger.removeChild(childDebugger);
                }

            } catch (IOException e) {
                throw new RobotRuntimeException("Error while calling robot: " + e.getMessage(), e);
            } catch (XillParsingException e) {
                throw new RobotRuntimeException("Error while parsing robot: " + e.getMessage(), e);
            } catch (Exception e) {
                debugger.handle(e);
            }

            return false; // Something went wrong
        }
    }

    /**
     * Create a new {@link RunBulkExpression}
     *
     * @param path    the path of the called bot
     * @param robotID the root robot of this tree
     * @param plugins the current plugin loader
     */
    public RunBulkExpression(final Processable path, final RobotID robotID, final List<XillPlugin> plugins) {
        this.path = path;
        this.robotID = robotID;
        this.plugins = plugins;
        robotLogger = logUtil.getLogger(robotID);
        resolver = new FileResolverImpl();
        maxThreadsVal = 0;
    }

    @Override
    public InstructionFlow<MetaExpression> process(final Debugger debugger) {
        MetaExpression pathExpression = path.process(debugger).get();

        File otherRobot = resolver.buildPath(new ConstructContext(robotID, robotID, null, null, null, null, null), pathExpression).toFile();

        LOGGER.debug("Evaluating runBulk for " + otherRobot.getAbsolutePath());

        if (!otherRobot.exists()) {
            throw new RobotRuntimeException("Called robot " + otherRobot.getAbsolutePath() + " does not exist.");
        }

        if (!otherRobot.getName().endsWith(Xill.FILE_EXTENSION)) {
            throw new RobotRuntimeException("Can only call robots with the ." + Xill.FILE_EXTENSION + " extension.");
        }

        parseOptions();
        int robotRunCount = runBulk(debugger, otherRobot);

        return InstructionFlow.doResume(ExpressionBuilderHelper.fromValue(robotRunCount));
    }

    private Iterator<MetaExpression> getIterator(final MetaExpression result) {
        if (result.isNull()) {
            return null;
        }

        switch (result.getType()) {
            case ATOMIC:
                if (!result.hasMeta(MetaExpressionIterator.class)) {
                    List<MetaExpression> list = new LinkedList<>();
                    list.add(result);
                    return list.iterator();
                } else {
                    return result.getMeta(MetaExpressionIterator.class);
                }
            case LIST: // Iterate over list
                List<MetaExpression> elements = result.getValue();
                return elements.iterator();
        }

        throw new RobotRuntimeException("Invalid argument!");
    }

    /**
     * Run the called robot multiple times
     *
     * @return The number of robot runs
     */
    private int runBulk(final Debugger debugger, final File calledRobotFile) {
        // Evaluate argument
        if (argument == null) {
            return 0; // Nothing to do
        }

        if (maxThreadsVal == 0) {// Default value equals the number of logical cores
            maxThreadsVal = Runtime.getRuntime().availableProcessors();
        }

        // Get argument iterator
        InstructionFlow<MetaExpression> argumentResult = argument.process(debugger);
        Iterator<MetaExpression> source = getIterator(argumentResult.get());
        if (source == null) {
            return 0;
        }

        BlockingQueue<MetaExpression> queue = new ArrayBlockingQueue<>(maxThreadsVal);
        Control control = new Control(debugger, calledRobotFile);

        // Start master thread
        Thread master = new MasterThread(source, queue, control);
        master.start();

        // Start working threads
        List<Thread> workingThreads = new LinkedList<>();
        for (int i = 0; i < maxThreadsVal; i++) {
            Thread worker = new WorkerThread(queue, control);
            worker.start();
            workingThreads.add(worker);
        }

        // Wait for master to complete
        try {
            master.join();
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted while waiting for join", e);
        }

        // Wait for until entire queue is processed
        while (!control.shouldStop() && !queue.isEmpty()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                LOGGER.error("Interrupted while sleeping", e);
            }
        }

        control.signalStop();

        // Wait for all worker threads to complete
        workingThreads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                LOGGER.error("Interrupted while waiting for join", e);
            }
        });

        return control.getRunCount();
    }

    @Override
    public Collection<Processable> getChildren() {
        return Collections.singletonList(path);
    }

    /**
     * @return the robotLogger
     */
    public Logger getRobotLogger() {
        return robotLogger;
    }

    /**
     * Set the argument that will be used for running called robots
     *
     * @param argument
     */
    public void setArgument(final Processable argument) {
        this.argument = argument;
    }

    /**
     * Set the options that will be used when running called robots
     *
     * @param options
     */
    public void setOptions(final Processable options) {
        this.options = options;
    }

    /**
     * Parse the option expression
     */
    private void parseOptions() {
        if (options == null) {
            return;
        }
        MetaExpression optionVar = options.process(new NullDebugger()).get();
        if (optionVar.isNull()) {
            return;
        }

        if (optionVar.getType() != ExpressionDataType.OBJECT) {
            throw new RobotRuntimeException("Invalid max. threads value");
        }

        Map<String, MetaExpression> optionParameters = optionVar.getValue();

        for (Map.Entry<String, MetaExpression> entry : optionParameters.entrySet()) {
            switch (entry.getKey()) {
                case "maxThreads":
                    maxThreadsVal = entry.getValue().getNumberValue().intValue();
                    if (maxThreadsVal < 1) {
                        throw new RobotRuntimeException("Invalid maxThreads value");
                    }
                    break;
                case "stopOnError":
                    String value = entry.getValue().getStringValue();
                    if ("yes".equals(value)) {
                        stopOnError = true;
                    } else if ("no".equals(value)) {
                        stopOnError = false;
                    } else {
                        throw new RobotRuntimeException("Invalid onError value");
                    }
                    break;
                default:
                    throw new RobotRuntimeException("Invalid option");
            }
        }
    }
}
