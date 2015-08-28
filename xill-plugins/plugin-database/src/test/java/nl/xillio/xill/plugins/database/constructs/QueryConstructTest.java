package nl.xillio.xill.plugins.database.constructs;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.database.BaseDatabaseConstruct;
import nl.xillio.xill.plugins.database.services.DatabaseService;
import nl.xillio.xill.plugins.database.services.DatabaseServiceFactory;
import nl.xillio.xill.plugins.database.util.ConnectionMetadata;
import nl.xillio.xill.testutils.ConstructTest;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link QueryConstruct}.
 *
 */
public class QueryConstructTest extends ConstructTest {

	/**
	 * <p>
	 * Tests the process with a database and two parameters handed
	 * </p>
	 *
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testProcessWithDatabaseAndNormalUsage() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		// mock

		// the query
		MetaExpression query = mockExpression(ATOMIC, true, 5, "QueryValue");

		// the database
		MetaExpression database = mockExpression(ATOMIC, true, 42, "database");
		ConnectionMetadata connectionMetadata = mock(ConnectionMetadata.class);
		when(database.getMeta(ConnectionMetadata.class)).thenReturn(connectionMetadata);

		// the timeout
		MetaExpression timeout = mockExpression(ATOMIC, true, 42, "timeout");

		// the parameters
		MetaExpression parameters = mock(MetaExpression.class);
		List<MetaExpression> parameterValue = new ArrayList<MetaExpression>();
		MetaExpression firstParameter = mock(MetaExpression.class);
		MetaExpression secondParameter = mock(MetaExpression.class);

		LinkedHashMap<String, Object> value = new LinkedHashMap<String, Object>();
		parameterValue.add(firstParameter);
		parameterValue.add(secondParameter);
		when(parameters.getValue()).thenReturn(parameterValue);
		when(firstParameter.getValue()).thenReturn(value);
		when(secondParameter.getValue()).thenReturn(value);

		// the databaseFactory
		DatabaseServiceFactory factory = mock(DatabaseServiceFactory.class);
		DatabaseService databaseService = mock(DatabaseService.class);
		when(factory.getService(anyString())).thenReturn(databaseService);

		// the robotID
		RobotID robotID = mock(RobotID.class);

		// run
		QueryConstruct.process(query, parameters, database, timeout, factory, robotID);

		// verify
		verify(databaseService, times(1)).query(any(), anyString(), anyList(), anyInt());
	}

	/**
	 * <p>
	 * Test the process when no database is handed and it has to be retrieved from the static storage
	 * </p>
	 *
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testProcessWithNoDatabaseAndNormalUsage() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		// mock

		// the query
		MetaExpression query = mockExpression(ATOMIC, true, 5, "QueryValue");

		// the database
		MetaExpression database = mockExpression(ATOMIC, true, 42, "database");
		when(database.isNull()).thenReturn(true);
		ConnectionMetadata connectionMetadata = mock(ConnectionMetadata.class);

		// the timeout
		MetaExpression timeout = mockExpression(ATOMIC, true, 42, "timeout");

		// the parameters
		MetaExpression parameters = mock(MetaExpression.class);
		List<MetaExpression> parameterValue = new ArrayList<MetaExpression>();
		MetaExpression firstParameter = mock(MetaExpression.class);
		MetaExpression secondParameter = mock(MetaExpression.class);

		LinkedHashMap<String, Object> value = new LinkedHashMap<String, Object>();
		parameterValue.add(firstParameter);
		parameterValue.add(secondParameter);
		when(parameters.getValue()).thenReturn(parameterValue);
		when(firstParameter.getValue()).thenReturn(value);
		when(secondParameter.getValue()).thenReturn(value);

		// the databaseFactory
		DatabaseServiceFactory factory = mock(DatabaseServiceFactory.class);
		DatabaseService databaseService = mock(DatabaseService.class);
		when(factory.getService(anyString())).thenReturn(databaseService);

		// the robotID
		RobotID robotID = mock(RobotID.class);
		BaseDatabaseConstruct.setLastConnections(robotID, connectionMetadata);

		// run
		QueryConstruct.process(query, parameters, database, timeout, factory, robotID);

		// verify
		verify(databaseService, times(1)).query(any(), anyString(), anyList(), anyInt());
	}

	/**
	 * @return
	 *         Returns all possible exceptions when trying to initiate the service.
	 */
	@DataProvider(name = "serviceExceptions")
	private Object[][] getAllServiceExceptions() {
		return new Object[][] {
				{null, new InstantiationException()},
				{null, new IllegalAccessException()},
				{null, new ClassNotFoundException()}
		};
	}

