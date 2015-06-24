package nl.xillio.sharedlibrary.settings;

import java.util.LinkedList;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public class SimpleSetting extends Setting {
	public static SimpleSettingType SIMPLE_SETTINGTYPE = new SimpleSettingType();

	private static String DEFAULTPW = "6WyMNf99H32Qn3ofZ32rxVNTXcd8sA6b";
	private static StandardPBEStringEncryptor encryptor;

	static {
		encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword(DEFAULTPW);
		encryptor.initialize();
	}

	/*
	 * Constructor for storing a setting
	 * 
	 * @param name
	 * 
	 * @param value
	 */
	public SimpleSetting(final String name, final String value, final boolean isencrypted) {
		super(SIMPLE_SETTINGTYPE);
		setValue("key", name);

		if (isencrypted) {
			setValue("value", value != null ? encryptor.encrypt(value) : null);
		} else {
			setValue("value", value);
		}
	}

	/**
	 * Constructor for registering a setting.
	 * 
	 * @param category
	 * @param key
	 * @param defaultvalue
	 * @param description
	 */
	public SimpleSetting(final String category, final String key, final String defaultvalue, final String description, final boolean isencrypted) {
		super(SIMPLE_SETTINGTYPE);

		setCategory(category);
		setKey(key);

		setDescription(description);
		setEncrypted(isencrypted);
		if (isencrypted) {
			setDefaultValue(defaultvalue != null ? encrypt(defaultvalue) : null);
		} else {
			setDefaultValue(defaultvalue);
		}
	}

	public String getKey() {
		return (String) getValue("key");
	}

	public void setKey(final String key) {
		setValue("key", key);
	}

	public void setCategory(final String category) {
		setValue("category", category);
	}

	public void setDescription(final String description) {
		setValue("description", description);
	}

	public void setDefaultValue(final String value) {
		setValue("default", value);
	}

	public void setEncrypted(final boolean isencrypted) {
		setValue("encrypted", isencrypted ? 1 : 0);
	}

	public boolean isEncrypted() {
		Object v = super.getValue("encrypted");
		if (v != null) {
			return v.toString().equals("1");
		}
		return false;
	}

	public Integer getIntegerValue() {
		Object value = super.getValue("integerValue");
		try {
			return Integer.parseInt(value.toString());
		} catch (Exception e) {}
		return null;
	}

	public Boolean getBooleanValue() {
		Object value = super.getValue("booleanValue");
		try {
			return Boolean.parseBoolean(value.toString());
		} catch (Exception e) {}
		return null;
	}

	public String getStringValue() {

		Object value = super.getValue("stringValue");
		if (value != null) {
			if (isEncrypted()) {
				return decrypt(value.toString());
			} else {
				return value.toString();
			}
		}
		return null;
	}

	public static synchronized String encrypt(final String value) {
		return encryptor.encrypt(value);
	}

	public static synchronized String decrypt(final String value) {
		return encryptor.decrypt(value);
	}

	private static class SimpleSettingType implements SettingType {
		private static final String type = "settings";
		private static final String key = "key";
		private final LinkedList<Column> columns = new LinkedList<Column>();

		public SimpleSettingType() {
			columns.add(new Column("key", "CHAR(255)", String.class, true, true));
			columns.add(new Column("value", "TEXT", String.class, false, true));
			columns.add(new Column("default", "TEXT", String.class, false, false));
			columns.add(new Column("category", "CHAR(255)", String.class, false, false));
			columns.add(new Column("description", "TEXT", String.class, false, false));
			columns.add(new Column("encrypted", "BOOLEAN", Boolean.class, false, false));
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
