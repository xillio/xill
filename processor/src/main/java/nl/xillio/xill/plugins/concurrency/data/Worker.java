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
    private XillQueue outputQueue;

    public Worker(Robot robot, Debugger debugger, XillQueue outputQueue) {
        this.robot = robot;
        this.debugger = debugger;
        this.outputQueue = outputQueue;
    }

    @Override
    public void run() {
        try {
            robot.process(debugger);
        } catch (Exception e) {
            debugger.handle(e);
        } finally {
            outputQueue.close();
        }
    }

    /**
     * Calling this method will set the input field of the argument of this option to the output queue
     * of the passed worker.
     *
     * @param inputWorker the worker
     */
    public void setInputWorker(Worker inputWorker) {
        MetaExpression argument = robot.getArgument();
        Map<String, MetaExpression> internalMap = argument.getValue();

        MetaExpression queue = fromValue("[Queue]");
        queue.storeMeta(inputWorker.outputQueue);
        queue.registerReference();
        internalMap.put("input", queue);
    }
}
