package nl.xillio.sharedlibrary.settings;

import java.util.LinkedList;

public class ProjectSetting extends Setting {
	public static ProjectSettingType PROJECT_SETTINGTYPE = new ProjectSettingType();
	private Setting setting = this;

	/**
	 * Constructor for registering a setting.
	 *
	 * @param category
	 * @param name
	 * @param folder
	 * @param description
	 */
	public ProjectSetting(final String name, final String folder, final String description) {
		super(PROJECT_SETTINGTYPE);
		setName(name);
		setFolder(folder);
		setDescription(description);
	}

	public ProjectSetting(final Setting<ProjectSettingType> setting) {
		super(PROJECT_SETTINGTYPE);
		this.setting = setting;
	}

	public String getName() {
		return (String) setting.getValue("name");
	}

	public void setName(final String name) {
		setting.setValue("name", name);
	}

	public String getFolder() {
		return (String) setting.getValue("folder");
	}

	public void setFolder(final String folder) {
		setting.setValue("folder", folder);
	}

	public String getDescription() {
		return (String) setting.getValue("description");
	}

	public void setDescription(final String description) {
		setting.setValue("description", description);
	}

	public static class ProjectSettingType implements SettingType {
		private static final String type = "project";
		private static final String key = "name";
		private final LinkedList<Column> columns = new LinkedList<Column>();

		public ProjectSettingType() {
			columns.add(new Column("name", "CHAR(255)", String.class, true, true));
			columns.add(new Column("folder", "TEXT", String.class, false, true));
			columns.add(new Column("description", "TEXT", String.class, false, false));
		}

		@Override
		public String getType() {
			return type;
		}

		@Override
		public LinkedList<Column> getColumns() {
			return columns;
		}

		@Override
		public String getKeyColumn() {
			return key;
		}

	}
}
