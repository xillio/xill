package nl.xillio.xill.util.settings;

public class SettingsHandler {

	public static String LAYOUT = "Layout";
	
	private static String FILENAME = "xilliosettings.cfg";

	private ContentHandlerImpl content;
	private static SettingsHandler settings = new SettingsHandler();
	private SimpleSettingsHandler simple;
	private ProjectSettingsHandler project;
	
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
	
	public SimpleSettingsHandler simple() {
		return this.simple;
	}
	
	public ProjectSettingsHandler project() {
		return this.project;
	}
}
