package nl.xillio.xill.plugins.database.services;

import java.sql.Connection;
import java.sql.SQLException;

import nl.xillio.xill.plugins.database.util.Database;
import nl.xillio.xill.plugins.database.util.Tuple;

/**
 * Database service for Microsoft SQL Server
 * 
 * @author Geert Konijnendijk
 * @author Sander Visser
 *
 */
public class MssqlDatabaseServiceImpl extends BaseDatabaseService {

	@Override
	public Connection createConnection(String database, String user, String pass, Tuple<String, String>... options) throws SQLException {
		return connect(createConnectionURL(database, user, pass, options));
	}

	@Override
	protected String createConnectionURL(String database, String user, String pass, Tuple<String, String>... options) throws SQLException {
		return createJDBCURL(Database.MSSQL.getName(), database, user, pass, options);
	}

	@Override
	public void loadDriver() throws ClassNotFoundException {
		Class.forName(Database.MSSQL.getDriverClass());
	}

}
