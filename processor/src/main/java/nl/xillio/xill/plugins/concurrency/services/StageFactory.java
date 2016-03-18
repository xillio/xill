package nl.xillio.xill.plugins.concurrency.services;

import com.google.inject.Inject;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.plugins.concurrency.data.Stage;
import nl.xillio.xill.plugins.concurrency.data.Worker;
import nl.xillio.xill.plugins.concurrency.data.WorkerConfiguration;

/**
 * This class is responsible for creating a single stage.
 *
 * @author Thomas Biesaart
 */
class StageFactory {
    private final WorkerThreadFactory workerThreadFactory;

    @Inject
    StageFactory(WorkerThreadFactory workerThreadFactory) {
        this.workerThreadFactory = workerThreadFactory;
    }

    public Stage build(WorkerConfiguration workerConfiguration, ConstructContext context) {
        // First we build all threads
        Worker[] workers = new Worker[workerConfiguration.getThreadCount()];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = workerThreadFactory.build(workerConfiguration, context, i);
        }

        // Then we connect the queues
        for (int i = 1; i < workers.length; i++) {
            Worker inputWorker = workers[i - 1];
            Worker targetWorker = workers[i];

            targetWorker.setInputWorker(inputWorker);
        }

        return new Stage(workers);
    }
}
