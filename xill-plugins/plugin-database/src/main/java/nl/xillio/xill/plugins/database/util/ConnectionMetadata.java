package nl.xillio.xill.plugins.database.util;

import java.sql.Connection;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.MetadataExpression;

/**
 * 
 * Data class for storing a {@link Connection} in a {@link MetaExpression}.
 * 
 * @author Geert Konijnendijk
 * @author Sander Visser
 *
 */
public class ConnectionMetadata implements MetadataExpression {

	private Connection connection;

	public ConnectionMetadata(Connection connection) {
		super();
		this.connection = connection;
	}

	public Connection getConnection() {
		return connection;
	}
}
