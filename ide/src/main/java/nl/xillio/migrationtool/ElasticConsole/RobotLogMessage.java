package nl.xillio.migrationtool.ElasticConsole;

/**
 * This class represents all information about a log event.
 */
public class RobotLogMessage {

	private final String level;
	private final String message;

	/**
	 * Creates a new log message.
	 *
	 * @param id
	 *        a robot it
	 * @param level
	 * @param message
	 *        the message to log
	 */
	public RobotLogMessage(final String level, final String message) {
		this.level = level;
		this.message = message;
	}

	/**
	 * Returns the level.
	 *
	 * @return the level
	 */
	public String getLevel() {
		return level;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
}
