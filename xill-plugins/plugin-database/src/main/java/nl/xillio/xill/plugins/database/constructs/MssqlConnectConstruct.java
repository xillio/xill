package nl.xillio.xill.plugins.database.constructs;

import nl.xillio.xill.plugins.database.util.Database;

/**
 * The connect construct for the Mssql database.
 */
public class MssqlConnectConstruct extends SimplesqlConnectConstruct {

	/**
	 * The constructor of the {@link MssqlConnectConstruct}.
	 */
	public MssqlConnectConstruct() {
		super(Database.MSSQL.getName());
	}

}
