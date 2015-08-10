package nl.xillio.xill.plugins.database.services;

import java.sql.Connection;
import java.util.Properties;

import nl.xillio.xill.plugins.database.util.Tuple;
import nl.xillio.xill.services.XillService;

public interface DatabaseService extends XillService {

	/**
	 * Create a JDBC {@link Connection} to the database at the given URL
	 * 
	 * @param url
	 *        URL to connect to
	 * @return A connection ready to execute queries on
	 */
	Connection connect(String url);

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
	Connection connect(String url, Properties properties);

	/**
	 * Create a connection string for a JDBC connection to MySQL
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
	String createMysqlURL(String database, String user, String pass, Tuple<String, String>... options);

	/**
	 * Create a connection string for a JDBC connection to Oracle SQL
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
	String createOracleURL(String database, String user, String pass);

	/**
	 * Create properties object for connection
	 * 
	 * @param options
	 * @return
	 */
	Properties createOracleOptions(Tuple<String, String>... options);

	/**
	 * Create a connection string for a JDBC connection to Microsoft SQL Server
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
	String createMssqlURL(String database, String user, String pass, Tuple<String, String>... options);

	/**
	 * 
	 * @param file
	 *        If null returns in-memory connection string
	 * @return
	 */
	String createSqliteURL(String file);

}
