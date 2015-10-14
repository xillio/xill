package nl.xillio.xill.util.settings;

/**
 * Class that is main point for dealing with settings in Content Tools 
 * It encapsulates all settings handlers.
 * 
 * @author Zbynek Hochmann
 */
public class SettingsHandler {

	public static String LAYOUT = "Layout";

	private static String FILENAME = "xilliosettings.cfg";

	private ContentHandlerImpl content;
	private static SettingsHandler settings = new SettingsHandler();
	private SimpleSettingsHandler simple;
	private ProjectSettingsHandler project;

	/**
	 * @return The instance of settings handler
	 */
	public static SettingsHandler getSettingsHandler() {
		return settings;
	}

	private SettingsHandler() {// singleton class
		this.content = new ContentHandlerImpl(FILENAME);
		try {
			this.content.init();
		} catch (Exception e) {
			System.err.println("Cannot initialize settings handler for reason: " + e.getMessage());
		}
		
		this.simple = new SimpleSettingsHandler(this.content);
		this.project = new ProjectSettingsHandler(this.content);
	}

	/**
	 * @return The implementation of simple variable settings
	 */
	public SimpleSettingsHandler simple() {
		return this.simple;
	}

	/**
	 * @return The implementation of project settings
	 */
	public ProjectSettingsHandler project() {
		return this.project;
	}
}
