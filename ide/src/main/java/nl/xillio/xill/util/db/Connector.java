package nl.xillio.xill.util.db;

import java.sql.SQLException;
import java.util.Map;

public interface Connector {
	public String getName();

	public String getDriverName();

	public String makeConnectionString(final String host, final int port, final String database, final String user, final String password, final Map<String, String> options);

	public Connection createConnection(final String connectionString) throws SQLException;

}
