package nl.xillio.sharedlibrary.settings;

import java.util.Hashtable;

public class Setting<T extends SettingType> {
	private final T type;

	Hashtable<String, Object> values = new Hashtable<String, Object>();

	public Setting(final T type) {
		this.type = type;
	}

	public SettingType getType() {
		return type;
	}

	public Object getValue(final String name) {
		return values.get(name);
	}

	public void setValue(final String name, final Object value) {
		if (name != null && value != null) {
			values.put(name, value);
		}
	}

	@Override
	public String toString() {
		String ret = "Setting <" + type.getType() + "> {";
		for (String key : values.keySet()) {
			ret += key + ": " + "\"" + values.get(key) + "\",";
		}

		ret += "}";
		return ret;
	}

}
