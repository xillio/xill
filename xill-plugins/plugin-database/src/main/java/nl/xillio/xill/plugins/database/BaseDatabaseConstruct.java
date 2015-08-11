package nl.xillio.xill.plugins.database;

import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.plugins.database.services.DatabaseServiceFactory;

import com.google.inject.Inject;

public abstract class BaseDatabaseConstruct extends Construct {

	@Inject
	protected DatabaseServiceFactory factory;

}
