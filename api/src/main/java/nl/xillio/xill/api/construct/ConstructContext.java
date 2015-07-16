package nl.xillio.xill.api.construct;

import nl.xillio.xill.api.RobotLogger;
import nl.xillio.xill.api.components.RobotID;

import org.apache.log4j.Logger;

/**
 * This class represents a context in which a construct can be processed.
 */
public class ConstructContext {
	private final RobotID robotID;
	private Logger robotLogger;
	private RobotID rootRobot;
	private Logger rootLogger;

	/**
	 * Create a new {@link ConstructContext} for a specific robot
	 *
	 * @param robot the robotID of the current robot
	 * @param rootRobot the robotID of the root robot
	 * @param contstruct the construct that will be using this context
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
			robotLogger = RobotLogger.getLogger(robotID);
		}

		return robotLogger;
	}
	
	/**
	 * @return the root RobotLogger
	 */
	public Logger getRootLogger() {

		// Make sure the logger is set
		if (rootLogger == null) {
			rootLogger = RobotLogger.getLogger(rootRobot);
		}

		return rootLogger;
	}
}
