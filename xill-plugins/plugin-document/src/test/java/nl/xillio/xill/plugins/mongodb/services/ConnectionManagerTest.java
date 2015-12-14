package nl.xillio.xill.plugins.mongodb.services;

import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.ConstructContext;
import org.testng.annotations.Test;

import java.io.File;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;


public class ConnectionManagerTest {
    @Test
    public void testGetConnectionWithInfo() {
        ConnectionFactory factory = mockFactory(new Connection(null));
        ConnectionManager manager = new ConnectionManager(factory);
        ConnectionInfo info = new ConnectionInfo("localhost", 2345, "database", "username", "password");
        ConstructContext context = context(RobotID.dummyRobot(), RobotID.dummyRobot());

        Connection connection = manager.getConnection(context, info);

        assertNotNull(connection);
    }

    @Test
    public void testGetConnectionRecreateClosed() {
        ConnectionFactory factory = mockFactory(new Connection(null), new Connection(null));
        ConnectionManager manager = new ConnectionManager(factory);
        ConnectionInfo info = new ConnectionInfo("localhost", 2345, "database", "username", "password");
        ConstructContext context = context(RobotID.dummyRobot(), RobotID.dummyRobot());

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
        ConstructContext context = context(RobotID.dummyRobot(), RobotID.dummyRobot());

        manager.getConnection(context);
    }

    @Test
    public void testGetCachedConnection() throws NoSuchConnectionException {
        ConnectionFactory factory = mockFactory(new Connection(null), new Connection(null));

        ConnectionManager manager = new ConnectionManager(factory);
        ConnectionInfo info = new ConnectionInfo("localhost", 2345, "database", "username", "password");
        ConstructContext context = context(RobotID.dummyRobot(), RobotID.dummyRobot());

        // Create the connection
        Connection newConnection = manager.getConnection(context, info);

        // Get the connection without info
        Connection otherConnection = manager.getConnection(context);

        assertSame(otherConnection, newConnection);

        // Get the connection as a sub robot
        ConstructContext subRobot = context(RobotID.getInstance(new File("."), new File("Other Robot")), context.getRootRobot());
        Connection subRobotConnection = manager.getConnection(subRobot);

        assertSame(subRobotConnection, newConnection);

        // Get the connection as an other robot
        RobotID weirdID = RobotID.getInstance(new File("Unit Test"), new File("Special Robot"));
        Connection otherRobot = manager.getConnection(context(weirdID, weirdID), info);

        assertNotSame(otherRobot, newConnection);
    }

    private ConnectionFactory mockFactory(Connection firstResult, Connection... result) {
        ConnectionFactory factory = mock(ConnectionFactory.class, RETURNS_DEEP_STUBS);
        when(factory.build(any())).thenReturn(firstResult, result);

        return factory;
    }

    private ConstructContext context(RobotID robot, RobotID rootRobot) {
        return new ConstructContext(robot, rootRobot, null, null, null, null);
    }
}
