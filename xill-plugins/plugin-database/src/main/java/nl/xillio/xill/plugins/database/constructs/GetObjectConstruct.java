package nl.xillio.xill.plugins.database.constructs;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import nl.xillio.xill.api.components.MetaExpression;
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
 * 
 * @author Sander Visser
 *
 */
public class GetObjectConstruct extends BaseDatabaseConstruct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
		  (table, object, columns, database) -> process(table, object, columns, database, factory),
		  new Argument("table", ATOMIC),
		  new Argument("object", OBJECT),
		  new Argument("columns", LIST),
		  new Argument("database", NULL, ATOMIC));
	}

	static MetaExpression process(final MetaExpression table, final MetaExpression object, final MetaExpression columns, final MetaExpression database, final DatabaseServiceFactory factory) {
		String tblName = table.getStringValue();
		LinkedHashMap<String, Object> constraints = (LinkedHashMap<String, Object>) object.getValue();
		ConnectionMetadata metaData;
		if (database.equals(NULL)) {
			metaData = BaseDatabaseService.getLastConnection();
		} else {
			metaData = database.getMeta(ConnectionMetadata.class);
			BaseDatabaseService.setLastConnection(metaData);
		}
		Connection connection = metaData.getConnection();
		Object result = null;

		List<MetaExpression> columnsMeta = (ArrayList<MetaExpression>) columns.getValue();
		List<String> columnsStrings = columnsMeta.stream().map(m -> m.getStringValue()).collect(Collectors.toList());

		try {
			result = factory.getService(metaData.getDatabaseName()).getObject(connection, tblName, constraints, columnsStrings);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			throw new RobotRuntimeException(e.getMessage());
		}

		return parseObject(result);

	}
}
