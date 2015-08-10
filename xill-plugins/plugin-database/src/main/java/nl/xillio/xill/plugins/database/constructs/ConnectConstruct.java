package nl.xillio.xill.plugins.database.constructs;

import static nl.xillio.xill.plugins.database.util.Database.MSSQL;
import static nl.xillio.xill.plugins.database.util.Database.MYSQL;
import static nl.xillio.xill.plugins.database.util.Database.ORACLE;
import static nl.xillio.xill.plugins.database.util.Database.SQLITE;

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
import nl.xillio.xill.plugins.database.util.ConnectionMetadata;
import nl.xillio.xill.plugins.database.util.Tuple;

import com.google.inject.Inject;

/**
 *
 */
public class ConnectConstruct extends Construct {

	@Inject
	private DatabaseService service;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		Argument[] args =
		{new Argument("database", ATOMIC), new Argument("type", ATOMIC), new Argument("user", NULL, ATOMIC), new Argument("pass", NULL, ATOMIC),
		    new Argument("options", new ObjectExpression(new LinkedHashMap<>()), OBJECT)};
		return new ConstructProcessor((a) -> process(a, service), args);
	}

	/**
	 */
	@SuppressWarnings("unchecked")
	static MetaExpression process(final MetaExpression[] args, DatabaseService service) {
		String database = args[0].isNull() ? null : args[0].getStringValue();
		String type = args[1].getStringValue();
		String user = args[2].isNull() ? null : args[2].getStringValue();
		String pass = args[3].isNull() ? null : args[3].getStringValue();
		Map<String, MetaExpression> options = (Map<String, MetaExpression>) args[4].getValue();

		String url;
		Properties properties = null;

		Tuple<String, String>[] optionsArray =
		    (Tuple[]) options.entrySet().stream().map((e) -> new Tuple<String, String>(e.getKey(), e.getValue().getStringValue())).toArray((s) -> new Tuple[s]);

		if (type.equals(ORACLE.getName())) {
			url = service.createOracleURL(database, user, pass);
			properties = service.createOracleOptions(optionsArray);
		}
		else if (type.equals(MSSQL.getName())) {
			url = service.createMssqlURL(database, user, pass, optionsArray);
		}
		else if (type.equals(MYSQL.getName())) {
			url = service.createMysqlURL(database, user, pass, optionsArray);
		}
		else if (type.equals(SQLITE.getName())) {
			url = service.createSqliteURL(database);
		}
		else {
			throw new RobotRuntimeException("DBMS type is not supported");
		}

		Connection connection = properties == null ? service.connect(url) : service.connect(url, properties);
		MetaExpression metaExpression = new AtomicExpression(database);
		metaExpression.storeMeta(new ConnectionMetadata(connection));

		return metaExpression;
	}
}
