package nl.xillio.xill.plugins.database.constructs;

import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.database.BaseDatabaseConstruct;

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
	public ConstructProcessor doPrepareProcess(final ConstructContext context) {
		return new ConstructProcessor((query, database, timeout) -> PreparedStatementConstruct.process(query, NULL, database, timeout, factory, context.getRobotID()),
			new Argument("query", ATOMIC),
			new Argument("database", NULL, ATOMIC),
			new Argument("timeout", fromValue(30), ATOMIC));
	}
}