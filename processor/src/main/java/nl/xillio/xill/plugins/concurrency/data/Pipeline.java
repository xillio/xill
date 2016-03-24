package nl.xillio.xill.plugins.concurrency.data;

/**
 * This class represents the configuration of a pipeline.
 *
 * @author Thomas Biesaart
 * @author Titus Nachbauer
 */
public class Pipeline {
    private final WorkerConfiguration[] configuration;

    public Pipeline(WorkerConfiguration[] configuration) {
        this.configuration = configuration;
    }

    public WorkerConfiguration[] getConfiguration() {
        return configuration;
    }
}
