package nl.xillio.xill.plugins.mongodb.services;

import com.mongodb.MongoClient;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.ConstructContext;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;


public class ConnectionManagerTest {
    @Test
    public void testGetConnectionWithInfo() throws ConnectionFailedException {
        ConnectionFactory factory = mockFactory(1);
        ConnectionManager manager = new ConnectionManager(factory);
        ConnectionInfo info = new ConnectionInfo("localhost", 2345, "database", "username", "password");
        ConstructContext context = context();

        Connection connection = manager.getConnection(context, info);

        assertNotNull(connection);
    }

    @Test
    public void testGetConnectionRecreateClosed() throws ConnectionFailedException {
        ConnectionFactory factory = mockFactory(2);
        ConnectionManager manager = new ConnectionManager(factory);
        ConnectionInfo info = new ConnectionInfo("localhost", 2345, "database", "username", "password");
        ConstructContext context = context();

        // Close the connection
        Connection connection = manager.getConnection(context, info);
        connection.close();
        assertTrue(connection.isClosed());

        // The previous connection is closed so this should create a new connection
        Connection newConnection = manager.getConnection(context, info);
        assertNotSame(newConnection, connection);
        assertFalse(newConnection.isClosed());
    }

    @Test(expectedExceptions = NoSuchConnectionException.class)
    public void testGetNotExistingConnectionWithoutInfo() throws NoSuchConnectionException {
        ConnectionManager manager = new ConnectionManager(null);
        ConstructContext context = context();

        manager.getConnection(context);
    }

    @Test
    public void testGetCachedConnection() throws NoSuchConnectionException, ConnectionFailedException {
        ConnectionFactory factory = mockFactory(2);

        ConnectionManager manager = new ConnectionManager(factory);
        ConnectionInfo info = new ConnectionInfo("localhost", 2345, "database", "username", "password");
        ConstructContext context = context(UUID.randomUUID());

        // Create the connection
        Connection newConnection = manager.getConnection(context, info);

        // Get the connection without info
        Connection otherConnection = manager.getConnection(context);

        assertSame(otherConnection, newConnection);

        // Get the connection as a sub robot
        ConstructContext subRobot = context(context.getCompilerSerialId());
        Connection subRobotConnection = manager.getConnection(subRobot);

        assertSame(subRobotConnection, newConnection);

        // Get the connection as an other robot
        Connection otherRobot = manager.getConnection(context(), info);

        assertNotSame(otherRobot, newConnection);
    }

    private ConnectionFactory mockFactory(int numberOfConnections) {
        Connection[] returnValues = new Connection[numberOfConnections - 1];

        for (int i = 0; i < returnValues.length; i++) {
            returnValues[i] = new Connection(mock(MongoClient.class, RETURNS_DEEP_STUBS), "");
        }

        return mockFactory(new Connection(mock(MongoClient.class, RETURNS_DEEP_STUBS), ""), returnValues);
    }

    private ConnectionFactory mockFactory(Connection firstResult, Connection... result) {
        ConnectionFactory factory = mock(ConnectionFactory.class, RETURNS_DEEP_STUBS);
        when(factory.build(any())).thenReturn(firstResult, result);

        return factory;
    }

    private ConstructContext context() {
        return context(UUID.randomUUID());
    }

    private ConstructContext context(UUID id) {
        return new ConstructContext(RobotID.dummyRobot(), RobotID.dummyRobot(), null, null, id, null, null);
    }
}
