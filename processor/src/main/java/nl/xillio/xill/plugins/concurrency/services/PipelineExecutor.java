package nl.xillio.xill.plugins.concurrency.services;

import com.google.inject.Inject;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.plugins.concurrency.data.Pipeline;
import nl.xillio.xill.plugins.concurrency.data.Stage;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is responsible for executing the workers defined in a pipeline.
 *
 * @author Titus Nachbauer
 * @author Thomas Biesaart
 */
public class PipelineExecutor {
    private final StageFactory stageFactory;

    @Inject
    PipelineExecutor(StageFactory stageFactory) {
        this.stageFactory = stageFactory;
    }

    public void execute(Pipeline pipeline, ConstructContext context) {
        // First we build all stages
        List<Stage> stages = Arrays.stream(pipeline.getConfiguration())
                .map(conf -> stageFactory.build(conf, context))
                .collect(Collectors.toList());

        // Then we start all stages
        stages.stream().forEach(Stage::start);
    }
}
