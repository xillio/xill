package nl.xillio.xill.util.db.connectors;

import java.sql.SQLException;
import java.util.Map;

import nl.xillio.xill.util.db.Connection;
import nl.xillio.xill.util.db.Connector;
import nl.xillio.xill.util.db.helpers.StandardSQLConnection;

/**
 * Connects to an Oracle database using an SID
 */
public class OracleSIDSQLConnector implements Connector {

	@Override
	public String getName() {
		return "oraclesid";
	}

	@Override
	public String getDriverName() {
		return "oracle.jdbc.OracleDriver";
	}

	@Override
	public String makeConnectionString(final String host, final int port, final String sid, final String user, final String password, final Map<String, String> options) {
		return String.format("jdbc:oracle:thin:%s/%s@%s:%s:%s", user, password, host, port, sid); // create connectionstring using sid
	}

	@Override
	public Connection createConnection(final String connectionString) throws SQLException {
		return new StandardSQLConnection(getName(), getDriverName(), connectionString) {};
	}

}
