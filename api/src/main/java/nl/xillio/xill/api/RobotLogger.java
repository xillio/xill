package nl.xillio.xill.api;

import org.apache.log4j.Logger;

import nl.xillio.xill.api.components.RobotID;

/**
 * This class is a wrapper which is used to get loggers for robots.
 */
public class RobotLogger {

	/**
	 * The prefix used to identify a virtual 'robot' package. This way log4j can use custom appenders for robots
	 */
	public static final String ROBOT_LOGGER_PREFIX = "robot.";

	/**
	 * Creates a new logger for a robot.
	 *
	 * @param id
	 *        the robot id
	 * @return the logger
	 */
	public static Logger getLogger(final RobotID id) {
		return Logger.getLogger(ROBOT_LOGGER_PREFIX + id.toString());
	}

}
