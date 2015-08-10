package nl.xillio.xill.plugins.database.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;

import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.database.util.Database;
import nl.xillio.xill.plugins.database.util.Tuple;

import org.apache.commons.lang3.tuple.Pair;

@SuppressWarnings("unchecked")
public class DatabaseServiceImpl implements DatabaseService {

	/**
	 * Constructor loading the necessary JDBC drivers
	 * 
	 * @throws ClassNotFoundException
	 *         If a driver cannot be found
	 */
	public DatabaseServiceImpl() throws ClassNotFoundException {
		// Load JDBC drivers
		for (Database db : Database.values())
			Class.forName(db.getDriverClass());
	}

	@Override
	public Connection connect(String url) {
		try {
			return DriverManager.getConnection(url);
		} catch (SQLException e) {
			throw new RobotRuntimeException("Could not connect to database", e);
		}
	}

	@Override
	public Connection connect(String url, Properties properties) {
		try {
			return DriverManager.getConnection(url, properties);
		} catch (SQLException e) {
			throw new RobotRuntimeException("Could not connect to database", e);
		}
	}

	@Override
	public String createMysqlURL(String database, String user, String pass, Tuple<String, String>... options) {
		return createJDBCURL("mysql", database, user, pass);
	}

	@Override
	public String createOracleURL(String database, String user, String pass) {
		if (user == null ^ pass == null)
		  throw new IllegalArgumentException("User and pass should be both null or both non-null");
		if (user != null && pass != null)
			// prepend username and password
			return String.format("jdbc:oracle:thin:%s/%s@//%s:%s/%s", user, pass, database);
		else
			// url without username and password
			return String.format("jdbc:oracle:thin:@//%s:%s/%s", database);
	}

	@Override
	public Properties createOracleOptions(Tuple<String, String>... options) {
		Properties properties = new Properties();
		Arrays.stream(options).forEach(p -> properties.put(p.getKey(), p.getValue()));
		return properties;
	}

	@Override
	public String createMssqlURL(String database, String user, String pass, Tuple<String, String>... options) {
		return createJDBCURL("mssql", database, user, pass);
	}

	@Override
	public String createSqliteURL(String file) {
		String path = file == null ? ":memory:" : file;
		return String.format("jdbc:sqlite:%s", path);
	}

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
