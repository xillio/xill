package nl.xillio.xill.plugins.database.constructs;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.MetaExpressionIterator;
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
 * Construct for querying a database. Supports both reading and writing queries. Has support for multiple queries in one SQL String if the
 * underlying database driver supports it.
 *
 * @author Geert Konijnendijk
 * @author Sander Visser
 *
 */
public class QueryConstruct extends BaseDatabaseConstruct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor((query, parameters, database, timeout) -> process(query, parameters, database, timeout, factory, context.getRobotID()),
			new Argument("query", ATOMIC),
			new Argument("parameters", NULL, LIST),
			new Argument("database", NULL, ATOMIC),
			new Argument("timeout", fromValue(30), ATOMIC));
	}

	@SuppressWarnings("unchecked")
	static MetaExpression process(final MetaExpression query, final MetaExpression parameters, final MetaExpression database, final MetaExpression timeout, final DatabaseServiceFactory factory,
			final RobotID robotID) {
		String sql = query.getStringValue();
		ConnectionMetadata metaData;

		if (database.isNull()) {
			metaData = lastConnections.get(robotID);
		} else {
			metaData = assertMeta(database, "database", ConnectionMetadata.class, "contain a database");
		}
		Connection connection = metaData.getConnection();

		int timeoutValue = timeout.getNumberValue().intValue();

		// Parse the content of the parameter MetaExpression
		List<MetaExpression> parameterContent = (List<MetaExpression>) parameters.getValue();
		List<LinkedHashMap<String, Object>> parameterObjects = new ArrayList<LinkedHashMap<String, Object>>(parameterContent.size());
		for (MetaExpression meta : parameterContent) {
			parameterObjects.add((LinkedHashMap<String, Object>) meta.getValue());
		}

		Object result;
		try {
			result = factory.getService(metaData.getDatabaseName()).query(connection, sql, parameterObjects, timeoutValue);
			return returnValue(result, sql);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | ClassCastException e) {
			throw new RobotRuntimeException("Illegal DBMS type", e);
		} catch (SQLException | IllegalArgumentException e) {
			throw new RobotRuntimeException(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	static MetaExpression returnValue(final Object value, final String sql) throws IllegalArgumentException {
		if (value instanceof Integer) {
			return extractValue((int) value);
		}
		else if (value instanceof Iterator) {
			return extractValue((Iterator<Object>) value, sql);
		}
		else {
			return NULL;
		}
	}

	static MetaExpression extractValue(final Integer integer) {
		return fromValue(integer);
	}

	private static MetaExpression extractValue(final Iterator<Object> iterator, final String sql) {

		MetaExpressionIterator<Object> iterationResult = new MetaExpressionIterator<>(iterator, (o) -> {
			if (o instanceof Integer) {
				return fromValue((int) o);
			} else if (o instanceof Map) {
				return parseObject(o);
			}
			return NULL;
		});

		MetaExpression metaIterator = fromValue("Results[" + sql + "]");
		metaIterator.storeMeta(iterationResult);
		return metaIterator;
	}
}
