package nl.xillio.xill.plugins.mongodb.services;

import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

public class ConnectionFactoryTest {
    @Test
    public void testGetConnection() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        ConnectionInfo info = new ConnectionInfo("localhost", 2345, "database");

        Connection connection = connectionFactory.build(info);

        assertNotNull(connection);
        assertNotNull(connection.getDatabase());
        assertFalse(connection.isClosed());
    }
}
