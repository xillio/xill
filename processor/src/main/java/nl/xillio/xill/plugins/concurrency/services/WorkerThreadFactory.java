package nl.xillio.xill.plugins.concurrency.services;

import com.google.inject.Inject;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.NullDebugger;
import nl.xillio.xill.api.XillEnvironment;
import nl.xillio.xill.api.XillProcessor;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.api.errors.XillParsingException;
import nl.xillio.xill.plugins.concurrency.data.Worker;
import nl.xillio.xill.plugins.concurrency.data.WorkerConfiguration;
import nl.xillio.xill.plugins.concurrency.data.XillQueue;
import nl.xillio.xill.services.files.FileResolver;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static nl.xillio.xill.api.components.ExpressionBuilderHelper.emptyObject;
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
     * @return the worker
     */
    public Worker build(WorkerConfiguration workerConfiguration, ConstructContext context, int threadId) {
        XillProcessor processor = compile(workerConfiguration, context);

        XillQueue outputQueue = new XillQueue(workerConfiguration.getOutputQueueSize());
        MetaExpression argument = buildArgument(outputQueue, workerConfiguration.getConfiguration(), threadId);
        processor.getRobot().setArgument(argument);

        return new Worker(processor.getRobot(), processor.getDebugger(), outputQueue);
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
        MetaExpression result = configuration;
        if (result == null || result.getType() != ExpressionDataType.OBJECT) {
            result = emptyObject();
        }

        Map<String, MetaExpression> internalMap = result.getValue();

        MetaExpression outputValue = fromValue("[Queue]");
        outputValue.storeMeta(outputQueue);
        outputValue.registerReference();
        internalMap.put("output", outputValue);

        MetaExpression threadIdValue = fromValue(threadId);
        threadIdValue.registerReference();
        internalMap.put("threadId", threadIdValue);

        return result;
    }

    private XillProcessor compile(WorkerConfiguration workerConfiguration, ConstructContext context) {
        Path robot = fileResolver.buildPath(context, fromValue(workerConfiguration.getRobot()));
        XillProcessor processor = getProcessor(robot, context);

        compile(processor);
        return processor;
    }

    private void compile(XillProcessor processor) {
        try {
            processor.compile();
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
