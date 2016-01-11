package test.integration;


import nl.xillio.xill.RobotIntegrationTest;
import org.testng.annotations.Test;

public class Robot extends RobotIntegrationTest {

    @Test
    public void testRun() throws Exception {
        runRobot(getClass().getResource("/tests/date.xill"));
    }
}
