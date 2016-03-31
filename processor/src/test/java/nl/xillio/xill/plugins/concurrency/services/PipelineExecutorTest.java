package nl.xillio.xill.plugins.concurrency.services;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.plugins.concurrency.data.Pipeline;
import nl.xillio.xill.plugins.concurrency.data.Stage;
import nl.xillio.xill.plugins.concurrency.data.WorkerConfiguration;
import org.testng.annotations.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


public class PipelineExecutorTest extends TestUtils {

    @Test
    public void testExecute() {
        Stage resultStage = mock(Stage.class);
        StageFactory stageFactory = mock(StageFactory.class);
        when(stageFactory.build(any(), any())).thenReturn(resultStage);
        Pipeline pipeline = new Pipeline(new WorkerConfiguration[]{
                new WorkerConfiguration(),
                new WorkerConfiguration()
        });

        new PipelineExecutor(stageFactory).execute(pipeline, context());

        // Test that both stages are started
        verify(resultStage, times(2)).start();
    }
}