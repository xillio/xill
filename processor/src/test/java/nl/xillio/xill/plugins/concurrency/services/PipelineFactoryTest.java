package nl.xillio.xill.plugins.concurrency.services;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.concurrency.data.Pipeline;
import nl.xillio.xill.plugins.concurrency.data.WorkerConfiguration;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;


public class PipelineFactoryTest extends TestUtils {

    @Test
    public void testNormalFlow() {
        PipelineFactory pipelineFactory = new PipelineFactory(new WorkerConfigurationFactory());

        Pipeline pipeline = pipelineFactory.build(list(
                map(
                        "robot", "MyRobot.xill"
                ),
                map(
                        "robot", "MyOtherRobot.xill"
                )
        ).getValue());

        WorkerConfiguration[] configurationList = pipeline.getConfiguration();
        assertEquals(configurationList.length, 2);

        for (WorkerConfiguration item : configurationList) {
            assertNotNull(item);
        }
    }

    @Test(expectedExceptions = RobotRuntimeException.class)
    public void testEmptyInput() {
        new PipelineFactory(null).build(new ArrayList<>());
    }

}