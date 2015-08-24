package nl.xillio.xill.plugins.database;

import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.plugins.database.services.DatabaseServiceFactory;
import nl.xillio.xill.plugins.database.util.ConnectionMetadata;

import java.util.LinkedHashMap;

import com.google.inject.Inject;

public abstract class BaseDatabaseConstruct extends Construct {

	protected static LinkedHashMap<RobotID, ConnectionMetadata> lastConnections = new LinkedHashMap<>();
	
	@Inject
	protected DatabaseServiceFactory factory;

}
