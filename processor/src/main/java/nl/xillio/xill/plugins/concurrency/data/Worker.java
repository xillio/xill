package nl.xillio.xill.plugins.concurrency.data;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.Robot;

/**
 * This class represents an executable worker, created from a {@link WorkerConfiguration}.
 */
public class Worker extends Thread{

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
}
