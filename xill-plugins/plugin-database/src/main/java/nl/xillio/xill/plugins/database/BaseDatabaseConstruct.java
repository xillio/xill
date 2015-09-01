package nl.xillio.xill.plugins.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;

import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.plugins.database.services.DatabaseServiceFactory;
import nl.xillio.xill.plugins.database.util.ConnectionMetadata;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;

/**
 * The base class for each construct in the database plugin.
 */
public abstract class BaseDatabaseConstruct extends Construct {

	private static final Logger log = LogManager.getLogger();

	protected static LinkedHashMap<RobotID, ConnectionMetadata> lastConnections = new LinkedHashMap<>();

	@Inject
	protected DatabaseServiceFactory factory;

	/**
	 * Add hook to close all connections when runtime terminates
	 */
	public static void registerShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> lastConnections.forEach((k, v) -> closeConnection(v.getConnection()))));
	}

	/**
	 * Close a connection without checked exceptions
	 * 
	 * @param c
	 */
	private static void closeConnection(Connection c) {
		try {
			c.close();
		} catch (SQLException e) {
			log.error("Could not close database connection", e);
		}
	}

	/**
	 * Set the last made connection.
	 * 
	 * @param id
	 *        The ID of the connection.
	 * @param connectionMetadata
	 *        The connection.
	 */
	public static void setLastConnections(RobotID id, ConnectionMetadata connectionMetadata) {
		lastConnections.put(id, connectionMetadata);
	}

	/**
	 * Get the {@link ConnectionMetadata} which was last used given an ID.
	 * 
	 * @param id
	 *        The ID.
	 * @return
	 *         The last used ConnectionMetadata.
	 */
	public ConnectionMetadata getLastConnections(RobotID id) {
		return lastConnections.get(id);
	}
}
