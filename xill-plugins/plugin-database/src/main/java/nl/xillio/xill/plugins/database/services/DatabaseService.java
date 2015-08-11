package nl.xillio.xill.plugins.database.services;

import java.sql.Connection;
import java.sql.SQLException;

import nl.xillio.xill.plugins.database.util.StatementIterator;
import nl.xillio.xill.plugins.database.util.Tuple;
import nl.xillio.xill.services.XillService;

public interface DatabaseService extends XillService {

	Connection createConnection(String database, String user, String pass, Tuple<String, String>... options) throws SQLException;

	/**
	 * 
	 * @param connection
	 *        The JDBC connection
	 * @param query
	 *        A SQL query, can contain multiple queries
	 * @param timeout
	 *        Maximum timeout in seconds
	 * @return An Integer if the query contains one insert or update statement, a {@link StatementIterator} otherwise.
	 * @throws SQLException
	 *         When the query fails
	 */
	Object query(Connection connection, String query, int timeout) throws SQLException;

}
