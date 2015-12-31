package nl.xillio.xill.plugins.database.services;

import nl.xillio.xill.plugins.database.util.Database;
import nl.xillio.xill.plugins.database.util.Tuple;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Database service for SQLite
 *
 * @author Geert Konijnendijk
 */
public class SQLiteDatabaseServiceImpl extends BaseDatabaseService {

    @SuppressWarnings("unchecked")
    @Override
    public Connection createConnection(String database, String user, String pass, Tuple<String, String>... options) throws SQLException {
        return connect(createConnectionURL(database, user, pass, options));
    }

    @SuppressWarnings("unchecked")
    @Override
    protected String createConnectionURL(String database, String user, String pass, Tuple<String, String>... options) throws SQLException {
        String path = database == null ? ":memory:" : database;
        return String.format("jdbc:sqlite:%s", path);
    }

    @Override
    public void loadDriver() throws ClassNotFoundException {
        Class.forName(Database.SQLITE.getDriverClass());
    }

    @Override
    public String escapeString(String unescaped) {
        // SQLite does not escape with backslashes.
        String escaped = unescaped;
        escaped = escaped.replaceAll("'", "''");
        return escaped;
    }
}
