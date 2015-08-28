package nl.xillio.xill.plugins.database.constructs;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.LinkedHashMap;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.mysql.jdbc.Connection;

import nl.xillio.xill.api.components.AtomicExpression;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.database.BaseDatabaseConstruct;
import nl.xillio.xill.plugins.database.services.DatabaseService;
import nl.xillio.xill.plugins.database.services.DatabaseServiceFactory;
import nl.xillio.xill.plugins.database.util.ConnectionMetadata;
import nl.xillio.xill.testutils.ConstructTest;

/**
 *	Test the {@link ConnectConstruct}
 *
 */
public class ConnectConstructTest extends ConstructTest {

	/**
	 * test the method when all given input is valid. does not throw any exceptions
	 * @throws SQLException 
	 * @throws InstantiationException 
	 * @throws IllegalAccessException 
	 * @throws ClassNotFoundException 
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testProcessNormal() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		// mock
		// mock all the input
		MetaExpression database = mockExpression(ATOMIC, true, 0, "databaseName");
		MetaExpression type = mockExpression(ATOMIC, true, 0, "databaseType");
		MetaExpression user = mockExpression(ATOMIC, true, 0, "user");
		MetaExpression pass = mockExpression(ATOMIC, true, 0, "pass");
		MetaExpression options = mockExpression(OBJECT);
		when(options.getValue()).thenReturn(new LinkedHashMap<>());

		MetaExpression[] args = {database, type, user, pass, options};
		DatabaseServiceFactory factory = mock(DatabaseServiceFactory.class);
		RobotID robotID = mock(RobotID.class);

		// mock the connection and service
		Connection connection = mock(Connection.class);
		DatabaseService dbService = mock(DatabaseService.class);

		when((dbService).createConnection(eq("databaseName"), eq("user"), eq("pass"), any())).thenReturn(connection);
		when((factory).getService("databaseType")).thenReturn(dbService);
		ConnectionMetadata metadata = mock(ConnectionMetadata.class);

		BaseDatabaseConstruct.setLastConnections(robotID, metadata);

		mock(BaseDatabaseConstruct.class);

		// run
		MetaExpression result = ConnectConstruct.process(args, factory, robotID);

		// verify
		verify(dbService, times(1)).createConnection(eq("databaseName"), eq("user"), eq("pass"));
		// verify(bdbc,times(1)).setLastConnections(eq(robotID), any());
		verify(factory, times(1)).getService("databaseType");

		// assert
		Assert.assertEquals(result, new AtomicExpression("databaseName"));

	}

	/**
	 * This test checks whether the method throws an exception when service.createconnection goes wrong.
	 * @throws Throwable 
	 * 
	 * @throws RobotRuntimeException
	 */
	@SuppressWarnings("unchecked")
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "...")
	public void testProcessSQLException() throws Throwable {
		// the args
		MetaExpression database = mockExpression(ATOMIC, true, 0, "databaseName");
		MetaExpression type = mockExpression(ATOMIC, true, 0, "databaseType");
		MetaExpression user = mockExpression(ATOMIC, true, 0, "user");
		MetaExpression pass = mockExpression(ATOMIC, true, 0, "pass");
		MetaExpression options = mockExpression(OBJECT);
		when(options.getValue()).thenReturn(new LinkedHashMap<>());

		MetaExpression[] args = {database, type, user, pass, options};
		
		// the factory and its settings
		DatabaseServiceFactory factory = mock(DatabaseServiceFactory.class);
		DatabaseService dbService = mock(DatabaseService.class);
		when((factory).getService("databaseType")).thenReturn(dbService);
		when((dbService).createConnection(eq("databaseName"), eq("user"), eq("pass"))).thenThrow(new SQLException("..."));
		
		// the robotID
		RobotID robotID = mock(RobotID.class);
		
		// run
		ConnectConstruct.process(args, factory, robotID);
	}

	/**
	 * @return
	 * 				Returns an array containing all exceptions we throw
	 */
	@DataProvider(name = "exceptions")
	private Object[][] allExceptions() {
		return new Object[][] {
				{null, new InstantiationException()},
				{null, new IllegalAccessException()},
				{null, new ClassNotFoundException()}
		};
	}

	/**
	 * This test checks whether the method throws an exception when factory.getService goes wrong.
	 * @param o 
	 * 				Fodder object needed by testNG.
	 * @param e 
	 * 				The exception we throw when getting a service.
	 * @throws Throwable 
	 * 
	 * @throws RobotRuntimeException
	 */
	@Test(dataProvider = "exceptions", expectedExceptions = RobotRuntimeException.class)
	public void testProcessGetServiceException(final Object o, final Exception e) throws Throwable {
		// mock
		
		// the arguments
		MetaExpression database = mockExpression(ATOMIC, true, 0, "databaseName");
		MetaExpression type = mockExpression(ATOMIC, true, 0, "databaseType");
		MetaExpression user = mockExpression(ATOMIC, true, 0, "user");
		MetaExpression pass = mockExpression(ATOMIC, true, 0, "pass");
		MetaExpression options = mockExpression(OBJECT);
		when(options.getValue()).thenReturn(new LinkedHashMap<>());

		MetaExpression[] args = {database, type, user, pass, options};
		
		// the factory and its settings
		DatabaseServiceFactory factory = mock(DatabaseServiceFactory.class);		
		mock(Connection.class);
		mock(DatabaseService.class);
		when((factory).getService("databaseType")).thenThrow(e);
		
		// the robotID
		RobotID robotID = mock(RobotID.class);
		
		// run
		ConnectConstruct.process(args, factory, robotID);
	}
}
