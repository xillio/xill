package nl.xillio.xill.plugins.database;

import nl.xillio.plugins.XillPlugin;

/**
 * This package includes all Database constructs
 */
public class DatabaseXillPlugin extends XillPlugin {

	/**
	 * Registers hooks to shutdown opened database connections when the VM terminates
	 */
	public DatabaseXillPlugin() {
		BaseDatabaseConstruct.registerShutdownHook();
	}

}
