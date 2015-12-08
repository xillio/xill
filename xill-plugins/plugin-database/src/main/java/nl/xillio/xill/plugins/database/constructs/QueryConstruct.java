package nl.xillio.xill.plugins.database.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.MetaExpressionIterator;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.database.BaseDatabaseConstruct;
import nl.xillio.xill.plugins.database.services.DatabaseServiceFactory;
import nl.xillio.xill.plugins.database.util.ConnectionMetadata;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * Construct for querying a database.
 * Supports both reading and writing queries. Has support for multiple queries in one SQL String if the
 * underlying database driver supports it.
 *
 * @author Geert Konijnendijk
 * @author Sander Visser
 */
public class QueryConstruct extends BaseDatabaseConstruct {

    @Override
    public ConstructProcessor doPrepareProcess(final ConstructContext context) {
        return new ConstructProcessor(
                (query, parameters, timeout, database) -> process(query, parameters, database, timeout, factory, context),
                new Argument("query", ATOMIC),
                new Argument("parameters", emptyObject(), LIST, OBJECT),
                new Argument("timeout", fromValue(30), ATOMIC),
                new Argument("database", NULL, ATOMIC));
    }

    @SuppressWarnings("unchecked")
    static MetaExpression process(final MetaExpression query, final MetaExpression parameters, final MetaExpression database, final MetaExpression timeout, final DatabaseServiceFactory factory,
                                  final ConstructContext context) {
        String sql = query.getStringValue();
        ConnectionMetadata metaData;

        if (database.isNull()) {
            metaData = getLastConnection(context.getRootRobot());
        } else {
            metaData = assertMeta(database, "database", ConnectionMetadata.class, "variable with a connection");
        }
        Connection connection = metaData.getConnection();

        int timeoutValue = timeout.getNumberValue().intValue();

        // Parse the content of the parameter MetaExpression
        List<LinkedHashMap<String, Object>> parameterObjects = new ArrayList<>();
        if (!parameters.isNull()) {
            // Multiple parameters
            if (parameters.getType() == LIST) {
                List<Object> parameterContent = (List<Object>) extractValue(parameters);
                for (Object param : parameterContent) {
                    if (param instanceof LinkedHashMap) {
                        parameterObjects.add((LinkedHashMap<String, Object>) param);
                    } else {
                        throw new RobotRuntimeException("Expected objects in the 'parameters' parameter.");
                    }
                }
            }
            // Single parameter
            else {
                parameterObjects.add((LinkedHashMap<String, Object>) extractValue(parameters));
            }
        }

        Object result;
        try {
            result = factory.getService(metaData.getDatabaseName()).preparedQuery(connection, sql, parameterObjects, timeoutValue, context.getOnRobotInterrupt());

            return returnValue(result, sql);
        } catch (ReflectiveOperationException | ClassCastException e) {
            throw new RobotRuntimeException("Illegal DBMS type", e);
        } catch (SQLException | IllegalArgumentException e) {
            throw new RobotRuntimeException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    static MetaExpression returnValue(final Object value, final String sql) {
        if (value instanceof Integer) {
            return extractValue((int) value);
        } else if (value instanceof Iterator) {
            return extractValue((Iterator<Object>) value, sql);
        } else {
            return NULL;
        }
    }

    static MetaExpression extractValue(final Integer integer) {
        return fromValue(integer);
    }

    static MetaExpression extractValue(final Iterator<Object> iterator, final String sql) {

        MetaExpressionIterator<Object> iterationResult = new MetaExpressionIterator<>(
                iterator,
                QueryConstruct::transformIteratorElement
        );

        MetaExpression metaIterator = fromValue("Results[" + sql + "]");

        metaIterator.storeMeta(iterationResult);
        return metaIterator;
    }

    static MetaExpression transformIteratorElement(final Object o) {
        if (o instanceof Integer) {
            return fromValue((int) o);
        } else if (o instanceof Map) {
            return parseObject(o);
        }
        return NULL;
    }
}
