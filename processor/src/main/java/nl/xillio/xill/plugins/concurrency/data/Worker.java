package nl.xillio.xill.plugins.concurrency.data;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Robot;

import java.util.Map;

import static nl.xillio.xill.api.components.ExpressionBuilderHelper.fromValue;

/**
 * This class represents an executable worker, created from a {@link WorkerConfiguration}.
 */
public class Worker extends Thread {

    private final Robot robot;
    private final Debugger debugger;

    public Worker(Robot robot, Debugger debugger) {
        this.robot = robot;
        this.debugger = debugger;
    }

    @Override
    public void run() {
        try {
            robot.process(debugger);
        } catch (Exception e) {
            debugger.handle(e);
        }
    }

    /**
     * Calling this method will set the input field of the argument of this option to the output queue
     * of the passed worker.
     *
     * @param inputQueue the queue
     */
    void setInputQueue(XillQueue inputQueue) {
        MetaExpression argument = robot.getArgument();
        Map<String, MetaExpression> internalMap = argument.getValue();

        MetaExpression queue = fromValue("[Queue]");
        queue.storeMeta(inputQueue);
        queue.registerReference();
        internalMap.put("input", queue);
    }

    /**
     * Remove the reference to the output queue. This method should be called on all workers
     * in the last stage.
     */
    void removeOutputQueue() {
        MetaExpression argument = robot.getArgument();
        Map<String, MetaExpression> internalMap = argument.getValue();
        MetaExpression expression = internalMap.remove("output");
        if (expression != null) {
            expression.releaseReference();
        }
    }
}
