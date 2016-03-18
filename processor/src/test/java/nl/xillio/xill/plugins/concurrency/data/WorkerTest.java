package nl.xillio.xill.plugins.concurrency.data;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.Xill;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.NullDebugger;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Robot;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import org.testng.annotations.Test;

import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertSame;


public class WorkerTest extends TestUtils {

    @Test
    public void testRunNoErrors() throws Exception {
        Robot robot = mock(Robot.class);
        XillQueue xillQueue = mock(XillQueue.class);
        Worker worker = new Worker(robot, new NullDebugger(), xillQueue);
        worker.run();

        // Check if robot was processed
        verify(robot).process(any(NullDebugger.class));

        // And if the queue was closed
        verify(xillQueue).close();
    }

    @Test
    public void testRunWithException() {

        Robot robot = mock(Robot.class);
        when(robot.process(any())).thenThrow(new RobotRuntimeException("There was an error"));
        XillQueue xillQueue = mock(XillQueue.class);

        Debugger debugger = mock(Debugger.class);

        Worker worker = new Worker(robot, debugger, xillQueue);
        worker.run();

        // Check if the exception was forwarded to the debugger
        verify(debugger).handle(any(RobotRuntimeException.class));
        // And the queue was closed even though there was an error
        verify(xillQueue).close();
    }

    @Test
    public void testSetInputWorker() throws Exception {
        XillQueue xillQueue = mock(XillQueue.class);

        Robot robot = new nl.xillio.xill.components.Robot(RobotID.dummyRobot(), mock(Debugger.class), null, null, null);
        MetaExpression argument = emptyObject();
        robot.setArgument(argument);

        Worker workerA = new Worker(null, null, xillQueue);
        Worker workerB = new Worker(robot, null, null);

        workerB.setInputWorker(workerA);

        Map<String, MetaExpression> internalMap = argument.getValue();

        // Check if the input was added
        assertSame(internalMap.get("input").getMeta(XillQueue.class), xillQueue);

    }
}