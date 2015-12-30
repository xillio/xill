package nl.xillio.xill.plugins.mongodb.services;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import java.util.Collections;
import java.util.List;

/**
 * Factory that builds connections with a {@link MongoClient}.
 *
 * @author Thomas Biesaart
 * @author Titus Nachbauer
 */
public class ConnectionFactory {

    /**
     * Build a connection.
     *
     * @param info the connection info
     * @return the connection
     */
    public Connection build(ConnectionInfo info) {
        return new Connection(createClient(info), info.getDatabase());
    }

    private MongoClient createClient(ConnectionInfo info) {
        ServerAddress address = new ServerAddress(info.getHost(), info.getPort());
        if (info.getUsername() == null) {
            return new MongoClient(address);
        } else {
            List<MongoCredential> credentials = Collections.singletonList(
                    MongoCredential.createCredential(
                            info.getUsername(),
                            info.getDatabase(),
                            info.getPassword().toCharArray()
                    )
            );
            return new MongoClient(address, credentials);
        }
    }
}
