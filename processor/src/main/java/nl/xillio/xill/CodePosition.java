package nl.xillio.xill;

import nl.xillio.xill.api.components.RobotID;

/**
 * This class represents a location in the source
 */
public class CodePosition {

    private final RobotID robot;
    private final int lineNumber;

    /**
     * @param robot
     * @param lineNumber
     */
    public CodePosition(final RobotID robot, final int lineNumber) {
        this.robot = robot;
        this.lineNumber = lineNumber;
    }

    /**
     * @return the lineNumber
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * @return the robot
     */
    public RobotID getRobotID() {
        return robot;
    }

    @Override
    public String toString() {
        return robot.getPath().getAbsolutePath() + ":" + lineNumber;
    }
}
