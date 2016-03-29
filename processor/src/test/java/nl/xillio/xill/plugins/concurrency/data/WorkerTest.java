package nl.xillio.xill.plugins.concurrency.data;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.NullDebugger;
import nl.xillio.xill.api.components.Robot;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import org.testng.annotations.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


public class WorkerTest extends TestUtils {

    @Test
    public void testRunNoErrors() throws Exception {
        Robot robot = mock(Robot.class);
        Worker worker = new Worker(robot, new NullDebugger());
        worker.run();

        // Check if robot was processed
        verify(robot).process(any(NullDebugger.class));
    }

    @Test
    public void testRunWithException() {

        Robot robot = mock(Robot.class);
        when(robot.process(any())).thenThrow(new RobotRuntimeException("There was an error"));

        Debugger debugger = mock(Debugger.class);

        Worker worker = new Worker(robot, debugger);
        worker.run();

        // Check if the exception was forwarded to the debugger
        verify(debugger).handle(any(RobotRuntimeException.class));
    }
}