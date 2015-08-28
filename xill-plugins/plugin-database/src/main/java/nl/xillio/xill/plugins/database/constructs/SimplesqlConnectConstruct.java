package nl.xillio.xill.plugins.database.constructs;

import java.util.LinkedHashMap;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.ObjectExpression;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.database.BaseDatabaseConstruct;
import nl.xillio.xill.plugins.database.services.DatabaseServiceFactory;

/**
 * TODO: jdoc
 *
 */
public abstract class SimplesqlConnectConstruct extends BaseDatabaseConstruct {

	private String databaseName;

	/**
	 * The constructor for the {@link SimplesqlConnectConstruct}.
	 * @param databaseName
	 * 						The name of the database.
	 */
	public SimplesqlConnectConstruct(String databaseName) {
		this.databaseName = databaseName;
	}

	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		Argument[] args =
		{new Argument("host", ATOMIC),
				new Argument("port", fromValue(3306), ATOMIC),
				new Argument("database", ATOMIC),
				new Argument("user", NULL, ATOMIC),
				new Argument("pass", NULL, ATOMIC),
				new Argument("options", new ObjectExpression(new LinkedHashMap<>()), OBJECT)};
		return new ConstructProcessor(a -> process(a, databaseName, factory, context.getRobotID()), args);
	}

	static MetaExpression process(MetaExpression[] args, String databaseName, DatabaseServiceFactory factory, RobotID robotID) {
		// Re-order the arguments and call the generic connect construct
		String database = String.format("%s:%d/%s", args[0].getStringValue(), args[1].getNumberValue().intValue(), args[2].getStringValue());
		MetaExpression[] newArgs = {fromValue(database), fromValue(databaseName), args[3], args[4], args[5]};
		return ConnectConstruct.process(newArgs, factory, robotID);
	}
}
