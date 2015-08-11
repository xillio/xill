package nl.xillio.xill.plugins.database.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import nl.xillio.xill.plugins.database.util.StatementIterator;
import nl.xillio.xill.plugins.database.util.Tuple;

@SuppressWarnings("unchecked")
public abstract class BaseDatabaseService implements DatabaseService {

	/**
	 * Create a JDBC {@link Connection} to the database at the given URL
	 * 
	 * @param url
	 *        URL to connect to
	 * @return A connection ready to execute queries on
	 */
	protected Connection connect(String url) throws SQLException {
		return DriverManager.getConnection(url);
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
	protected Connection connect(String url, Properties properties) throws SQLException {
		return DriverManager.getConnection(url, properties);
	}

	@Override
	public Object query(Connection connection, String query, int timeout) throws SQLException {
		Statement stmt = connection.createStatement();
		stmt.setQueryTimeout(timeout);
		stmt.execute(query);

		// If the first result is the only result and an update count simply return that count, otherwise create an iterator over the statement
		int firstCount = stmt.getUpdateCount();
		if (firstCount != -1) {
			boolean more = stmt.getMoreResults();
			int secondCount = stmt.getUpdateCount();
			ResultSet secondSet = stmt.getResultSet();
			if (!more && secondCount == -1)
				return firstCount;
			else
				return new StatementIterator(stmt, firstCount);
		}
		return new StatementIterator(stmt);
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
	protected abstract String createConnectionURL(String database, String user, String pass, Tuple<String, String>... options) throws SQLException;

	public abstract void loadDriver() throws ClassNotFoundException;

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
	protected String createJDBCURL(String type, String database, String user, String pass, Tuple<String, String>... options) {
		String url = String.format("jdbc:%s://%s", type, database);
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
			url = String.format("%suser=%s&", url, user);
			url = String.format("%spassword=%s&", url, pass);
		}
		// append other options
		for (Tuple<String, String> option : options) {
			url = String.format("%s%s=%s&", url, option.getKey(), option.getValue());
		}
		return url;
	}
}
