package nl.xillio.xill.plugins.database.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;

import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.database.util.Database;
import nl.xillio.xill.plugins.database.util.Tuple;
import oracle.jdbc.driver.OracleConnection;

/**
 * DatabaseService for Oracle.
 *
 */
public class OracleDatabaseServiceImpl extends BaseDatabaseService {

	@SuppressWarnings("unchecked")
	@Override
	public Connection createConnection(final String database, final String user, final String pass, final Tuple<String, String>... options) {
		try {
			Connection connection = connect(createConnectionURL(database, user, pass, options), createProperties(options));

			// Enable prepared statement caching
			((OracleConnection) connection).setImplicitCachingEnabled(true);
			return connection;
		} catch (Exception e) {
			throw new RobotRuntimeException(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected String createConnectionURL(final String database, final String user, final String pass, final Tuple<String, String>... options) throws SQLException {
		if ((user == null) != (pass == null)) {
			throw new IllegalArgumentException("User and pass should be both null or both non-null");
		} else if (user != null && pass != null) {
			// prepend username and password
			return String.format("jdbc:oracle:thin:%s/%s@%s", user, pass, database);
		} else {
			// url without username and password
			return String.format("jdbc:oracle:thin:@%s", database);
		}
	}

	/**
	 * Creates a new {@link Properties} given a set of options.
	 *
	 * @param options
	 *        The options we get.
	 * @return
	 *         The properties extracted from the options.
	 * @throws NullPointerException
	 */
	@SuppressWarnings("unchecked")
	public Properties createProperties(final Tuple<String, String>... options) {
		Properties properties = new Properties();
		Arrays.stream(options).forEach(p -> properties.put(p.getKey(), p.getValue()));
		return properties;
	}

	@Override
	public void loadDriver() throws ClassNotFoundException {
		Class.forName(Database.ORACLE.getDriverClass());
	}

	@Override
	protected String createSelectQuery(final String table, final String constraintsSql) {
		return String.format("SELECT * FROM %1$s WHERE %2$s AND rownum <= 1", table, constraintsSql);
	}

}
