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

	@SuppressWarnings("unchecked")
	@Override
	public Connection createConnection(String database, String user, String pass, Tuple<String, String>... options) throws SQLException {
		return connect(createConnectionURL(database, user, pass, options));
	}

	@SuppressWarnings("unchecked")
	@Override
	protected String createConnectionURL(String database, String user, String pass, Tuple<String, String>... options) throws SQLException {
		return createJDBCURL("jtds:sqlserver", database, user, pass, ";", ";", options);
	}

	@Override
	public void loadDriver() throws ClassNotFoundException {
		Class.forName(Database.MSSQL.getDriverClass());
	}

	@Override
	String createSelectQuery(String table, String constraintsSql) {
		return String.format("SELECT TOP 1 * FROM %1$s WHERE %2$s", table, constraintsSql);
	}

}
