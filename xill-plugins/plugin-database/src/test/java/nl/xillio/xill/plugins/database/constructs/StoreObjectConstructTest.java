package nl.xillio.xill.plugins.database.constructs;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.mysql.jdbc.Connection;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.database.BaseDatabaseConstruct;
import nl.xillio.xill.plugins.database.services.DatabaseService;
import nl.xillio.xill.plugins.database.services.DatabaseServiceFactory;
import nl.xillio.xill.plugins.database.util.ConnectionMetadata;
import nl.xillio.xill.testutils.ConstructTest;

public class StoreObjectConstructTest extends ConstructTest {

	/**
	 * @test process with normal input and no database given
	 * 
	 */
	@Test(expectedExceptions = RobotRuntimeException.class)
	public void testProcessDatabaseNull() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		// mock
		MetaExpression table = mockExpression(ATOMIC, true, 0, "table");
		MetaExpression object = fromValue(new LinkedHashMap<>());
		MetaExpression keys = fromValue(new ArrayList<MetaExpression>());
		MetaExpression overwrite = mockExpression(ATOMIC, true, 0, "empty");
		MetaExpression database = mockExpression(ATOMIC, true, 0, "databaseName");
		MetaExpression[] args = {table, object, keys, overwrite, database};
		Connection connection = spy(Connection.class);
		DatabaseServiceFactory factory = mock(DatabaseServiceFactory.class);
		RobotID id = mock(RobotID.class);

		when(database.equals(NULL)).thenReturn(true);

		ConnectionMetadata conMetadata = mock(ConnectionMetadata.class);
		when((conMetadata).getDatabaseName()).thenReturn("databaseName");

		BaseDatabaseConstruct.setLastConnections(id, conMetadata);

		DatabaseService dbService = mock(DatabaseService.class);
		when((factory).getService(any())).thenReturn(dbService);

		doThrow(RobotRuntimeException.class).when(dbService).storeObject(null, "table", (LinkedHashMap<String, Object>) extractValue(object), (java.util.List<String>) keys.getValue(), true);

		// run
		MetaExpression result = StoreObjectConstruct.process(args, factory, id);

		// verify
		verify(conMetadata, times(1)).getConnection();
		verify(dbService, times(1)).storeObject(connection, "table", (LinkedHashMap<String, Object>) extractValue(object), (java.util.List<String>) keys.getValue(), true);
		verify(database, never()).getMeta(ConnectionMetadata.class);