	/**
	 * <p>
	 * Tests the process when the getService throws an exception
	 * </p>
	 * <p>
	 * Note that we hand every possible exception with a {@link DataProvider}
	 * </p>
	 *
	 * @param o
	 *        A random fodder object.
	 * @param e
	 *        The exception we throw.
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	@Test(dataProvider = "serviceExceptions", expectedExceptions = RobotRuntimeException.class)
	public void testProcessWithDatabaseWithFailingToGetService(final Object o, final Exception e) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		// mock

		// the query
		MetaExpression query = mockExpression(ATOMIC, true, 5, "QueryValue");

		// the database
		MetaExpression database = mockExpression(ATOMIC, true, 42, "database");
		when(database.isNull()).thenReturn(true);
		ConnectionMetadata connectionMetadata = mock(ConnectionMetadata.class);

		// the timeout
		MetaExpression timeout = mockExpression(ATOMIC, true, 42, "timeout");

		// the parameters
		MetaExpression parameters = mock(MetaExpression.class);
		List<MetaExpression> parameterValue = new ArrayList<MetaExpression>();
		MetaExpression firstParameter = mock(MetaExpression.class);

		LinkedHashMap<String, Object> value = new LinkedHashMap<String, Object>();
		parameterValue.add(firstParameter);
		when(parameters.getValue()).thenReturn(parameterValue);
		when(firstParameter.getValue()).thenReturn(value);

		// the databaseFactory
		DatabaseServiceFactory factory = mock(DatabaseServiceFactory.class);
		when(factory.getService(anyString())).thenThrow(e);

		// the robotID
		RobotID robotID = mock(RobotID.class);
		BaseDatabaseConstruct.setLastConnections(robotID, connectionMetadata);

		// run
		QueryConstruct.process(query, parameters, database, timeout, factory, robotID);
	}

	/**
	 * @return
	 *         Returns all possible exceptions when trying to execute the query method
	 */
	@DataProvider(name = "queryExceptions")
	private Object[][] getAllQueryExceptions() {
		return new Object[][] {
				{null, new SQLException()},
				{null, new IllegalArgumentException()}
		};
	}

	/**
	 * <p>
	 * tests the process when the query function fails and throws an exception
	 * </p>
	 * <p>
	 * Note that we try every possible exception with a {@link DataProvider}.
	 * </p>
	 *
	 * @param o
	 *        A fodder object.
	 * @param e
	 *        The exception we throw.
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	@SuppressWarnings({"unchecked"})
	@Test(dataProvider = "queryExceptions", expectedExceptions = RobotRuntimeException.class)
	public void testProcessWithDatabaseAndQueryFailure(final Object o, final Exception e) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		// mock

		// the query
		MetaExpression query = mockExpression(ATOMIC, true, 5, "QueryValue");

		// the database
		MetaExpression database = mockExpression(ATOMIC, true, 42, "database");
		ConnectionMetadata connectionMetadata = mock(ConnectionMetadata.class);
		when(database.getMeta(ConnectionMetadata.class)).thenReturn(connectionMetadata);

		// the timeout
		MetaExpression timeout = mockExpression(ATOMIC, true, 42, "timeout");

		// the parameters
		MetaExpression parameters = mock(MetaExpression.class);
		List<MetaExpression> parameterValue = new ArrayList<MetaExpression>();
		MetaExpression firstParameter = mock(MetaExpression.class);
		MetaExpression secondParameter = mock(MetaExpression.class);

		LinkedHashMap<String, Object> value = new LinkedHashMap<String, Object>();
		parameterValue.add(firstParameter);
		parameterValue.add(secondParameter);
		when(parameters.getValue()).thenReturn(parameterValue);
		when(firstParameter.getValue()).thenReturn(value);
		when(secondParameter.getValue()).thenReturn(value);

		// the databaseFactory
		DatabaseServiceFactory factory = mock(DatabaseServiceFactory.class);
		DatabaseService databaseService = mock(DatabaseService.class);
		when(factory.getService(anyString())).thenReturn(databaseService);
		when(databaseService.query(any(), anyString(), anyList(), anyInt())).thenThrow(e);

		// the robotID
		RobotID robotID = mock(RobotID.class);

		// run
		QueryConstruct.process(query, parameters, database, timeout, factory, robotID);
	}

	/**
	 * Tests the returnValue method with an integer handed.
	 */
	@Test
	public void testReturnValueWithInteger() {
		MetaExpression output = QueryConstruct.returnValue(42, "sql");

		Assert.assertEquals(output, fromValue(42));
	}

	/**
	 * Tests the returnValue method with an invalid value handed.
	 */
	@Test
	public void testReturnValueWithNoValidValue() {
		MetaExpression output = QueryConstruct.returnValue(null, "sql");

		Assert.assertEquals(output, NULL);
	}

	/**
	 * Test the returnValue method with an iterator with integers handed.
	 */
	@Test
	public void testReturnValueWithIntegerIterator() {
		Iterator<Integer> iterator = Arrays.asList(5, 10).iterator();

		MetaExpression output = QueryConstruct.returnValue(iterator, "sql");

		Assert.assertEquals(output.getStringValue(), "Results[sql]");
	}

	/**
	 * Test the transformIteratorElement method when it receives an integer.
	 */
	@Test
	public void testTransformIteratorElementWithInteger() {

		MetaExpression output = QueryConstruct.transformIteratorElement(42);

		Assert.assertEquals(output, fromValue(42));
	}

	/**
	 * Test the transformIteratorElement method when it receives a HashMap.
	 */
	@Test
	public void testTransformIteratorElementWithHashMap() {
		LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("key", "value");

		MetaExpression output = QueryConstruct.transformIteratorElement(map);

		Assert.assertEquals(output.getValue().toString(), "{key=\"value\"}");
	}
}
