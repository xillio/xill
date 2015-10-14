package nl.xillio.xill.plugins.database.constructs;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.mysql.jdbc.Connection;

import nl.xillio.xill.ConstructTest;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.database.BaseDatabaseConstruct;
import nl.xillio.xill.plugins.database.services.DatabaseService;
import nl.xillio.xill.plugins.database.services.DatabaseServiceFactory;
import nl.xillio.xill.plugins.database.util.ConnectionMetadata;

/**
 * Test the {@link StoreObjectConstruct}.
 *
 */
public class StoreObjectConstructTest extends ConstructTest {

	/**
	 * @throws ReflectiveOperationException
	 * @throws SQLException
	 * @test process with normal input and no database given
	 *
	 */
	@SuppressWarnings("unchecked")
	@Test(expectedExceptions = RobotRuntimeException.class)
	public void testProcessDatabaseNull() throws ReflectiveOperationException, SQLException {
		// mock
		MetaExpression table = mockExpression(ATOMIC, true, 0, "table");
		MetaExpression object = fromValue(new LinkedHashMap<>());
		MetaExpression keys = fromValue(new ArrayList<MetaExpression>());
		MetaExpression overwrite = mockExpression(ATOMIC, true, 0, "empty");
		MetaExpression database = mockExpression(ATOMIC, true, 0, "databaseName");
		MetaExpression[] args = {table, object, keys, overwrite, database};
		DatabaseServiceFactory factory = mock(DatabaseServiceFactory.class);
		RobotID id = mock(RobotID.class);

		when(database.equals(NULL)).thenReturn(true);

		ConnectionMetadata conMetadata = mock(ConnectionMetadata.class);
		when((conMetadata).getDatabaseName()).thenReturn("databaseName");

		BaseDatabaseConstruct.setLastConnection(id, conMetadata);

		DatabaseService dbService = mock(DatabaseService.class);
		when((factory).getService(any())).thenReturn(dbService);

		doThrow(RobotRuntimeException.class).when(dbService).storeObject(null, "table", (LinkedHashMap<String, Object>) extractValue(object), (java.util.List<String>) keys.getValue(), true);

		// run
		StoreObjectConstruct.process(args, factory, id);
	}

	/**
	 * @throws ReflectiveOperationException
	 * @throws SQLException
	 * @test process with normal input and a database given.
	 *
	 */
	@SuppressWarnings("unchecked")
	@Test(expectedExceptions = RobotRuntimeException.class)
	public void testProcessDatabaseNotNull() throws ReflectiveOperationException, SQLException {
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

		doThrow(RobotRuntimeException.class).when(dbService).storeObject(null, "table", (LinkedHashMap<String, Object>) extractValue(object), (java.util.List<String>) keys.getValue(), true);

		// run
		StoreObjectConstruct.process(args, factory, id);
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
	 * <p>
	 * process should throw a robotruntimeException when getService throws:
	 * </p>
	 * <ul>
	 * <li>InstantiationException</li>
	 * <li>IllegalACcesException</li>
	 * <li>ClassnotFoundexception.</li>
	 * 
	 * @param o
	 *        Fodder object for testNG.
	 * @param e
	 *        The exception we throw.
	 * @throws ReflectiveOperationException
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	@Test(dataProvider = "exceptions", expectedExceptions = RobotRuntimeException.class)
	public void testProcessGetServiceExceptions(final Object o, final Exception e) throws ReflectiveOperationException, SQLException {
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

		// run
		StoreObjectConstruct.process(args, factory, id);
	}

	/**
	 * This method should throw an robotrunTimeException when an SQLException occurs in service.getObject
	 * 
	 * @throws ReflectiveOperationException
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	@Test(expectedExceptions = RobotRuntimeException.class)
	public void testProcessSQLException() throws ReflectiveOperationException, SQLException {
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
