package nl.xillio.xill.util.db.connectors;

import java.sql.SQLException;
import java.util.Map;

import nl.xillio.xill.util.db.Connection;
import nl.xillio.xill.util.db.Connector;
import nl.xillio.xill.util.db.helpers.StandardSQLConnection;

public class SQLiteConnector implements Connector {

	@Override
	public String getName() {
		return "sqlite";
	}

	@Override
	public String getDriverName() {
		return "org.sqlite.JDBC";
	}

	@Override
	public String makeConnectionString(final String host, final int port, final String database, final String user, final String password, final Map<String, String> options) {
		return String.format("jdbc:sqlite:%s", database);
	}

	@Override
	public Connection createConnection(final String connectionString) throws SQLException {
		return new StandardSQLConnection(getName(), getDriverName(), connectionString) {};
	}
}
