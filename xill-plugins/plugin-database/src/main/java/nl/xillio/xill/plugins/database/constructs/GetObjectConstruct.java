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
import nl.xillio.xill.plugins.database.util.TypeConverter.ConversionException;

/**
 * Get an object from the database. Object can already have values set, these will be used to filter.
 *
 * @author Sander Visser
 *
 */
public class GetObjectConstruct extends BaseDatabaseConstruct {

	@Override
	public ConstructProcessor doPrepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(table, object, database) -> process(table, object, database, factory, context.getRobotID()),
			new Argument("table", ATOMIC),
			new Argument("object", OBJECT),
			new Argument("database", NULL, ATOMIC));
	}

	@SuppressWarnings("unchecked")
	static MetaExpression process(final MetaExpression table, final MetaExpression object, final MetaExpression database, final DatabaseServiceFactory factory, final RobotID robotID) {
		// get the name of the table
		String tblName = table.getStringValue();
		// create a map
		LinkedHashMap<String, Object> constraints = (LinkedHashMap<String, Object>) extractValue(object);
		ConnectionMetadata metaData;
		// if no database is given use the last made connection of this robot.
		if (database.equals(NULL)) {
			metaData = getLastConnection();
		} else {
			metaData = assertMeta(database, "database", ConnectionMetadata.class, "variable with a connection"); // check whether the given MetaExpression has the right Metadata.
		}

		Connection connection = metaData.getConnection();
		LinkedHashMap<String, Object> result = null;
		try {
			result = factory.getService(metaData.getDatabaseName()).getObject(connection, tblName, constraints); // use the service for getting the object.
		} catch (ReflectiveOperationException | SQLException | ConversionException | IllegalArgumentException e) {
			throw new RobotRuntimeException(e.getMessage(), e);
		}
		// if no entry is found in the table with the given constraints then return null
		if (result == null)
			return NULL;

		LinkedHashMap<String, MetaExpression> value = new LinkedHashMap<String, MetaExpression>();
		result.forEach((k, v) -> value.put(k, parseObject(v)));
		return fromValue(value);

	}
}
