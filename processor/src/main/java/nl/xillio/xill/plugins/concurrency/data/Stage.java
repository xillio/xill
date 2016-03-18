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

    public Stage(Worker[] workers) {
        this.workers = workers;
    }

    @Override
    public void run() {
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

    public Worker[] getWorkers() {
        return workers;
    }
}
