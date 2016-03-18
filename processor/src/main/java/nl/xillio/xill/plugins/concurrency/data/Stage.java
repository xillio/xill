package nl.xillio.xill.plugins.concurrency.data;

import me.biesaart.utils.Log;
import org.slf4j.Logger;

/**
 * This class represents a collection of workers that run the same robot.
 *
 * @author Thomas Biesaart
 */
public class Stage extends Thread {
    private static final Logger LOGGER = Log.get();
    private Worker[] workers;
    private final XillQueue xillQueue;

    public Stage(Worker[] workers, XillQueue xillQueue) {
        this.workers = workers;
        this.xillQueue = xillQueue;
    }

    @Override
    public void run() {
        try {
            doRun();
        } finally {
            xillQueue.close();
        }
    }

    private void doRun() {
        // Start all workers
        for (Worker worker : workers) {
            worker.start();
        }

        // Join all workers
        for (Worker worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                LOGGER.error("Interrupted while waiting for threads to finish", e);
            }
        }
    }

    public void setInputStage(Stage inputStage) {
        for (Worker worker : workers) {
            worker.setInputQueue(inputStage.xillQueue);
        }
    }

    /**
     * Remove all references to the output queue. This method should be called on the last stage in a pipeline.
     */
    public void removeOutputQueue() {
        xillQueue.closeAndClear();
        for (Worker worker : workers) {
            worker.removeOutputQueue();
        }
    }

    public Worker[] getWorkers() {
        return workers;
    }
}
