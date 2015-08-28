package nl.xillio.xill.plugins.database;

import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.plugins.database.services.DatabaseServiceFactory;
import nl.xillio.xill.plugins.database.util.ConnectionMetadata;

import java.util.LinkedHashMap;

import com.google.inject.Inject;

/**
 * The base class for each construct in the database plugin.
 */
public abstract class BaseDatabaseConstruct extends Construct {

	protected static LinkedHashMap<RobotID, ConnectionMetadata> lastConnections = new LinkedHashMap<>();
	
	@Inject
	protected DatabaseServiceFactory factory;

	/**
	 * Set the last made connection.
	 * @param id
	 * 					The ID of the connection.
	 * @param connectionMetadata
	 * 					The connection.
	 */
	public static void setLastConnections(RobotID id, ConnectionMetadata connectionMetadata) {
		BaseDatabaseConstruct.lastConnections.put(id, connectionMetadata);
	}
	
	/**
	 * Get the {@link ConnectionMetadata} which was last used given an ID.
	 * @param id
	 * 					The ID.
	 * @return
	 * 				The last used ConnectionMetadata.
	 */
	public ConnectionMetadata getLastConnections(RobotID id){
		return BaseDatabaseConstruct.lastConnections.get(id);
	}
}
