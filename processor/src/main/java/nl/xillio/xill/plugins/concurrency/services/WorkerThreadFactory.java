package nl.xillio.xill.plugins.concurrency.services;

import com.google.inject.Inject;
import nl.xillio.xill.api.XillEnvironment;
import nl.xillio.xill.api.XillProcessor;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.api.errors.XillParsingException;
import nl.xillio.xill.plugins.concurrency.data.Worker;
import nl.xillio.xill.plugins.concurrency.data.WorkerConfiguration;
import nl.xillio.xill.plugins.concurrency.data.XillQueue;
import nl.xillio.xill.services.files.FileResolver;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;

import static nl.xillio.xill.api.components.ExpressionBuilderHelper.fromValue;

/**
 * This class is responsible for creating threads from {@link WorkerConfiguration}.
 *
 * @author Titus Nachbauer
 * @author Thomas Biesaart
 */
class WorkerThreadFactory {
    private final XillEnvironment xillEnvironment;
    private final FileResolver fileResolver;

    @Inject
    public WorkerThreadFactory(XillEnvironment xillEnvironment, FileResolver fileResolver) {
        this.xillEnvironment = xillEnvironment;
        this.fileResolver = fileResolver;
    }

    /**
     * This method will assemble a worker that is ready for execution. It will attach an output queue to the worker
     * which should later be connected to the input queue of the next worker.
     *
     * @param workerConfiguration the configuration
     * @param context             the context of the worker
     * @param threadId            the id of the worker thread. This should be 0 for the first worker.
     * @param outputQueue         the queue that should be used
     * @return the worker
     */
    public Worker build(WorkerConfiguration workerConfiguration, ConstructContext context, int threadId, XillQueue outputQueue) {
        XillProcessor processor = compile(workerConfiguration, context);

        // Set the error handler
        processor.getDebugger().setErrorHandler(e -> {
                    String robot = workerConfiguration.getRobot();
                    String message = String.format("An error occurred in %s: %s", robot, e.getMessage());
                    context.getRootLogger().error(message, e);
                }
        );

        MetaExpression argument = buildArgument(outputQueue, workerConfiguration.getConfiguration(), threadId);
        processor.getRobot().setArgument(argument);

        return new Worker(processor.getRobot(), processor.getDebugger());
    }

    /**
     * Build the input argument for the worker. This argument uses the configuration as a base if there is one.
     * The argument will contain at least two fields: output and threadId
     *
     * @param outputQueue   the output queue
     * @param configuration the base configuration
     * @param threadId      the thread id
     * @return the argument
     */
    private MetaExpression buildArgument(XillQueue outputQueue, MetaExpression configuration, int threadId) {
        LinkedHashMap<String, MetaExpression> map = new LinkedHashMap<>();

        if (configuration != null && configuration.getType() == ExpressionDataType.OBJECT) {
            map.putAll(configuration.getValue());
        }


        MetaExpression outputValue = fromValue("[Queue]");
        outputValue.storeMeta(outputQueue);
        map.put("output", outputValue);

        MetaExpression threadIdValue = fromValue(threadId);
        map.put("threadId", threadIdValue);

        // Register references
        map.values().forEach(MetaExpression::registerReference);

        return fromValue(map);
    }

    private XillProcessor compile(WorkerConfiguration workerConfiguration, ConstructContext context) {
        Path robot = fileResolver.buildPath(context, fromValue(workerConfiguration.getRobot()));
        XillProcessor processor = getProcessor(robot, context);

        compile(processor, context.getRobotID());
        return processor;
    }

    private void compile(XillProcessor processor, RobotID parentRobotId) {
        try {
            processor.compileAsSubRobot(parentRobotId);
        } catch (IOException e) {
            throw new RobotRuntimeException("An IO error occurred while parsing a robot: " + e.getMessage(), e);
        } catch (XillParsingException e) {
            throw new RobotRuntimeException("Syntax error in " + processor.getRobotID().getPath() + ": " + e.getMessage(), e);
        }
    }

    private XillProcessor getProcessor(Path robot, ConstructContext context) {
        try {
            return context.createChildProcessor(robot, xillEnvironment);
        } catch (IOException e) {
            throw new RobotRuntimeException("An IO error occurred while compiling a robot: " + e.getMessage(), e);
        }
    }
}
