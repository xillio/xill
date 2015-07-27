package nl.xillio.sharedlibrary.settings;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.xillio.xill.util.db.Connection;
import nl.xillio.xill.util.db.Connector;
import nl.xillio.xill.util.db.DataBaseHandler;

public class SettingsHandler {
	private Logger log = LogManager.getLogger();
	private Connection database;

	private static String DBNAME = "xilliosettings.db";
	public static String SETTINGSDATABASE = "xilliodefaultsettings";

	private static SettingsHandler settingsHandler;

	public static SettingsHandler getSettingsHandler() {
		settingsHandler = new SettingsHandler(new File(DBNAME));
		return settingsHandler;
	}

	public static SettingsHandler getSettingsHandler(final File file) {
		settingsHandler = new SettingsHandler(file);
		return settingsHandler;
	}

	private SettingsHandler(final File databaseFile) {
		// Bootup the database connection
		Connector con = DataBaseHandler.getConnector("sqlite");
		String connectionString = con.makeConnectionString(null, 0, databaseFile.getAbsolutePath(), null, null, null);
		try {
			database = con.createConnection(connectionString);
			DataBaseHandler.registerConnection(SETTINGSDATABASE, database);
		} catch (SQLException e) {
			log.error("Could not register settingsfile: " + e.getMessage());
		}
	}

	public void registerLogger(final Logger logger) {
		log = logger;
	}

	/**
	 * Shorthand version to return the simple setting
	 *
	 * @param key
	 *        Name of the setting to be returned
	 * @return The value of the setting
	 */
	public String getSimpleSetting(final String key) {
		Setting<SettingType> result = getSetting(SimpleSetting.SIMPLE_SETTINGTYPE, key);
		if (result != null) {
			String ret;

			if (result.getValue("value") != null) {
				ret = unEscapeSQL(result.getValue("value").toString());
			} else {
				ret = (String) result.getValue("default");
			}

			Object enc = result.getValue("encrypted");
			if (enc != null && enc.toString().equals("1")) {
				return SimpleSetting.decrypt(ret);
			}
			return ret;

		}
		return null;
	}

