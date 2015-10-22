package nl.xillio.xill.util.settings;

/**
 * The class used just like simple read storage for one project settings
 */
public class ProjectSettings {

	private String name;
	private String folder;
	private String description;

	/**
	 * @param name Project name
	 * @param folder Project folder
	 * @param description Project description
	 */
	public ProjectSettings(final String name, final String folder, final String description) {
		this.name = name;
		this.folder = folder;
		this.description = description;
	}

	/**
	 * @return The project name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return The project folder
	 */
	public String getFolder() {
		return this.folder;
	}

	/**
	 * @return The project description
	 */
	public String getDescription() {
		return this.description;
	}
}