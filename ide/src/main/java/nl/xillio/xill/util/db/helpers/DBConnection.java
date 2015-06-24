package nl.xillio.xill.util.db.helpers;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class DBConnection implements nl.xillio.xill.util.db.Connection {
	private Connection connection = null;

	private String type = null, connectionString = null, driverName = null;

	public DBConnection(final String type, final String driverName, final String connectionString) throws SQLException {
		this.type = type;
		this.driverName = driverName;
		this.connectionString = connectionString;

		connection = getConnection();
		if (connection != null) {
			// Add a shutdown hook to ensure the database connection is closed properly once done
			Runtime.getRuntime().addShutdownHook(new Thread(this::close));
		}
	}

	@Override
	public String getType() {
		return type;
	}

	public String getDriverName() {
		return driverName;
	}

	@Override
	public String getConnectionString() {
		return connectionString;
	}

	/**
	 * Hack to work with stored procedures that take OUT parameters
	 * from within Java code.
	 *
	 * @param sql
	 *        CALL ... statement.
	 * @return CallableStatement
	 * @throws SQLException
	 */
	@Override
	public CallableStatement prepareCall(final String sql) throws SQLException {
		return getConnection().prepareCall(sql);
	}

	/**
	 * @return The connection belonging to this DBConnection object
	 */
	protected Connection getConnection() throws SQLException {
		if (connection != null && !connection.isClosed()) {
			return connection;
		}

		return makeConnection();
	}

	@Override
	public void connect() {
		try {
			if (connection == null || connection.isClosed()) {
				connection = makeConnection();
			}
		} catch (SQLException e) {}
	}

	@Override
	public void reset() {
		try {
			resetConnection();
		} catch (SQLException e) {}
	}

	protected Connection resetConnection() throws SQLException {
		if (connection != null && !connection.isClosed()) {
			connection.close();
		}
		connection = makeConnection();
		return connection;
	}

	private Connection makeConnection() throws SQLException {
		// Load database driver
		/* NOT REQUIRED IN JDBC4 ANYMORE, HOWEVER WHEN PACKAGED DOES NOT WORK WHEN REMOVED */
		try {
			Class.forName(driverName).newInstance();

		} catch (Exception e) {
			throw new SQLException(null, "Failed to load database driver: " + e.getMessage());
		}

		try {
			return DriverManager.getConnection(connectionString);
		} catch (Exception e) {
			throw new SQLException(null, "Failed to connect to database: " + e.getMessage());
		}
	}

	@Override
	public String getIdentifierQuoteString() {
		try {
			return connection.getMetaData().getIdentifierQuoteString();
		} catch (Exception e) {
			return "";
		}
	}

	@Override
	public String escapeValue(String value) {
		if (value == null) {
			return value;
		}

		// sqlite doesn't escape with backslash!
		if (!type.toLowerCase().equals("sqlite")) {
			value = value.replace("\\", "\\\\");
		}
		value = value.replace("'", "''");
		return value;
	}

	@Override
	public void close() {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		} catch (SQLException e) {} catch (Exception e) {
			System.out.println("Unexpected SQL error: " + e.getMessage());
		}
	}

	@Override
	public void enableTransactions() throws SQLException {
		connection.setAutoCommit(false);

	}

	@Override
	public void commit() throws SQLException {
		connection.commit();
	}

	@Override
	public void rollback() throws SQLException {
		connection.rollback();
	}

	@Override
	public void disableTransactions() throws SQLException {
		connection.setAutoCommit(true);
	}
}
