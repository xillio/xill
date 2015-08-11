package nl.xillio.xill.plugins.database.util;

import java.util.Arrays;
import java.util.Optional;

import nl.xillio.xill.plugins.database.services.BaseDatabaseService;
import nl.xillio.xill.plugins.database.services.MssqlDatabaseServiceImpl;
import nl.xillio.xill.plugins.database.services.MysqlDatabaseServiceImpl;
import nl.xillio.xill.plugins.database.services.OracleDatabaseServiceImpl;
import nl.xillio.xill.plugins.database.services.SQLiteDatabaseServiceImpl;

public enum Database {
	ORACLE("oracle", "oracle.jdbc.OracleDriver", OracleDatabaseServiceImpl.class),
	MSSQL("mssql", "net.sourceforge.jtds.jdbc.Driver", MssqlDatabaseServiceImpl.class),
	SQLITE("sqlite", "org.sqlite.JDBC", SQLiteDatabaseServiceImpl.class),
	MYSQL("mysql", "com.mysql.jdbc.Driver", MysqlDatabaseServiceImpl.class);

	private String name;
	private String driverClass;
	private Class<? extends BaseDatabaseService> serviceClass;

	private Database(String name, String driverClass, Class<? extends BaseDatabaseService> serviceClass) {
		this.name = name;
		this.driverClass = driverClass;
		this.serviceClass = serviceClass;
	}

	public String getName() {
		return name;
	}

	public String getDriverClass() {
		return driverClass;
	}

	public Class<? extends BaseDatabaseService> getServiceClass() {
		return serviceClass;
	}

	/**
	 * 
	 * @param name
	 *        The name of the {@link Database}
	 * @return The service belonging to the DBMS with the given name
	 */
	public static Class<? extends BaseDatabaseService> findServiceClass(String name) {
		Optional<Database> db = Arrays.stream(values()).filter((d) -> d.getName().equals(name)).findAny();
		if (!db.isPresent())
		  throw new IllegalArgumentException("DBMS type is not supported");
		return db.get().getServiceClass();
	}
}
