package nl.xillio.xill.api.errors;

import nl.xillio.xill.api.components.RobotID;

/**
 * This {@link Exception} is generally thrown when the {@link XillProgramFactory} was unable to generate a program tree from the provide token tree.
 */
public class XillParsingException extends Exception {
	private static final long serialVersionUID = 62988736232220717L;
	private final int line;
	private final RobotID robot;

	/**
	 * Create a {@link XillParsingException} with a message
	 *
	 * @param message
	 * @param line
	 * @param robot
	 */
	public XillParsingException(final String message, final int line, final RobotID robot) {
		super(message);
		this.line = line;
		this.robot = robot;
	}

	/**
	 * Create a {@link XillParsingException} with a cause
	 *
	 * @param message
	 * @param line
	 * @param robot
	 * @param e
	 */
	public XillParsingException(final String message, final int line, final RobotID robot, final Exception e) {
		super(message, e);
		this.line = line;
		this.robot = robot;
	}

	/**
	 * @return the line
	 */
	public int getLine() {
		return line;
	}

	/**
	 * @return the robot
	 */
	public RobotID getRobot() {
		return robot;
	}

}
