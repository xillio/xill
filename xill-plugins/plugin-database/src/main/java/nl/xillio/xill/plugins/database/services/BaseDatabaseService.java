package nl.xillio.xill.plugins.database.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.database.util.Tuple;

import org.apache.commons.lang3.tuple.Pair;

@SuppressWarnings("unchecked")
public abstract class BaseDatabaseService implements DatabaseService {

	/**
	 * Create a JDBC {@link Connection} to the database at the given URL
	 * 
	 * @param url
	 *        URL to connect to
	 * @return A connection ready to execute queries on
	 */
	protected Connection connect(String url) {
		try {
			return DriverManager.getConnection(url);
		} catch (SQLException e) {
			throw new RobotRuntimeException("Could not connect to database", e);
		}
	}

	/**
	 * Create a JDBC {@link Connection} to the database at the given URL, using the given properties. Especially useful for connecting to an Oracle database.
	 * 
	 * @param url
	 *        URL to connect to
	 * @param properties
	 *        Separate properties object
	 * @return A connection ready to execute queries on
	 * 
	 */
	protected Connection connect(String url, Properties properties) {
		try {
			return DriverManager.getConnection(url, properties);
		} catch (SQLException e) {
			throw new RobotRuntimeException("Could not connect to database", e);
		}
	}

	/**
	 * Create a connection string for a JDBC connection
	 * 
	 * @param database
	 *        Database name
	 * @param user
	 *        Username
	 * @param pass
	 *        Password
	 * @param options
	 *        JDBC specific options
	 * @return Connection String
	 */
	protected abstract String createConnectionURL(String database, String user, String pass, Tuple<String, String>... options);

	public abstract void loadDriver() throws ClassNotFoundException;

	// @Override
	// public String createMysqlURL(String database, String user, String pass, Tuple<String, String>... options) {
	// return createJDBCURL("mysql", database, user, pass);
	// }
	//
	// @Override
	// public String createOracleURL(String database, String user, String pass) {
	// if (user == null ^ pass == null)
	// throw new IllegalArgumentException("User and pass should be both null or both non-null");
	// if (user != null && pass != null)
	// // prepend username and password
	// return String.format("jdbc:oracle:thin:%s/%s@//%s:%s/%s", user, pass, database);
	// else
	// // url without username and password
	// return String.format("jdbc:oracle:thin:@//%s:%s/%s", database);
	// }
	//
	// @Override
	// public Properties createOracleOptions(Tuple<String, String>... options) {
	// Properties properties = new Properties();
	// Arrays.stream(options).forEach(p -> properties.put(p.getKey(), p.getValue()));
	// return properties;
	// }
	//
	// @Override
	// public String createMssqlURL(String database, String user, String pass, Tuple<String, String>... options) {
	// return createJDBCURL("mssql", database, user, pass);
	// }
	//
	// @Override
	// public String createSqliteURL(String file) {
	//
	// }

	/**
	 * 
	 * @param type
	 *        JDBC name of the database
	 * @param database
	 *        host, port and database name in the correct format for the JDBC driver
	 * @param user
	 *        can be null
	 * @param pass
	 *        can be null
	 * @param options
	 *        Driver specific options
	 * @return
	 */
	private String createJDBCURL(String type, String database, String user, String pass, Pair<String, String>... options) {
		String url = String.format("jdbc:%s//%s", type, database, user, pass);
		// no other parameters, so return
		if (user == null && pass == null && options.length == 0)
		  return url;
		// question mark for url parameters
		url = url.concat("?");
		if (user == null ^ pass == null)
		  throw new IllegalArgumentException("User and pass should be both null or both non-null");
		if (user != null && pass != null)
		{
			// append username and password options
			url = String.format("%susername=%s&", url, user);
			url = String.format("%spassword=%s&", pass);
		}
		// append other options
		for (Pair<String, String> option : options) {
			url = String.format("%s%s=%s&", url, option.getKey(), option.getValue());
		}
		return url;
	}

}
