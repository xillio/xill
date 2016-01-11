package test.integration;


import nl.xillio.xill.RobotIntegrationTest;

import java.net.URL;

public class Robot extends RobotIntegrationTest {

    @Override
    protected URL getRobot() {
        return getClass().getResource("/tests/date.xill");
    }
}
