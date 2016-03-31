package nl.xillio.xill.plugins.concurrency.services;

import com.google.inject.Inject;
import me.biesaart.utils.Log;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.plugins.concurrency.data.Pipeline;
import nl.xillio.xill.plugins.concurrency.data.Stage;
import org.slf4j.Logger;

import java.util.Arrays;

/**
 * This class is responsible for executing the workers defined in a pipeline.
 *
 * @author Titus Nachbauer
 * @author Thomas Biesaart
 */
public class PipelineExecutor {
    private static final Logger LOGGER = Log.get();
    private final StageFactory stageFactory;

    @Inject
    PipelineExecutor(StageFactory stageFactory) {
        this.stageFactory = stageFactory;
    }

    public void execute(Pipeline pipeline, ConstructContext context) {
        // First we build all stages
        Stage[] stages = Arrays.stream(pipeline.getConfiguration())
                .map(conf -> stageFactory.build(conf, context))
                .toArray(Stage[]::new);

        // Connect the stages
        for (int i = 1; i < stages.length; i++) {
            Stage input = stages[i - 1];
            Stage consumer = stages[i];
            consumer.setInputStage(input);
        }

        // Remove the output queue from the last stage
        stages[stages.length - 1].removeOutputQueue();

        // Then we start all stages
        for (Stage stage : stages) {
            stage.start();
        }

        // And we wait for them to finish
        for (Stage stage : stages) {
            try {
                stage.join();
            } catch (InterruptedException e) {
                LOGGER.error("Interrupted while waiting for stage to finish", e);
            }
        }
    }
}
