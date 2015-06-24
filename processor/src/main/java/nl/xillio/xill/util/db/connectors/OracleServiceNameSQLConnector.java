package nl.xillio.xill.util.db.connectors;

import java.sql.SQLException;
import java.util.Map;

import nl.xillio.xill.util.db.Connection;
import nl.xillio.xill.util.db.Connector;
import nl.xillio.xill.util.db.helpers.StandardSQLConnection;

public class OracleServiceNameSQLConnector implements Connector {

	@Override
	public String getName() {
		return "oracleservicename";
	}

	@Override
	public String getDriverName() {
		return "oracle.jdbc.OracleDriver";
	}

	@Override
	public String makeConnectionString(final String host, final int port, final String name, final String user, final String password, final Map<String, String> options) {
		return String.format("jdbc:oracle:thin:%s/%s@//%s:%s/%s", user, password, host, port, name); // create connectionstring using service_name
	}

	@Override
	public Connection createConnection(final String connectionString) throws SQLException {
		return new StandardSQLConnection(getName(), getDriverName(), connectionString) {};
	}

}
