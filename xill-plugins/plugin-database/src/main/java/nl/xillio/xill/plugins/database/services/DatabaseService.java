package nl.xillio.xill.plugins.database.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import nl.xillio.xill.plugins.database.util.StatementIterator;
import nl.xillio.xill.plugins.database.util.Tuple;
import nl.xillio.xill.plugins.database.util.TypeConverter.ConversionException;
import nl.xillio.xill.services.XillService;

/**
 * The interface defined for any databaseService.
 */
public interface DatabaseService extends XillService {

	/**
	 * Creates a connection.
	 * 
	 * @param database
	 *        The database type we want to connect with.
	 * @param user
	 *        The user of the database.
	 * @param pass
	 *        The pass of the database.
	 * @param options
	 *        Possible handed options.
	 * @return
	 *         Returns a {@link Connection}.
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	Connection createConnection(String database, String user, String pass, Tuple<String, String>... options) throws SQLException;

	/**
	 * Executes a query and returns an object.
	 * 
	 * @param connection
	 *        The JDBC connection
	 * @param query
	 *        A SQL query, can contain multiple queries (if enabled in the JDBC driver).
	 *        Can be parameterized with ":parameterName". All other colons should be escaped.
	 * @param parameters
	 *        Parameters of which the values are filled into the query at points where ":key" is found
	 * @param timeout
	 *        Maximum timeout in seconds
	 * @return An Integer if the query contains one insert or update statement, a {@link StatementIterator} otherwise.
	 * @throws SQLException
	 *         When the query fails
	 */
	Object query(Connection connection, String query, List<LinkedHashMap<String, Object>> parameters, int timeout) throws SQLException;

	/**
	 * Gets an object from a database.
	 * 
	 * @param connection
	 *        The JDBC connection.
	 * @param tableName
	 *        The name of the table.
	 * @param constraints
	 *        The constraints of the query.
	 * @return
	 *         Returns an object from a table given a few constraints.
	 * @throws SQLException
	 * @throws ConversionException
	 */
	LinkedHashMap<String, Object> getObject(Connection connection, String tableName, Map<String, Object> constraints) throws SQLException, ConversionException, IllegalArgumentException;

	/**
	 * <p>
	 * Stores an object in a table.
	 * </p>
	 * <p>
	 * May or may not update an existing object
	 * <p>
	 * 
	 * @param connection
	 *        The JDBC connection.
	 * @param table
	 *        The name of the table.
	 * @param newObject
	 *        The object we want to store.
	 * @param keys
	 *        The keys we hand the object.
	 * @param allowUpdate
	 *        A boolean whether we allow to update existing entries in the table.
	 * @throws SQLException
	 */
	void storeObject(Connection connection, String table, LinkedHashMap<String, Object> newObject, List<String> keys, boolean allowUpdate) throws SQLException;

}