	/**
	 * Fetch a list of all settings of the specified type
	 *
	 * @param <T>
	 * @param settingType
	 * @return
	 */
	public <T extends SettingType, S extends Setting<T>> List<S> getSettingsList(final T settingType) {
		List<S> settings = new LinkedList<>();

		database.connect();

		try {
			String query = "SELECT * FROM " + settingType.getType();
			ResultSet result = database.query(query);
			while (result.next()) {
				Setting<T> setting = new Setting<>(settingType);

				for (Column column : settingType.getColumns()) {
					setting.setValue(column.getName(), result.getObject(column.getName()));
				}
				settings.add((S) setting);
			}

		} catch (Exception e) {
			if (e.getMessage().contains("SQL error or missing database")) {
				try {
					createSettingTypeTable(settingType);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} finally {
			database.close();
		}

		return settings;
	}

	/**
	 * Fetch a setting of the indicated type
	 *
	 * @param settingType
	 *        Type of the setting to be fetched
	 * @param key
	 *        Name of the setting
	 * @return The value(s) of the setting
	 */
	public Setting<SettingType> getSetting(final SettingType settingType, String key) {
		database.connect();
		Setting<SettingType> setting = new Setting<>(settingType);
		key = escapeSQL(key);
		try {
			if (settingType.getKeyColumn() != null) {
				ResultSet result = database.query("SELECT * FROM " + settingType.getType() + " WHERE " + settingType.getKeyColumn() + " = '" + key + "'");
				if (result.next()) {
					for (Column column : settingType.getColumns()) {
						Object v = result.getObject(column.getName());
						if (v instanceof String) {
							setting.setValue(column.getName(), unEscapeSQL(v.toString()));
						} else {
							setting.setValue(column.getName(), result.getObject(column.getName()));
						}
					}
				} else {
					return null;
				}
			}

		} catch (Exception e) {
			return null;
		} finally {
			database.close();
		}
		return setting;
	}

	/**
	 * Shorthand function to directly update a simple setting
	 *
	 * @param key
	 *        Name of the setting
	 * @param value
	 *        Value of the setting
	 */
	public void saveSimpleSetting(final String key, final String value) {
		Setting<SettingType> setting = getSetting(SimpleSetting.SIMPLE_SETTINGTYPE, key);
		Object v = null;
		if (setting != null) {
			v = setting.getValue("encrypted");
		}
		setting = new SimpleSetting(key, value, v != null && v.toString().equals("1"));
		saveSetting(setting, false, true);
	}

	/**
	 * Saves the setting. Equivalent for saveSetting(setting, false)
	 *
	 * @param setting
	 */
	public void saveSetting(final Setting setting) {
		saveSetting(setting, false, true);
	}

	/**
	 * Save the provided setting to the database
	 *
	 * @param setting
	 *        Setting containing key and values
	 * @param setOptional
	 *        If set to true, also the optional fields will be set.
	 */
	public void saveSetting(final Setting setting, final boolean setOptional, final boolean overwrite) {
		SettingType settingType = setting.getType();
		String key = settingType.getKeyColumn();

		database.connect();
		try {

			// Verify table exists
			String query_tableexists = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + settingType.getType() + "'";
			ResultSet result = database.query(query_tableexists);
			if (!result.next()) {
				createSettingTypeTable(settingType);
			}

			// INSERT or UPDATE
			Column keyColumn = getColumnByName(settingType.getColumns(), key);
			if (keyColumn != null) {

				// Check if row already exists
				String query_rowexists = "SELECT * FROM '" + settingType.getType() + "' WHERE [" + key + "] = " + valueToString(setting.getValue(key));
				boolean rowexists = database.query(query_rowexists).next();
				if (rowexists && overwrite) {
					// UPDATE

					// Generate value list
					String pairs = "";
					for (Column column : settingType.getColumns()) {
						String cname = column.getName();
						String val = valueToString(setting.getValue(cname));

						if (column.isRequired() || setOptional) {
							pairs += ", [" + escapeSQL(cname) + "] = " + val;
						}
					}

					// Perform query
					if (pairs.length() > 0) {
						pairs = pairs.substring(2);
						String query = "UPDATE " + settingType.getType() + " SET " + pairs + " WHERE [" + key + "] = " + valueToString(setting.getValue(key));
						database.query(query);
					}

				} else if (!rowexists) {
					// INSERT

					// Generate value list
					String fields = "", values = "";
					for (Column column : settingType.getColumns()) {
						String cname = column.getName();
						String val = valueToString(setting.getValue(cname));

						if (column.isRequired() || setOptional) {
							fields += ", [" + escapeSQL(cname) + "]";
							values += ", " + val;
						}
					}

					// Perform query
					if (fields.length() > 0) {
						fields = fields.substring(2);
						values = values.substring(2);
						String query = "INSERT INTO " + settingType.getType() + " (" + fields + ") VALUES (" + values + ")";
						database.query(query);
					}
				}
			}

		} catch (SQLException e) {
			log.error("Failed to save setting '" + setting.getValue(setting.getType().getKeyColumn()) + "': " + e.getMessage());
		} finally {
			database.close();
		}
	}

	private static String valueToString(final Object value) {
		String val = "";
		if (value instanceof Integer || value instanceof Boolean) {
			val = value.toString();
		} else if (value == null) {
			val = "NULL";
		} else {
			val = "'" + escapeSQL(value.toString()) + "'";
		}
		return val;
	}

	/**
	 * Remove the specified setting
	 *
	 * @param settingType
	 * @param key
	 */
	public void removeSetting(final SettingType settingType, final String key) {
		database.connect();
		try {
			String query_delete = "DELETE FROM '" + settingType.getType() + "' WHERE " + settingType.getKeyColumn() + "='" + key + "'";
			database.query(query_delete);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			database.close();
		}
	}

	/**
	 * Register an unencrypted simple setting
	 *
	 * @param category
	 * @param key
	 * @param defaultvalue
	 * @param description
	 */
	public void registerSimpleSetting(final String category, final String key, final String defaultvalue, final String description) {
		registerSimpleSetting(category, key, defaultvalue, description, false);
	}

	/**
	 * Register the simple setting
	 *
	 * @param category
	 * @param key
	 * @param defaultvalue
	 * @param description
	 */
	public void registerSimpleSetting(final String category, final String key, final String defaultvalue, final String description, final boolean isencrypted) {
		saveSetting(new SimpleSetting(category, key, defaultvalue, description, isencrypted), true, false);
	}

	public void resetSimpleSetting(final SimpleSetting setting) {
		setting.setValue(setting.getKey(), setting.getValue("default"));
	}

	private void createSettingTypeTable(final SettingType settingType) throws SQLException {
		database.connect();

		try {
			// Create new table
			String query_createtable = "CREATE TABLE [" + settingType.getType() + "] (";
			for (Column c : settingType.getColumns()) {
				query_createtable += "\n[" + c.getName() + "] " + c.getSQLType() + ", ";
			}
			if (query_createtable.endsWith(", ")) {
				query_createtable = query_createtable.substring(0, query_createtable.length() - 2);
			}
			query_createtable += "\n);";
			database.query(query_createtable);

			// Add indexes
			for (Column c : settingType.getColumns()) {
				if (c.isKey()) {
					String query_createindexes = "CREATE INDEX [" + c.getName() + "] ON [" + settingType.getType() + "] ([" + c.getName() + "]);";
					database.query(query_createindexes);
				}
			}
		} catch (SQLException e) {

		} finally {
			database.close();
		}
	}

	private static String escapeSQL(final String value) {
		String clean_string = value;
		/*
		 * clean_string = clean_string.replaceAll("\\\\", "\\\\\\\\");
		 * clean_string = clean_string.replaceAll("\\n","\\\\n");
		 * clean_string = clean_string.replaceAll("\\r", "\\\\r");
		 * clean_string = clean_string.replaceAll("\\t", "\\\\t");
		 * clean_string = clean_string.replaceAll("\\00", "\\\\0");
		 * clean_string = clean_string.replaceAll("\\\"", "\\\\\"");
		 */
		clean_string = clean_string.replaceAll("'", "''");
		return clean_string;
	}

	private static String unEscapeSQL(final String value) {
		String clean_string = value;
		clean_string = clean_string.replace("''", "'");
		return clean_string;
	}

	private static Column getColumnByName(final LinkedList<Column> columns, final String name) {
		for (Column column : columns) {
			if (column.getName().equals(name)) {
				return column;
			}
		}
		return null;
	}
}
