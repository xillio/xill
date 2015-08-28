package nl.xillio.xill.plugins.database.util;

import java.util.Arrays;
import java.util.Optional;

import nl.xillio.xill.plugins.database.services.BaseDatabaseService;
import nl.xillio.xill.plugins.database.services.MssqlDatabaseServiceImpl;
import nl.xillio.xill.plugins.database.services.MysqlDatabaseServiceImpl;
import nl.xillio.xill.plugins.database.services.OracleDatabaseServiceImpl;
import nl.xillio.xill.plugins.database.services.SQLiteDatabaseServiceImpl;

/**
 * The database enum, used for identifying database types.
 */
public enum Database {
	/**
	 * The enum for the Oracle database.
	 */
	ORACLE("oracle", "oracle.jdbc.OracleDriver", OracleDatabaseServiceImpl.class),
	/**
	 * The enum for the MSsql database.
	 */
	MSSQL("mssql", "net.sourceforge.jtds.jdbc.Driver", MssqlDatabaseServiceImpl.class),
	/**
	 * The enum for the SQLite database.
	 */
	SQLITE("sqlite", "org.sqlite.JDBC", SQLiteDatabaseServiceImpl.class),
	/**
	 *The enum for the Mysql database.  
	 */
	MYSQL("mysql", "com.mysql.jdbc.Driver", MysqlDatabaseServiceImpl.class);

	private String name;
	private String driverClass;
	private Class<? extends BaseDatabaseService> serviceClass;

	Database(String name, String driverClass, Class<? extends BaseDatabaseService> serviceClass) {
		this.name = name;
		this.driverClass = driverClass;
		this.serviceClass = serviceClass;
	}


	/**
	 * @return
	 * 			Returns the name of the database.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 * 				Returns the driver class of the database.
	 */
	public String getDriverClass() {
		return driverClass;
	}

	/**
	 * @return
	 * 				Returns the service class of the database.
	 */
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
