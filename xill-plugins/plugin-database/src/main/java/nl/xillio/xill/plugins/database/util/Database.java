package nl.xillio.xill.plugins.database.util;

public enum Database {
	ORACLE("oracle", "oracle.jdbc.OracleDriver"), MSSQL("mssql", "net.sourceforge.jtds.jdbc.Driver"), MYSQL("mysql", "org.mariadb.jdbc.Driver"), SQLITE("sqlite", "org.sqlite.JDBC");

	private String name;
	private String driverClass;

	private Database(String name, String driverClass) {
		this.name = name;
		this.driverClass = driverClass;
	}

	public String getName() {
		return name;
	}

	public String getDriverClass() {
		return driverClass;
	}
}
