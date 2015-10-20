package nl.xillio.xill.plugins.system.exec;

import java.io.File;

/**
 * This class represents the information requried by the {@link ProcessFactory} to build a process
 */
public class ProcessDescription {
	private static final String DEFAULT_LABEL = "System.exec";

	private final String[] commands;
	private final File workdingDirectory;
	private String friendlyName = DEFAULT_LABEL;

	/**
	 * Create a new {@link ProcessDescription}
	 *
	 * @param workdingDirectory
	 *        the working directory or null
	 * @param commands
	 *        the commands to run
	 */
	public ProcessDescription(final File workdingDirectory, final String... commands) {
		this.workdingDirectory = workdingDirectory;
		this.commands = commands;
	}

	/**
	 * @return the commands
	 */
	public String[] getCommands() {
		return commands;
	}

	/**
	 * @return the workdingDirectory
	 */
	public File getWorkdingDirectory() {
		return workdingDirectory;
	}

	/**
	 * @return the friendlyName
	 */
	public String getFriendlyName() {
		return friendlyName;
	}

	/**
	 * @param friendlyName
	 *        the friendlyName to set
	 */
	public void setFriendlyName(final String friendlyName) {
		this.friendlyName = friendlyName;
	}
}
