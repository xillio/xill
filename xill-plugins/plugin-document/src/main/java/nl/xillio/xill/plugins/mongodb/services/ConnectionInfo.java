package nl.xillio.xill.plugins.mongodb.services;

import java.util.Objects;

/**
 * This class represents the information required to create a MongoDB connection.
 *
 * @author Thomas Biesaart
 * @author Titus Nachbauer
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

    @Override
    public String toString() {
        if (username == null) {
            return String.format("Mongo[%s:%d/%s]", host, port, database);
        }
        return String.format("Mongo[%s@%s:%d/%s]", username, host, port, database);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ConnectionInfo)) {
            return false;
        }

        ConnectionInfo other = (ConnectionInfo) obj;
        return other.host.equals(host) &&
                other.port == port &&
                other.database.equals(database) &&
                (username == null && other.username == null || other.username.equals(username)) &&
                (password == null && other.password == null || other.password.equals(password));
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port, database, username, password);
    }
}
