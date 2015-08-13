package nl.xillio.xill.plugins.database.services;

import java.sql.Connection;
import java.sql.SQLException;

import nl.xillio.xill.plugins.database.util.Database;
import nl.xillio.xill.plugins.database.util.Tuple;

/**
 * 
 * Database service for SQLite
 * 
 * @author Geert Konijnendijk
 *
 */
public class SQLiteDatabaseServiceImpl extends BaseDatabaseService {

	@Override
	public Connection createConnection(String database, String user, String pass, Tuple<String, String>... options) throws SQLException {
		return connect(createConnectionURL(database, user, pass, options));
	}

	@Override
	protected String createConnectionURL(String database, String user, String pass, Tuple<String, String>... options)  throws SQLException{
		String path = database == null ? ":memory:" : database;
		return String.format("jdbc:sqlite:%s", path);
	}

	@Override
	public void loadDriver() throws ClassNotFoundException {
		Class.forName(Database.SQLITE.getDriverClass());
	}

}
