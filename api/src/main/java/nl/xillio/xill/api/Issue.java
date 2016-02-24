package nl.xillio.xill.api;

import nl.xillio.xill.api.components.RobotID;

/**
 * This class represents an issue with the code.
 */
public class Issue {
    private final String message;
    private final int line;
    private final Type severity;
    private final RobotID robot;

    /**
     * The severity of this issue.
     *
     * @see Type#ERROR
     * @see Type#WARNING
     * @see Type#INFO
     */
    public enum Type {
        /**
         * Can't compile.
         */
        ERROR,
        /**
         * Needs attention.
         */
        WARNING,
        /**
         * Friendly notice.
         */
        INFO
    }

    /**
     * Default constructor to create a new Issue.
     *
     * @param message  the message to display
     * @param line     the line where the issue occurred
     * @param severity the severity of the issue
     * @param robot    the robot in which the issue occures
     * @see Type
     */
    public Issue(final String message, final int line, final Type severity, final RobotID robot) {
        this.message = message;
        this.line = line;
        this.severity = severity;
        this.robot = robot;
    }

    /**
     * Returns the robot attached to this issue.
     *
     * @return the robot
     */
    public RobotID getRobot() {
        return robot;
    }

    /**
     * Return the issue's message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the line where the issue occured.
     *
     * @return the line
     */
    public int getLine() {
        return line;
    }

    /**
     * Returns the severity of the issue.
     *
     * @return the severity
     */
    public Type getSeverity() {
        return severity;
    }
}
