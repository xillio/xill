package nl.xillio.xill.plugins.database.constructs;

import nl.xillio.xill.plugins.database.util.Database;

public class MssqlConnectConstruct extends SimplesqlConnectConstruct {

	public MssqlConnectConstruct() {
		super(Database.MSSQL.getName());
	}

}
