package nl.xillio.xill.plugins.concurrency.services;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.plugins.concurrency.data.Stage;
import nl.xillio.xill.plugins.concurrency.data.Worker;
import nl.xillio.xill.plugins.concurrency.data.WorkerConfiguration;
import org.testng.annotations.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;


public class StageFactoryTest extends TestUtils {
    @Test
    public void testBuild() {
        WorkerThreadFactory workerThreadFactory = mock(WorkerThreadFactory.class);
        when(workerThreadFactory.build(any(), any(), anyInt())).thenReturn(mock(Worker.class));

        StageFactory stageFactory = new StageFactory(workerThreadFactory);
        WorkerConfiguration workerConfiguration = new WorkerConfiguration();
        workerConfiguration.setRobot("test.xill");
        workerConfiguration.setThreadCount(10);

        Stage stage = stageFactory.build(workerConfiguration, context());

        assertEquals(stage.getWorkers().length, 10);
    }
}