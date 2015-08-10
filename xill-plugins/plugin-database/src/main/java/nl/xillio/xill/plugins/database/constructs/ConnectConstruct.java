package nl.xillio.xill.plugins.database.constructs;

import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import nl.xillio.xill.api.components.AtomicExpression;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.ObjectExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.database.services.DatabaseService;
import nl.xillio.xill.plugins.database.services.DatabaseServiceFactory;
import nl.xillio.xill.plugins.database.util.ConnectionMetadata;
import nl.xillio.xill.plugins.database.util.Tuple;

/**
 *
 */
public class ConnectConstruct extends Construct {

	private static DatabaseServiceFactory factory = new DatabaseServiceFactory();

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		Argument[] args =
		{new Argument("database", ATOMIC), new Argument("type", ATOMIC), new Argument("user", NULL, ATOMIC), new Argument("pass", NULL, ATOMIC),
		    new Argument("options", new ObjectExpression(new LinkedHashMap<>()), OBJECT)};
		return new ConstructProcessor(ConnectConstruct::process, args);
	}

	/**
	 */
	@SuppressWarnings("unchecked")
	static MetaExpression process(final MetaExpression[] args) {
		String database = args[0].isNull() ? null : args[0].getStringValue();
		String type = args[1].getStringValue();
		String user = args[2].isNull() ? null : args[2].getStringValue();
		String pass = args[3].isNull() ? null : args[3].getStringValue();
		Map<String, MetaExpression> options = (Map<String, MetaExpression>) args[4].getValue();

		String url;
		Properties properties = null;

		Tuple<String, String>[] optionsArray =
		    (Tuple[]) options.entrySet().stream().map((e) -> new Tuple<String, String>(e.getKey(), e.getValue().getStringValue())).toArray((s) -> new Tuple[s]);

		DatabaseService service;
		try {
			service = factory.getService(type);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e1) {
			throw new RobotRuntimeException("DBMS type is not supported", e1);
		}

		Connection connection = service.createConnection(database, user, pass, optionsArray);
		MetaExpression metaExpression = new AtomicExpression(database);
		metaExpression.storeMeta(new ConnectionMetadata(connection));

		return metaExpression;
	}
}
