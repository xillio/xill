package nl.xillio.xill.plugins.mongodb.constructs;


import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.mongodb.services.Connection;
import nl.xillio.xill.plugins.mongodb.services.ConnectionFactory;
import nl.xillio.xill.plugins.mongodb.services.ConnectionManager;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class ConnectConstructTest extends TestUtils {
    @Test
    public void testCreateNewConnection() {
        ConnectionFactory factory = mock(ConnectionFactory.class);
        ConnectionManager connectionManager = new ConnectionManager(factory);
        ConnectConstruct connectConstruct = new ConnectConstruct(connectionManager);

        MetaExpression result = connectConstruct.process(
                fromValue("localhost"),
                fromValue(1423),
                fromValue("database"),
                fromValue("username"),
                fromValue("password123"),
                context);

        assertEquals(result.getStringValue(), "Mongo[username@localhost:1423/database]");
        assertFalse(result.getMeta(Connection.class).isClosed());
    }
}
