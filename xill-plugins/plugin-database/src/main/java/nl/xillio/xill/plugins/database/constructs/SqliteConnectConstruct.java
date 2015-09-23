package nl.xillio.xill.plugins.database.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.database.BaseDatabaseConstruct;
import nl.xillio.xill.plugins.database.services.DatabaseServiceFactory;
import nl.xillio.xill.plugins.database.util.Database;

/**
 * The connect construct for the SQLite database.
 */
public class SqliteConnectConstruct extends BaseDatabaseConstruct {

	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		// Default is in-memory database
		return new ConstructProcessor((file) -> process(file, factory,context.getRobotID()), new Argument("file", fromValue(":memory:"), ATOMIC));
	}

	static MetaExpression process(MetaExpression file, DatabaseServiceFactory factory,RobotID robotID) {
		MetaExpression[] newArgs = {file, fromValue(Database.SQLITE.getName()), NULL, NULL, emptyObject()};
		return ConnectConstruct.process(newArgs, factory,robotID);
	}
}