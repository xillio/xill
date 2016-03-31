package nl.xillio.xill.plugins.concurrency.services;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
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
        if (list.isEmpty()) {
            throw new RobotRuntimeException("A pipeline must have at least one worker");
        }
        WorkerConfiguration[] workers = list.stream()
                .map(workerConfigurationFactory::build)
                .toArray(WorkerConfiguration[]::new);

        return new Pipeline(workers);
    }
}
