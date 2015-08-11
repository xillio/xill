package nl.xillio.xill.plugins.database.constructs;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.MetaExpressionIterator;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.database.BaseDatabaseConstruct;
import nl.xillio.xill.plugins.database.services.DatabaseServiceFactory;
import nl.xillio.xill.plugins.database.util.ConnectionMetadata;
import nl.xillio.xill.plugins.database.util.StatementIterator;

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
		return new ConstructProcessor((query, database, timeout) -> process(query, database, timeout, factory),
		  new Argument("query", ATOMIC),
		  new Argument("database", ATOMIC),
		  new Argument("timeout", fromValue(30), ATOMIC));
	}

	static MetaExpression process(MetaExpression query, MetaExpression database, MetaExpression timeout, DatabaseServiceFactory factory) {
		String sql = query.getStringValue();
		ConnectionMetadata metaData = database.getMeta(ConnectionMetadata.class);
		Connection connection = metaData.getConnection();
		int to = timeout.getNumberValue().intValue();

		Object result = null;
		try {
			result = factory.getService(metaData.getDatabaseName()).query(connection, sql, to);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new RobotRuntimeException("Illegal DBMS type", e);
		} catch (SQLException e) {
			throw new RobotRuntimeException(e.getMessage());
		}

		if (result instanceof Integer) {
			return fromValue((int) result);
		}
		else if (result instanceof StatementIterator) {
			// Wrap StatementIterator into MetaExpressionIterator
			MetaExpressionIterator<Object> it = new MetaExpressionIterator<>((StatementIterator) result, (o) -> {
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
