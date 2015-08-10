package nl.xillio.xill.plugins.database;

import nl.xillio.plugins.XillPlugin;
import nl.xillio.xill.plugins.database.services.DatabaseService;
import nl.xillio.xill.plugins.database.services.DatabaseServiceImpl;

import com.google.inject.Binder;

/**
 * This package includes all Database constructs
 */
public class DatabaseXillPlugin extends XillPlugin {

	@Override
	public void configure(Binder binder) {
		binder.bind(DatabaseService.class).to(DatabaseServiceImpl.class);
	}
}
