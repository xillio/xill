package nl.xillio.xill.plugins.database.constructs;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;

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
public class GetObjectConstruct extends BaseDatabaseConstruct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(table, object, database) -> process(table, object, database, factory, context.getRobotID()),
			new Argument("table", ATOMIC),
			new Argument("object", OBJECT),
			new Argument("database", NULL, ATOMIC));
	}

	@SuppressWarnings("unchecked")
	static MetaExpression process(final MetaExpression table, final MetaExpression object, final MetaExpression database, final DatabaseServiceFactory factory, final RobotID robotID) {
		String tblName = table.getStringValue();
		LinkedHashMap<String, Object> constraints = (LinkedHashMap<String, Object>) extractValue(object);
		ConnectionMetadata metaData;
		if (database.equals(NULL)) {
			metaData = lastConnections.get(robotID);
		} else {
			metaData = database.getMeta(ConnectionMetadata.class);
		}

		Connection connection = metaData.getConnection();
		LinkedHashMap<String, Object> result = null;
		try {
			result = factory.getService(metaData.getDatabaseName()).getObject(connection, tblName, constraints);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			throw new RobotRuntimeException(e.getMessage(), e);
		}
		LinkedHashMap<String, MetaExpression> value = (LinkedHashMap<String, MetaExpression>) object.getValue();
		value.clear();
		result.forEach((k, v) -> value.put(k, parseObject(v)));
		return NULL;

	}
}
