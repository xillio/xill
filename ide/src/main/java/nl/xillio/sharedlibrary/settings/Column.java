package nl.xillio.sharedlibrary.settings;

public class Column {

	private final String name;
	private final String sqlType;
	private final Class<?> dataType;
	private final boolean isKey;
	private final boolean isRequired;

	/**
	 * Define a new column for the settings table
	 *
	 * @param name
	 *        Name of the column
	 * @param sqlType
	 *        SQL type of the column used for table creation (e.g. VARCHAR(255)).
	 * @param dataType
	 *        Datatype of the column e.g. String, Integer, Boolean
	 * @param isKey
	 *        True if the column should be indexed.
	 * @param isRequired
	 *        True if the column is not required for updates
	 */
	public Column(final String name, final String sqlType, final Class<?> dataType, final boolean isKey, final boolean isRequired) {
		this.name = name;
		this.sqlType = sqlType;
		this.dataType = dataType;
		this.isKey = isKey;
		this.isRequired = isRequired;
	}

	public String getName() {
		return name;
	}

	public String getSQLType() {
		return sqlType;
	}

	public boolean isKey() {
		return isKey;
	}

	public boolean isRequired() {
		return isRequired;
	}

	public Class<?> getDataType() {
		return dataType;
	}

	@Override
	public String toString() {
		return getName() + "(isKey: " + isKey() + ", isRequired: " + isRequired() + ")";
	}
}
