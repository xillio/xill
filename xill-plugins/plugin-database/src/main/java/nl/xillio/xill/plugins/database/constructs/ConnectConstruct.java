package nl.xillio.xill.plugins.database.constructs;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.ObjectExpression;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.database.BaseDatabaseConstruct;
import nl.xillio.xill.plugins.database.services.DatabaseService;
import nl.xillio.xill.plugins.database.services.DatabaseServiceFactory;
import nl.xillio.xill.plugins.database.util.ConnectionMetadata;
import nl.xillio.xill.plugins.database.util.Tuple;

/**
 * The construct for the connect function.
 */
public class ConnectConstruct extends BaseDatabaseConstruct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		Argument[] args =
		{new Argument("database", ATOMIC),
				new Argument("type", ATOMIC),
				new Argument("user", NULL, ATOMIC),
				new Argument("pass", NULL, ATOMIC),
				new Argument("options", new ObjectExpression(new LinkedHashMap<>()), OBJECT)};
		return new ConstructProcessor(a -> process(a, factory, context.getRobotID()), args);
	}

	@SuppressWarnings("unchecked")
	static MetaExpression process(final MetaExpression[] args, final DatabaseServiceFactory factory, final RobotID robotID) {
		String database = args[0].isNull() ? null : args[0].getStringValue();
		String type = args[1].getStringValue();
		String user = args[2].isNull() ? null : args[2].getStringValue();
		String pass = args[3].isNull() ? null : args[3].getStringValue();
		
		Map<String, MetaExpression> options = (Map<String, MetaExpression>) args[4].getValue();
		Tuple<String, String>[] optionsArray =
				options.entrySet().stream()
					.map(e -> new Tuple<String, String>(e.getKey(), e.getValue().getStringValue()))
					.toArray(s -> new Tuple[s]);

		DatabaseService service;
		try {
			service = factory.getService(type);
		} catch (ReflectiveOperationException | IllegalArgumentException e1) {
			throw new RobotRuntimeException("Database type is not supported", e1);
		}

		Connection connection;
		try {
			connection = service.createConnection(database, user, pass, optionsArray);
		} catch (SQLException e1) {
			throw new RobotRuntimeException(e1.getMessage(), e1);
		}

		MetaExpression metaExpression = fromValue(database);
		ConnectionMetadata newConnection = new ConnectionMetadata(type, connection); //
		lastConnections.put(robotID, newConnection); //add the robotId with the new connection to the pool
		metaExpression.storeMeta(newConnection); //store the connection metadata in the ouput MetaExpression

		return metaExpression;
	}
}
