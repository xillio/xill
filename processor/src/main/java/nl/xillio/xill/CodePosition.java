package nl.xillio.xill;

import nl.xillio.xill.api.components.RobotID;

/**
 * This class represents a location in the source
 */
public class CodePosition {

    private final RobotID robot;
    private final int lineNumber;

    /**
     * Create a new {@link CodePosition}-object.
     *
     * @param robot         the robot from which we would like to know the position.
     * @param lineNumber    the line number on which code has stopped execution.
     */
    public CodePosition(final RobotID robot, final int lineNumber) {
        this.robot = robot;
        this.lineNumber = lineNumber;
    }

    /**
     * Get the line number of the robot.
     *
     * @return the lineNumber
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Get the robot.
     *
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
