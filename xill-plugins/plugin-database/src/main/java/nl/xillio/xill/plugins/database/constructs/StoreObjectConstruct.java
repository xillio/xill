package nl.xillio.xill.plugins.database.constructs;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.database.BaseDatabaseConstruct;
import nl.xillio.xill.plugins.database.services.DatabaseServiceFactory;
import nl.xillio.xill.plugins.database.util.ConnectionMetadata;

/**
 * 
 * 
 * @author Sander Visser
 *
 */
public class StoreObjectConstruct extends BaseDatabaseConstruct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		Argument[] args =
		{
				new Argument("table", ATOMIC),
				new Argument("object", OBJECT),
				new Argument("keys", LIST),
				new Argument("overwrite", TRUE, ATOMIC),
				new Argument("database", NULL, ATOMIC),
		};
		return new ConstructProcessor((a) -> process(a, factory, context.getRobotID()), args);
	}

	@SuppressWarnings("unchecked")
	static MetaExpression process(final MetaExpression[] args, final DatabaseServiceFactory factory, final RobotID robotID) {
		String tblName = args[0].getStringValue();
		LinkedHashMap<String, Object> newObject = (LinkedHashMap<String, Object>) extractValue(args[1]);
		ConnectionMetadata metaData;
		if (args[4].equals(NULL)) {
			metaData = lastConnections.get(robotID);
		} else {
			metaData = args[4].getMeta(ConnectionMetadata.class);
		}
		Connection connection = metaData.getConnection();

		List<MetaExpression> keysMeta = (ArrayList<MetaExpression>) args[2].getValue();
		List<String> keys = keysMeta.stream().map(m -> m.getStringValue()).collect(Collectors.toList());

		boolean overwrite = args[3].getBooleanValue();

		try {
			factory.getService(metaData.getDatabaseName()).storeObject(connection, tblName, newObject, keys, overwrite);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			throw new RobotRuntimeException("SQL Exception, " + e.getMessage(), e);
		}

		return NULL;

	}

}
