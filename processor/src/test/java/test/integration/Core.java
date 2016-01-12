package test.integration;


import nl.xillio.xill.RobotIntegrationTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.URL;

public class Core extends RobotIntegrationTest {
    @DataProvider(name = "myRobots")
    @Override
    public Object[][] getRobots() {
        return super.getRobots();
    }

    @Test(dataProvider = "myRobots")
    @Override
    public void runRobot(URL robot, String name) throws Exception {
        super.runRobot(robot, name);
    }
}
