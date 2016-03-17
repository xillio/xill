package nl.xillio.xill.plugins.concurrency.services;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.concurrency.data.Pipeline;
import nl.xillio.xill.plugins.concurrency.data.WorkerConfiguration;

import java.util.List;

/**
 * This class will build a pipeline from a MetaExpression list.
 *
 * @author Thomas Biesaart
 * @author Titus Nachbauer
 */
public class PipelineFactory {
    private final WorkerConfigurationFactory workerConfigurationFactory;

    @Inject
    public PipelineFactory(WorkerConfigurationFactory workerConfigurationFactory) {
        this.workerConfigurationFactory = workerConfigurationFactory;
    }

    public Pipeline build(List<MetaExpression> list) {
        WorkerConfiguration[] workers = list.stream()
                .map(workerConfigurationFactory::build)
                .toArray(WorkerConfiguration[]::new);

        return new Pipeline(workers);
    }
}
