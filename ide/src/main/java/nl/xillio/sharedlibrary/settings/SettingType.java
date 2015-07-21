package nl.xillio.sharedlibrary.settings;

import java.util.LinkedList;

public interface SettingType {
	public String getType();

	public LinkedList<Column> getColumns();

	public String getKeyColumn();
}
