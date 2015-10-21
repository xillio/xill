package nl.xillio.xill.plugins.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.database.services.DatabaseServiceFactory;
import nl.xillio.xill.plugins.database.util.ConnectionMetadata;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;

/**
 * The base class for each construct in the database plugin.
 */
public abstract class BaseDatabaseConstruct extends Construct {

	private static final int VALIDATION_TIMEOUT = 1000;

	private static final Logger log = LogManager.getLogger();

	private static List<ConnectionMetadata> Connections = new ArrayList<>();


	@Inject
	protected DatabaseServiceFactory factory;

	@Override
	public final ConstructProcessor prepareProcess(ConstructContext context) {
		// Before start check that previous connections can be used
		context.addRobotStartedListener((action) -> cleanLastConnections());
		return doPrepareProcess(context);
	}

	/**
	 * @param context
	 * @return The {@link ConstructProcessor} that should be returned in {@link BaseDatabaseConstruct#prepareProcess(ConstructContext)}
	 */
	protected abstract ConstructProcessor doPrepareProcess(ConstructContext context);

	/**
	 * Add hook to close all connections when runtime terminates
	 */
	public static void registerShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> Connections.forEach(c -> closeConnection(c.getConnection()))));
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
	public static void setLastConnection(ConnectionMetadata connectionMetadata) {
		Connections.add(0, connectionMetadata); //add lastconnection to start of the list
	}

	/**
	 * Get the {@link ConnectionMetadata} which was last used given an ID.
	 * 
	 * @param id
	 *        The ID.
	 * @return
	 *         The last used ConnectionMetadata.
	 */
	public static ConnectionMetadata getLastConnection() {
		// Determine that there is a connection that can be used
		if (Connections.isEmpty()) {
			throw new RobotRuntimeException("There is no connection that can be used, connect to a database first");
		}

		// Determine that this connection is usable
		ConnectionMetadata metadata = Connections.get(0);
		try {
			if (metadata.getConnection().isClosed()) {
				Connections.remove(0);
				throw new RobotRuntimeException("The last connection was closed, reconnect to a database");
			}
		} catch (SQLException e) {
			Connections.remove(0);
			throw new RobotRuntimeException("The last connection can not be used: " + e.getMessage(), e);
		}
		
		return metadata;
	}

	/**
	 * Check for validity of all lastConnections and remove the invalid ones
	 */
	public static void cleanLastConnections() {
		for (int i = 0; i < Connections.size(); i++){
			Connection connection = Connections.get(i).getConnection();
			
			try {
				connection.close();
				// Try to find out if a connection is still valid, don't bother if this takes too long
				if (connection.isClosed() || !connection.isValid(VALIDATION_TIMEOUT)) {
					Connections.remove(i);
				}
			} catch (SQLException e) {
				// When an operation on the connection fails also assume it is invalid
				Connections.remove(i);
			}
		}
	}
}