		// assert
		Assert.assertEquals(result, NULL);
	}

	/**
	 * @test process with normal input and a database given.
	 * 
	 */
	@Test(expectedExceptions = RobotRuntimeException.class)
	public void testProcessDatabaseNotNull() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		// mock
		MetaExpression table = mockExpression(ATOMIC, true, 0, "table");
		MetaExpression object = fromValue(new LinkedHashMap<>());
		MetaExpression keys = fromValue(new ArrayList<MetaExpression>());
		MetaExpression overwrite = mockExpression(ATOMIC, true, 0, "empty");
		MetaExpression database = mockExpression(ATOMIC, true, 0, "databaseName");
		MetaExpression[] args = {table, object, keys, overwrite, database};
		Connection connection = spy(Connection.class);
		DatabaseServiceFactory factory = mock(DatabaseServiceFactory.class);
		RobotID id = mock(RobotID.class);

		when(database.equals(NULL)).thenReturn(false);

		ConnectionMetadata conMetadata = mock(ConnectionMetadata.class);
		when((conMetadata).getDatabaseName()).thenReturn("databaseName");

		when((database).getMeta(ConnectionMetadata.class)).thenReturn(conMetadata);

		DatabaseService dbService = mock(DatabaseService.class);
		when((factory).getService(any())).thenReturn(dbService);

		doThrow(RobotRuntimeException.class).when(dbService).storeObject(null, "table", (LinkedHashMap<String, Object>) extractValue(object), (java.util.List<String>) keys.getValue(), true);

		// run
		MetaExpression result = StoreObjectConstruct.process(args, factory, id);

		// verify
		verify(conMetadata, times(1)).getConnection();
		verify(dbService, times(1)).storeObject(connection, "table", (LinkedHashMap<String, Object>) extractValue(object), (java.util.List<String>) keys.getValue(), true);
		verify(database, times(1)).getMeta(ConnectionMetadata.class);

		// assert
		Assert.assertEquals(result, NULL);
	}
	
	
	@DataProvider(name = "exceptions")
	private Object[][] allExceptions() {
		return new Object[][] {
				{null, new InstantiationException()},
				{null, new IllegalAccessException()},
				{null, new ClassNotFoundException()}
		};
	}
	
	/**
	 * process should throw a robotruntimeException when getService throws 
	 * 	InstantiationException, IllegalACcesException and ClassnotFoundexception.
	 */
	@Test(dataProvider = "exceptions", expectedExceptions = RobotRuntimeException.class)
	public void testProcessGetServiceExceptions(final Object o, final Exception e) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		// mock
		MetaExpression table = mockExpression(ATOMIC, true, 0, "table");
		MetaExpression object = fromValue(new LinkedHashMap<>());
		MetaExpression keys = fromValue(new ArrayList<MetaExpression>());
		MetaExpression overwrite = mockExpression(ATOMIC, true, 0, "empty");
		MetaExpression database = mockExpression(ATOMIC, true, 0, "databaseName");
		MetaExpression[] args = {table, object, keys, overwrite, database};
		spy(Connection.class);
		DatabaseServiceFactory factory = mock(DatabaseServiceFactory.class);
		RobotID id = mock(RobotID.class);

		when(database.equals(NULL)).thenReturn(false);

		ConnectionMetadata conMetadata = mock(ConnectionMetadata.class);
		when((conMetadata).getDatabaseName()).thenReturn("databaseName");

		when((database).getMeta(ConnectionMetadata.class)).thenReturn(conMetadata);

		DatabaseService dbService = mock(DatabaseService.class);
		when((factory).getService(any())).thenThrow(e);

		doThrow(RobotRuntimeException.class).when(dbService).storeObject(null, "table", (LinkedHashMap<String, Object>) extractValue(object), (java.util.List<String>) keys.getValue(), true);

		StoreObjectConstruct.process(args, factory, id);
	}

	/**
	 * This method should throw an robotrunTimeException when an SQLException occurs in service.getObject
	 */
	@Test(expectedExceptions = RobotRuntimeException.class)
	public void testProcessSQLException() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		// mock
		MetaExpression table = mockExpression(ATOMIC, true, 0, "table");
		MetaExpression object = fromValue(new LinkedHashMap<>());
		MetaExpression keys = fromValue(new ArrayList<MetaExpression>());
		MetaExpression overwrite = mockExpression(ATOMIC, true, 0, "empty");
		MetaExpression database = mockExpression(ATOMIC, true, 0, "databaseName");
		MetaExpression[] args = {table, object, keys, overwrite, database};
		
		DatabaseServiceFactory factory = mock(DatabaseServiceFactory.class);
		RobotID id = mock(RobotID.class);

		when(database.equals(NULL)).thenReturn(false);

		ConnectionMetadata conMetadata = mock(ConnectionMetadata.class);
		when((conMetadata).getDatabaseName()).thenReturn("databaseName");

		when((database).getMeta(ConnectionMetadata.class)).thenReturn(conMetadata);

		DatabaseService dbService = mock(DatabaseService.class);
		when((factory).getService(any())).thenReturn(dbService);

		doThrow(new SQLException()).when(dbService).storeObject(null, "table", (LinkedHashMap<String, Object>) extractValue(object), (java.util.List<String>) keys.getValue(), true);

		StoreObjectConstruct.process(args, factory, id);
	}

}
