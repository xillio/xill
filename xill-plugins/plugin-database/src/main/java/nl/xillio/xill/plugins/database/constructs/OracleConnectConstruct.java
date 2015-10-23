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
import nl.xillio.xill.plugins.database.util.Database;

/**
 * The connect construct for the Oracle database.
 */
public class OracleConnectConstruct extends BaseDatabaseConstruct {
	@Override
	public ConstructProcessor doPrepareProcess(ConstructContext context) {
		Argument[] args =
		{
				new Argument("host", ATOMIC),
				new Argument("port", fromValue(1521), ATOMIC),
				new Argument("database", ATOMIC),
				new Argument("useSID", fromValue(true), ATOMIC),
				new Argument("user", NULL, ATOMIC),
				new Argument("pass", NULL, ATOMIC),
				new Argument("options", new ObjectExpression(new LinkedHashMap<>()), OBJECT)};
		return new ConstructProcessor(a -> process(a, factory, context.getRootRobot()), args);
	}

	static MetaExpression process(MetaExpression[] args, DatabaseServiceFactory factory, final RobotID robotID) {
		// Re-order the arguments and call the generic connect construct
		boolean useSID = args[3].getBooleanValue();
		String formatString = null;
		// Connect URL format differs depending on the use of SID or Service Name for a connection
		if (useSID)
			formatString = "%s:%d:%s";
		else
			formatString = "%s:%d/%s";
		String database = String.format(formatString, args[0].getStringValue(), args[1].getNumberValue().intValue(), args[2].getStringValue());
		MetaExpression[] newArgs = {fromValue(database), fromValue(Database.ORACLE.getName()), args[4], args[5], args[6]};
		return ConnectConstruct.process(newArgs, factory, robotID);

	}
}
