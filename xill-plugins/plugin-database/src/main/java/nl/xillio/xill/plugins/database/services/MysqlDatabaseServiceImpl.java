package nl.xillio.xill.plugins.database.services;

import nl.xillio.xill.plugins.database.util.Database;
import nl.xillio.xill.plugins.database.util.Tuple;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Database service for MySQL
 *
 * @author Geert Konijnendijk
 * @author Sander Visser
 */
public class MysqlDatabaseServiceImpl extends BaseDatabaseService {

    @SuppressWarnings("unchecked")
    @Override
    public Connection createConnection(String database, String user, String pass, Tuple<String, String>... options) throws SQLException {
        return connect(createConnectionURL(database, user, pass, options));
    }

    @SuppressWarnings("unchecked")
    @Override
    protected String createConnectionURL(String database, String user, String pass, Tuple<String, String>... options) {
        return createJDBCURL("mysql", database, user, pass, "?", "&", options);
    }

    @Override
    public void loadDriver() throws ClassNotFoundException {
        Class.forName(Database.MYSQL.getDriverClass());
    }
}
