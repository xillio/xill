package nl.xillio.xill.plugins.database.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.ObjectExpression;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.database.BaseDatabaseConstruct;
import nl.xillio.xill.plugins.database.services.DatabaseServiceFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Base implementation for connect constructs. Individual implementations can override this if they have different needs.
 */
public abstract class SimplesqlConnectConstruct extends BaseDatabaseConstruct {

    private final String databaseName;
    private final int defaultPort;

    /**
     * The constructor for the {@link SimplesqlConnectConstruct}.
     *
     * @param databaseName The name of the database.
     */
    public SimplesqlConnectConstruct(String databaseName, int defaultPort) {
        this.databaseName = databaseName;
        this.defaultPort = defaultPort;
    }

    @Override
    public ConstructProcessor doPrepareProcess(ConstructContext context) {
        Argument[] args =
                {
                        new Argument("database", ATOMIC),
                        new Argument("host", fromValue("localhost"), ATOMIC),
                        new Argument("port", fromValue(defaultPort), ATOMIC),
                        new Argument("user", NULL, ATOMIC),
                        new Argument("pass", NULL, ATOMIC),
                        new Argument("options", emptyObject(), OBJECT)};
        return new ConstructProcessor(a -> process(a, databaseName, factory, context.getRootRobot()), args);
    }

    @SuppressWarnings("unchecked")
    static MetaExpression process(MetaExpression[] args, String databaseName, DatabaseServiceFactory factory, RobotID robotID) {
        // Re-order the arguments and call the generic connect construct
        String database = String.format("%s:%d/%s", args[1].getStringValue(), args[2].getNumberValue().intValue(), args[0].getStringValue());

        String user = args[3].isNull() ? null : args[3].getStringValue();
        String pass = args[4].isNull() ? null : args[4].getStringValue();
        Map<String, MetaExpression> options = (Map<String, MetaExpression>) args[5].getValue();

        return ConnectConstruct.process(database, databaseName, user, pass, options, factory, robotID);
    }
}
