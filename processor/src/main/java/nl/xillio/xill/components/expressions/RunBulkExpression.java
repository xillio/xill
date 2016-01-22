package nl.xillio.xill.components.expressions;

import nl.xillio.plugins.PluginLoader;
import nl.xillio.plugins.XillPlugin;
import nl.xillio.xill.XillProcessor;
import nl.xillio.xill.api.*;
import nl.xillio.xill.api.components.*;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.api.errors.XillParsingException;
import nl.xillio.xill.services.files.FileResolver;
import nl.xillio.xill.services.files.FileResolverImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * This class represents calling another robot multiple times in a separate threads
 */
public class RunBulkExpression implements Processable {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Logger robotLogger;
    private final Processable path;
    private final RobotID robotID;
    private final PluginLoader<XillPlugin> pluginLoader;
    private Processable argument;
    private final FileResolver resolver;
    private Processable options;
    private int maxThreadsVal = 100;
    private boolean onErrorStopAll = false;


    /**
     * Create a new {@link RunBulkExpression}
     *
     * @param path         the path of the called bot
     * @param robotID      the root robot of this tree
     * @param pluginLoader the current plugin loader
     */
    public RunBulkExpression(final Processable path, final RobotID robotID, final PluginLoader<XillPlugin> pluginLoader) {
        this.path = path;
        this.robotID = robotID;
        this.pluginLoader = pluginLoader;
        robotLogger = RobotAppender.getLogger(robotID);
        resolver = new FileResolverImpl();
    }

    @Override
    public InstructionFlow<MetaExpression> process(final Debugger debugger) {
        MetaExpression pathExpression = path.process(debugger).get();

        File otherRobot = resolver.buildFile(new ConstructContext(robotID, robotID, null, null, null, null, null), pathExpression.getStringValue());

        LOGGER.debug("Evaluating runBulk for " + otherRobot.getAbsolutePath());

        if (debugger.getStackTrace().size() > Xill.MAX_STACK_SIZE) {
            throw new RobotRuntimeException("RunBulk went into too many recursions.");
        }
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

    /**
     * Convert input into list of MetaExpressions where input can be one of: null, single ATOMIC value, LIST, iterator
     */
    private List<MetaExpression> iterateArgument(final MetaExpression result) {
        final List<MetaExpression> list = new LinkedList<>();

        if (result.isNull()) {
            return list;
        }

        switch (result.getType()) {
            case ATOMIC:
                if (result.getMeta(MetaExpressionIterator.class) == null) {
                    list.add(result);
                } else {
                    MetaExpressionIterator iterator = result.getMeta(MetaExpressionIterator.class);
                     while (iterator.hasNext()) {
                         list.add(iterator.next());
                     }
                }
                break;
            case LIST: // Iterate over list
                list.addAll(result.getValue());
                break;
            default:
                throw new RobotRuntimeException("Invalid argument!");
        }

        return list;
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

        // Get list of arguments
        InstructionFlow<MetaExpression> argumentResult = argument.process(debugger);
        List<MetaExpression> args = iterateArgument(argumentResult.get());

        List<Thread> threadList = new LinkedList<>();
        final boolean error[] = {false};
        int robotRunCount = 0;

        // Iterate list of arguments and for each argument run a robot in a separate thread
        for (MetaExpression arg : args) {

            // Wait until there are running less than maxThreads
            if (!waitForThreadRun(threadList, maxThreadsVal-1)) {
                error[0] = true;
                break;
            }

            // If there is any error or user stop has been invoked - don't start next thread and end up the cycle
            if (error[0] || debugger.shouldStop()) {
                break;
            }

            // Prepare new thread
            Thread robotThread = new Thread(() -> {
                if (!processRobot(debugger, calledRobotFile, arg) && onErrorStopAll) {
                    error[0] = true; // Signal that the robot has ended up with error - stop all running threads
                }
            });

            robotThread.setUncaughtExceptionHandler((thread, e) -> {
                error[0] = true;
                LOGGER.error("Error occurred in the thread", e);
            });

            threadList.add(robotThread);

            // Run the thread
            robotThread.start();

            robotRunCount++;
        }

        // Wait for all remaining running threads are done and report if error occurred
        if (!waitForThreadRun(threadList, 0) || error[0]) {
            throw new RobotRuntimeException("Bulk robot processing has been interrupted due to the error in one or more running bots!");
        }

        return robotRunCount;
    }

    /**
     * Wait for the condition when actual number of running threads is <= maxThreads
     *
     * @param list The list of currently running threads
     * @param maxThreads The maximum threads that are allowed to run at a same time (defines a "thread batch size")
     * @return true if the required condition is met, false if error occurred
     */
    private boolean waitForThreadRun(final List<Thread> list, final int maxThreads) {
        boolean removed = false;
        while (list.size() > maxThreads) {// Wait for at least one thread to finish
            if (!removed) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    LOGGER.error("Interrupted while sleeping", e);
                }
            }

            removed = false;

            for (Thread t : list) {// Check the thread list for any finished thread
                if (!t.isAlive()) {
                    // Thread seems to be ended up
                    try {
                        t.join();
                    } catch (InterruptedException e) {
                        LOGGER.error("Interrupted while waiting for join", e);
                        return false;
                    }
                    list.remove(t);
                    removed = true;
                    break;
                }
            }
            // No thread had finished, so wait and test again
        }

        return true;
    }

    /**
     * @return true if the robot ended up successfully, false if there was an error or interruption, etc.
     */
    private boolean processRobot(final Debugger debugger, final File calledRobotFile, final MetaExpression arg) {
        // Process the robot
        try {
            StoppableDebugger childDebugger = (StoppableDebugger) debugger.createChild();
            childDebugger.setStopOnError(true);

            XillProcessor processor = new XillProcessor(robotID.getProjectPath(), calledRobotFile, pluginLoader, childDebugger);

            processor.compileAsSubRobot(robotID);

            try {
                Robot robot = processor.getRobot();
                robot.setArgument(arg);

                processor.getRobot().process(childDebugger);
                // Ignoring the returned value from the bot as it won't be processed anyway

                if ( (childDebugger instanceof StoppableDebugger) && (((StoppableDebugger)childDebugger).hasErrorOccurred()) ) {
                    return false; // If error occurred during the run of the called robot
                }

                return true;

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

        @SuppressWarnings("unchecked")
        Map<String, MetaExpression> optionParameters = (Map<String, MetaExpression>) optionVar.getValue();

        for (Map.Entry<String, MetaExpression> entry : optionParameters.entrySet()) {
            switch(entry.getKey()) {
                case "maxThreads":
                    maxThreadsVal = entry.getValue().getNumberValue().intValue();
                    if (maxThreadsVal < 1) {
                        throw new RobotRuntimeException("Invalid maxThreads value");
                    }
                    break;
                case "onError":
                    String value = entry.getValue().getStringValue();
                    if ("stopAll".equals(value)) {
                        onErrorStopAll = true;
                    } else if ("stopOne".equals(value)) {
                        onErrorStopAll = false;
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
