package nl.xillio.xill.plugins.document.data;


import nl.xillio.udm.services.UDMService;
import nl.xillio.udm.util.Require;

/**
 * This class represents a connection to the UDM inside a capsule for the {@link nl.xillio.xill.plugins.document.services.ConnectionPool}.
 *
 * @author Thomas Biesaart
 */
public class UDMConnection implements AutoCloseable {
    private UDMService udm;
    private String identity;

    public UDMConnection(String identity, UDMService udm) {
        Require.notNull(identity, udm);

        this.identity = identity;
        this.udm = udm;
    }

    @Override
    public void close() {
        udm.close();
    }

    public String getIdentity() {
        return identity;
    }

    public UDMService getUdm() {
        return udm;
    }
}
