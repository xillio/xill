package nl.xillio.xill.plugins.database;

import com.google.inject.Binder;

import nl.xillio.plugins.XillPlugin;
import nl.xillio.xill.plugins.database.services.EscapeService;
import nl.xillio.xill.plugins.database.services.EscapeServiceImpl;

/**
 * This package includes all Database constructs
 */
public class DatabaseXillPlugin extends XillPlugin {
	public void configure(final Binder binder) {
		super.configure(binder);

		binder.bind(EscapeService.class).to(EscapeServiceImpl.class);
	}
	
	@Override
	public void loadConstructs() {
		// TODO Auto-generated method stub
		super.loadConstructs();
	}
}
