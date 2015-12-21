package nl.xillio.xill.plugins.database.constructs;

import nl.xillio.xill.plugins.database.util.Database;

/**
 * The connect construct for the MYsql database.
 *
 */
public class MysqlConnectConstruct extends SimplesqlConnectConstruct {

	/**
	 * The constructor for the {@link MysqlConnectConstruct}.
	 */
	public MysqlConnectConstruct() {
		super(Database.MYSQL.getName(), 3306);
	}

}
