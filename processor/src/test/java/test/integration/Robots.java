package test.integration;


import nl.xillio.xill.RobotIntegrationTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.URL;

public class Robots extends RobotIntegrationTest {
    @DataProvider(name = "myRobots")
    @Override
    public Object[][] getRobots() {
        return super.getRobots();
    }

    @Test(dataProvider = "myRobots")
    @Override
    public void runRobot(URL robot) throws Exception {
        super.runRobot(robot);
    }
}
