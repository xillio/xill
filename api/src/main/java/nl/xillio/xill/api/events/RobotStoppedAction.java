package nl.xillio.xill.api.events;

import nl.xillio.xill.api.components.Robot;

import java.util.UUID;

/**
 * This class represents the object passed to all listeners when a robot is
 * stopped
 */
public class RobotStoppedAction {

    private final Robot robot;
    private final UUID compilerSerialID;

    /**
     * Create a new {@link RobotStoppedAction}
     *
     * @param robot the robot that stopped
     */
    public RobotStoppedAction(final Robot robot, final UUID compilerSerialID) {
        this.robot = robot;
        this.compilerSerialID = compilerSerialID;
    }

    public UUID getCompilerSerialID() {
        return compilerSerialID;
    }
}
