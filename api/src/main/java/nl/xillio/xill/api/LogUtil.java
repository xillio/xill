package nl.xillio.xill.api;

import nl.xillio.xill.api.components.RobotID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logger class for
 *
 * Created by Anwar on 2/18/2016.
 */
public class LogUtil {
    private LogUtil() {
        // No LogUtil for you
    }

    public static final String ROBOT_LOGGER_PREFIX = "robot.";

    /**
     * Get a new logger instance associated with a specific robot ID
     *
     * @param robotID ID of a robot
     * @return        An instance of the logger.
     */
    public static Logger getLogger(final RobotID robotID) {
        return LoggerFactory.getLogger(ROBOT_LOGGER_PREFIX + robotID.toString());
    }
}