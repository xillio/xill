package nl.xillio.xill.util.settings;

public class ProjectSetting {
	
	private String name;
	private String folder;
	private String description;
	//private ContentHandler content;
	
	public ProjectSetting(final String name, final String folder, final String description/*, final ContentHandler content*/) {
		this.name = name;
		this.folder = folder;
		this.description = description;
		//this.content = content;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getFolder() {
		return this.folder;
	}
	
	public String getDescription() {
		return this.description;
	}
}
