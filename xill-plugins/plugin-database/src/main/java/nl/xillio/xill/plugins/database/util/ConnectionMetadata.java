package nl.xillio.xill.plugins.database.util;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.data.MetadataExpression;

import java.sql.Connection;

/**
 * Data class for storing a {@link Connection} in a {@link MetaExpression}.
 *
 * @author Geert Konijnendijk
 * @author Sander Visser
 */
public class ConnectionMetadata implements MetadataExpression {

	String databaseName;
	private Connection connection;

	/**
	 * Constructor for the ConnectionMetadata.
	 *
	 * @param databaseName The name of the database.
	 * @param connection   The connection.
	 */
	public ConnectionMetadata(String databaseName, Connection connection) {
		super();
		this.databaseName = databaseName;
		this.connection = connection;
	}

	/**
	 * @return Returns the connection.
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * Sets the connection.
	 *
	 * @param newConnection The connection we want to set.
	 */
	public void setConnection(Connection newConnection) {
		connection = newConnection;
	}

	/**
	 * @return Returns the database name.
	 */
	public String getDatabaseName() {
		return databaseName;
	}
}
