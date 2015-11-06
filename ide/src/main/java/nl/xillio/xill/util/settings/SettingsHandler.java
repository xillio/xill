package nl.xillio.xill.util.settings;

/**
 * Class that is main point for dealing with settings in Xill IDE
 * It encapsulates all settings handlers.
 * 
 * @author Zbynek Hochmann
 */
public class SettingsHandler {

	private static String FILENAME = "xilliosettings.cfg";

	private ContentHandlerImpl content;
	private static SettingsHandler settings = new SettingsHandler();
	private SimpleVariableHandler simple;
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

		this.simple = new SimpleVariableHandler(this.content);
		this.project = new ProjectSettingsHandler(this.content);
	}

	/**
	 * @return The implementation of simple variable settings
	 */
	public SimpleVariableHandler simple() {
		return this.simple;
	}

	/**
	 * @return The implementation of project settings
	 */
	public ProjectSettingsHandler project() {
		return this.project;
	}

	/**
	 * It set the save mechanism (see {@link nl.xillio.xill.util.settings.ContentHandler#setManualCommit(boolean)}) 
	 * 
	 * @param manual true = manual commit, false = auto commit (default)
	 */
	public void setManualCommit(boolean manual) {
		try {
			this.content.setManualCommit(manual);
		} catch (Exception e) {
			System.err.println("Cannot set manual commit for reason: " + e.getMessage());
		}
	}

	/**
	 * It save all changes from last commit() if manual commit is on (see {@link nl.xillio.xill.util.settings.ContentHandler#commit()})
	 */
	public void commit() {
		try {
			this.content.commit();
		} catch (Exception e) {
			System.err.println("Cannot do commit for reason: " + e.getMessage());
		}
	}
}
