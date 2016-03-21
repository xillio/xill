package nl.xillio.xill.plugins.concurrency.services;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.XillEnvironment;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Robot;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.plugins.concurrency.data.Worker;
import nl.xillio.xill.plugins.concurrency.data.WorkerConfiguration;
import nl.xillio.xill.plugins.concurrency.data.XillQueue;
import nl.xillio.xill.services.files.FileResolver;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;


public class WorkerThreadFactoryTest extends TestUtils {
    @Test
    public void testSuccessfulBuild() throws IOException {
        // Mock the dependencies
        XillEnvironment environment = mock(XillEnvironment.class, RETURNS_DEEP_STUBS);
        FileResolver fileResolver = mock(FileResolver.class);

        WorkerThreadFactory workerThreadFactory = new WorkerThreadFactory(environment, fileResolver);

        // Create input
        WorkerConfiguration configuration = new WorkerConfiguration();
        ConstructContext constructContext = context(mock(Construct.class));
        Robot robot = new nl.xillio.xill.components.Robot(RobotID.dummyRobot(), null, null, null, null);
        when(environment.buildProcessor(any(), any(), any()).getRobot()).thenReturn(robot);

        Worker worker = workerThreadFactory.build(configuration, constructContext, 12342, mock(XillQueue.class));

        MetaExpression argument = robot.getArgument();
        Map<String, MetaExpression> internalMap = argument.getValue();

        // Check if the thread id is in there
        assertEquals(internalMap.get("threadId").getNumberValue().intValue(), 12342);
        // Check if an output queue is in there
        assertTrue(internalMap.get("output").hasMeta(XillQueue.class));
    }
}