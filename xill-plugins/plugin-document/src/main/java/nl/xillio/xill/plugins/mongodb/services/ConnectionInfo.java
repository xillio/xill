package nl.xillio.xill.plugins.mongodb.services;

/**
 *
 */
public class ConnectionInfo {
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;

    public ConnectionInfo(String host, int port, String database) {
        this(host, port, database, null, null);
    }

    public ConnectionInfo(String host, int port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getDatabase() {
        return database;
    }

    public String getPassword() {
        return password;
    }
}
