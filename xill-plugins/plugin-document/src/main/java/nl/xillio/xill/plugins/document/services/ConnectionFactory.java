package nl.xillio.xill.plugins.document.services;

import com.google.inject.ImplementedBy;
import nl.xillio.udm.UDM;
import nl.xillio.udm.services.UDMService;
import nl.xillio.xill.plugins.document.data.UDMConnection;

/**
 * This class is responsible for creating connections.
 *
 * @author Thomas Biesaart
 */
public class ConnectionFactory {

    /**
     * Build a new connection.
     *
     * @param identity the identity of this connection
     * @return the connection
     */
    public UDMConnection build(String identity) {
        UDMService udm = UDM.connect();
        return new UDMConnection(identity, udm);
    }
}
