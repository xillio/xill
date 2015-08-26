package nl.xillio.xill.plugins.database.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import nl.xillio.xill.plugins.database.util.Database;
import nl.xillio.xill.plugins.database.util.Tuple;
import oracle.jdbc.driver.OracleConnection;

public class OracleDatabaseServiceImpl extends BaseDatabaseService {

	@Override
	public Connection createConnection(String database, String user, String pass, Tuple<String, String>... options) throws SQLException {
		Connection connection = connect(createConnectionURL(database, user, pass, options), createProperties(options));
		// Enable prepared statement caching
		((OracleConnection) connection).setImplicitCachingEnabled(true);
		return connection;
	}

	@Override
	protected String createConnectionURL(String database, String user, String pass, Tuple<String, String>... options) throws SQLException {
		if ((user == null) != (pass == null))
			throw new IllegalArgumentException("User and pass should be both null or both non-null");
		else if (user != null && pass != null)
			// prepend username and password
			return String.format("jdbc:oracle:thin:%s/%s@%s", user, pass, database);
		else
			// url without username and password
			return String.format("jdbc:oracle:thin:@%s", database);
	}

	/**
	 * Creates a new {@link Properties} given a 
	 * @param options
	 * @return
	 * @throws NullPointerException
	 */
	public Properties createProperties(Tuple<String, String>... options) throws NullPointerException {
		Properties properties = new Properties();
		Arrays.stream(options).forEach(p -> properties.put(p.getKey(), p.getValue()));
		return properties;
	}

	@Override
	public void loadDriver() throws ClassNotFoundException {
		Class.forName(Database.ORACLE.getDriverClass());
	}

	@Override
	protected String createSelectQuery(String table, String constraintsSql) {
		return String.format("SELECT * FROM %1$s WHERE %2$s AND rownum <= 1", table, constraintsSql);
	}

}
