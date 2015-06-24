package nl.xillio.xill.util.db.connectors;

import java.sql.SQLException;
import java.util.Map;

import nl.xillio.xill.util.db.Connection;
import nl.xillio.xill.util.db.Connector;
import nl.xillio.xill.util.db.helpers.StandardSQLConnection;

public class MSSQLConnector implements Connector {

	@Override
	public String getName() {
		return "mssql";
	}

	@Override
	public String getDriverName() {
		return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	}

	@Override
	public String makeConnectionString(final String host, final int port, final String database, final String user, final String password, final Map<String, String> options) {
		String o = "";
		if (options != null) {
			for (String key : options.keySet()) {
				o += ";" + key + "=" + options.get(key);
			}
		}

		return String.format("jdbc:sqlserver://%s:%s;databaseName=%s;user=%s;password=%s%s", host, port, database, user, password, o);
	}

	@Override
	public Connection createConnection(final String connectionString) throws SQLException {
		return new StandardSQLConnection(getName(), getDriverName(), connectionString) {};
	}

}
