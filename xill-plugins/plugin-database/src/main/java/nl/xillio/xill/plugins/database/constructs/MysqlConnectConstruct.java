package nl.xillio.xill.plugins.database.constructs;

import nl.xillio.xill.plugins.database.util.Database;

public class MysqlConnectConstruct extends SimplesqlConnectConstruct {

	public MysqlConnectConstruct() {
		super(Database.MYSQL.getName());
	}

}
