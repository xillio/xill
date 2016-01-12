package test.integration;


import nl.xillio.xill.RobotIntegrationTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URL;

public class Excel extends RobotIntegrationTest {
    @DataProvider(name = "myRobots")
    @Override
    public Object[][] getRobots() {
        return super.getRobots();
    }

    @Test(dataProvider = "myRobots")
    @Override
    public void runRobot(URL robot, String name) throws IOException {
        super.runRobot(robot, name);
    }
}
