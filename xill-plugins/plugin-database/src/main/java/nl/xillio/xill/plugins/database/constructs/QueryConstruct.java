package nl.xillio.xill.plugins.database.constructs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
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
import nl.xillio.xill.api.components.RobotID;
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
		return new ConstructProcessor((query, parameters, database, timeout) -> process(query, parameters, database, timeout, factory,context.getRobotID()),
		  new Argument("query", ATOMIC),
		  new Argument("parameters", NULL, LIST),
		  new Argument("database", NULL, ATOMIC),
		  new Argument("timeout", fromValue(30), ATOMIC));
	}

	@SuppressWarnings("unchecked")
	static MetaExpression process(MetaExpression query, MetaExpression parameters, MetaExpression database, MetaExpression timeout, DatabaseServiceFactory factory, RobotID robotID) {
		String sql = query.getStringValue();
		ConnectionMetadata metaData;

		if (database.isNull()) {
			metaData = lastConnections.get(robotID);
		} else {
			metaData = assertMeta(database,"database", ConnectionMetadata.class, "contain a database");
		}
		Connection connection = metaData.getConnection();

		int to = timeout.getNumberValue().intValue();

		List<LinkedHashMap<String, Object>> objectParams = (List<LinkedHashMap<String, Object>>) extractValue(parameters);

		Object result;
		try {
			result = factory.getService(metaData.getDatabaseName()).query(connection, sql, objectParams, to);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new RobotRuntimeException("Illegal DBMS type", e);
		} catch (SQLException | IllegalArgumentException e) {
			throw new RobotRuntimeException(e.getMessage(),e);
		}

		return returnValue(result,sql);
		
	}
	
	private static MetaExpression returnValue(Object value,String sql){
		if (value instanceof Integer) {
			return fromValue((int) value);
		}
		else if (value instanceof Iterator) {
			// Wrap StatementIterator into MetaExpressionIterator
			MetaExpressionIterator<Object> it = new MetaExpressionIterator<>((Iterator<Object>) value, (o) -> {
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
}
