package nl.xillio.xill.util.settings;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class SimpleSettingsHandler {

	private static final String NAME = "name";
	private static final String KEYNAME = NAME;
	private static final String VALUE = "value";
	private static final String DEFAULT = "default";
	private static final String DESCRIPTION = "description";
	private static final String ENCRYPTED = "encrypted";

	private ContentHandler content;
	
	SimpleSettingsHandler(ContentHandler content) {
		this.content = content;
	}
	
	public void save(final String category, final String name, final String value) {
		this.save(category, name, value, false);
	}
	
	public void save(final String category, final String name, final String value, final boolean allowUnregistered) {
		try {
			HashMap<String, Object> itemContent = new HashMap<>();
			String valueStr = value;
			
			if (!this.content.exist(category, KEYNAME, name)) {
				// item does not exist!
				if (allowUnregistered) {
					// need to add name (as a key)
					itemContent.put(NAME, name);
				} else {
					throw new Exception(String.format("Settings [%1$s] has not been registered", name));
				}
			} else {
				Object o = this.content.get(category, name).get(ENCRYPTED);
				if ((o != null) && (o.toString().equals("1"))) {
					valueStr = ContentHandlerImpl.encrypt(value);
				}
			}

			itemContent.put(VALUE, valueStr);

			this.content.set(category, itemContent, KEYNAME, name);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public void register(final String category, final String name, final String defaultValue, final String description, final boolean encrypted) {
		try {
			HashMap<String, Object> itemContent = new HashMap<>();
			itemContent.put(NAME, name);
			itemContent.put(DEFAULT, defaultValue);
			itemContent.put(DESCRIPTION, description);
			itemContent.put(ENCRYPTED, encrypted ? "1" : "0");

			this.content.set(category, itemContent, KEYNAME, name);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	public void register(final String category, final String name, final String defaultValue, final String description) {
		this.register(category, name, defaultValue, description, false);
	}
	
	public String get(final String category, final String keyValue) {
		Object o = null;
		Map<String, Object> map;
		try {
			map = this.content.get(category, keyValue);
			if ((map == null) || (map.isEmpty())) {
				return null;
			}
			o = map.get(VALUE);
			if (o == null) {
				// no value defined - using default value
				o = map.get(DEFAULT);
				if (o == null) {
					throw new Exception("Invalid structure of settings file!");
				}
			}
			
			String result = o.toString(); 

			o = map.get(ENCRYPTED);
			if ((o != null) && (o.toString().equals("1"))) {
				result = ContentHandlerImpl.decrypt(result);
			}
			return result;

		} catch (Exception e) {
			System.err.println(e.getMessage());
			return null;
		}
	}
	
	public void delete(final String category, final String name) {
		try {
			this.content.delete(category, KEYNAME, name);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
}
