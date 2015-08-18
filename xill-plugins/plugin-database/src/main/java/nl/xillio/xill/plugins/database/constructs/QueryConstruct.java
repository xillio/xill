package nl.xillio.xill.plugins.database.constructs;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.MetaExpressionIterator;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.database.BaseDatabaseConstruct;
import nl.xillio.xill.plugins.database.services.BaseDatabaseService;
import nl.xillio.xill.plugins.database.services.DatabaseServiceFactory;
import nl.xillio.xill.plugins.database.util.ConnectionMetadata;

/**
 * 
 * Construct for querying a database. Supports both reading and writing queries. Has support for multiple queries in one SQL String if the
 * underlying database driver supports it.
 * 
 * @author Geert Konijnendijk
 * @author Sander Visser
 *
 */
public class QueryConstruct extends BaseDatabaseConstruct {

	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor((query, parameters, database, timeout) -> process(query, parameters, database, timeout, factory),
		  new Argument("query", ATOMIC),
		  new Argument("parameters", NULL, LIST),
		  new Argument("database", NULL, ATOMIC),
		  new Argument("timeout", fromValue(30), ATOMIC));
	}

	@SuppressWarnings("unchecked")
	static MetaExpression process(MetaExpression query, MetaExpression parameters, MetaExpression database, MetaExpression timeout, DatabaseServiceFactory factory) {
		String sql = query.getStringValue();
		ConnectionMetadata metaData;
		if (database.equals(NULL)) {
			metaData = BaseDatabaseService.getLastConnection();
		} else {
			metaData = database.getMeta(ConnectionMetadata.class);
			BaseDatabaseService.setLastConnection(metaData);
		}
		Connection connection = metaData.getConnection();

		int to = timeout.getNumberValue().intValue();

		List<LinkedHashMap<String, Object>> objectParams = (List<LinkedHashMap<String, Object>>) extractValue(parameters);

		Object result = null;
		try {
			result = factory.getService(metaData.getDatabaseName()).query(connection, sql, objectParams, to);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new RobotRuntimeException("Illegal DBMS type", e);
		} catch (SQLException | IllegalArgumentException e) {
			throw new RobotRuntimeException(e.getMessage(),e);
		}

		if (result instanceof Integer) {
			return fromValue((int) result);
		}
		else if (result instanceof Iterator) {
			// Wrap StatementIterator into MetaExpressionIterator
			MetaExpressionIterator<Object> it = new MetaExpressionIterator<>((Iterator<Object>) result, (o) -> {
				if (o instanceof Integer)
					return fromValue((int) o);
				else if (o instanceof Map)
				  return parseObject(o);
				return null;
			});

			MetaExpression metaIterator = fromValue("Results[" + sql + "]");
			metaIterator.storeMeta(it);
			return metaIterator;
		}

		return null;
	}

	private static final Pattern PARAMETER_PATTERN = Pattern.compile("(?!\\\\):([a-zA-Z]+)");

	public static void main(String[] args) {
		String sql = "SELECT * from table where (x=:bla) ";
		// System.out.println(Arrays.toString(PARAMETER_PATTERN.split(sql)));
		Matcher m = PARAMETER_PATTERN.matcher(sql);
		while (m.find()) {
			System.out.println(m.group(1));

		}

		System.out.println(m.replaceAll("?"));
		// m.

	}
}
