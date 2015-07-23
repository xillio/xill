package nl.xillio.xill.api.construct;

import org.apache.logging.log4j.Logger;

import nl.xillio.xill.api.RobotAppender;
import nl.xillio.xill.api.components.RobotID;

/**
 * This class represents a context in which a construct can be processed.
 */
public class ConstructContext {
	private final RobotID robotID;
	private Logger robotLogger;
	private final RobotID rootRobot;
	private Logger rootLogger;

	/**
	 * Create a new {@link ConstructContext} for a specific robot
	 *
	 * @param robot
	 *        the robotID of the current robot
	 * @param rootRobot
	 *        the robotID of the root robot
	 * @param contstruct
	 *        the construct that will be using this context
	 */
	public ConstructContext(final RobotID robot, final RobotID rootRobot, final Construct contstruct) {
		robotID = robot;
		this.rootRobot = rootRobot;
	}

	/**
	 * @return the robotID
	 */
	public RobotID getRobotID() {
		return robotID;
	}

	/**
	 * @return the robotID for the Root Robot
	 */
	public RobotID getRootRobot() {
		return rootRobot;
	}

	/**
	 * @return the robotLogger
	 */
	public Logger getLogger() {

		// Make sure the logger is set
		if (robotLogger == null) {
			robotLogger = RobotAppender.getLogger(robotID);
		}

		return robotLogger;
	}

	/**
	 * @return the root RobotAppender
	 */
	public Logger getRootLogger() {

		// Make sure the logger is set
		if (rootLogger == null) {
			rootLogger = RobotAppender.getLogger(rootRobot);
		}

		return rootLogger;
	}
}
