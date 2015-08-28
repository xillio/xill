package nl.xillio.xill.plugins.database.util;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import nl.xillio.xill.plugins.database.util.StatementIterator.StatementIterationException;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test the {@link StatementIterator}
 */
public class StatementIteratorTest {

	/**
	 * Test the retireveNextResult method when no ResultSet is handed.
	 */
	@Test
	public void testRetrieveNextResultWithNoResultSet() {
		// mock
		Statement statement = mock(Statement.class);
		StatementIterator iterator = new StatementIterator(statement);

		// run
		iterator.retrieveNextResult(true);
	}

	/**
	 * test the retrieveNextResult method when a ResultSet is handed.
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testRetrieveNextResultWithResultSet() throws SQLException {
		// mock
		Statement statement = mock(Statement.class);
		ResultSet currentSet = mock(ResultSet.class);
		when(statement.getResultSet()).thenReturn(currentSet);
		StatementIterator iterator = new StatementIterator(statement);

		// run
		iterator.retrieveNextResult(true);
	}

	/**
	 * test the retrieveNextResult method when the ResultSet throws an error.
	 * 
	 * @throws SQLException
	 */
	@Test(expectedExceptions = StatementIterationException.class)
	public void testRetrieveNextResultWithAnErrorThrown() throws SQLException {
		// mock
		Statement statement = mock(Statement.class);
		ResultSet currentSet = mock(ResultSet.class);
		when(statement.getResultSet()).thenReturn(currentSet);
		when(currentSet.next()).thenThrow(new SQLException());
		StatementIterator iterator = new StatementIterator(statement);

		// run
		iterator.retrieveNextResult(true);
	}

	/**
	 * Test the nextResult method.
	 */
	@Test
	public void testNextResultNormalUsage() {
		Statement statement = mock(Statement.class);
		StatementIterator iterator = spy(new StatementIterator(statement));

		when(iterator.hasNext()).thenReturn(true);
		iterator.nextResult();
	}

	/**
	 * Test the next method with a current update
	 */
	@Test
	public void testNextWithCurrentUpdate() {
		// mock
		Statement statement = mock(Statement.class);
		StatementIterator iterator = spy(new StatementIterator(statement));
		iterator.setCurrentUpdateCount(42);

		Object output = iterator.next();

		Assert.assertEquals(output, 42);
	}

	/**
	 * Test the next method with no current update
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testNextWithNoCurrentUpdate() throws SQLException {
		// mock
		Statement statement = mock(Statement.class);

		// the iterator
		StatementIterator iterator = spy(new StatementIterator(statement));
		ResultSetMetaData metadata = mock(ResultSetMetaData.class);
		when(metadata.getColumnCount()).thenReturn(1);
		when(metadata.getColumnName(1)).thenReturn("ColumnName");
		iterator.setCurrentMeta(metadata);
		iterator.setCurrentUpdateCount(-1);

		Object output = iterator.next();

		Assert.assertEquals(output, 0);
	}

	/**
	 * Test the next method with no current update and SQL failure
	 * 
	 * @throws SQLException
	 */
	@Test(expectedExceptions = StatementIterationException.class)
	public void testNextWithNoCurrentUpdateAndSQLException() throws SQLException {
		// mock
		Statement statement = mock(Statement.class);

		// the iterator
		StatementIterator iterator = spy(new StatementIterator(statement));
		ResultSetMetaData metadata = mock(ResultSetMetaData.class);
		when(metadata.getColumnCount()).thenThrow(new SQLException());
		iterator.setCurrentMeta(metadata);
		iterator.setCurrentUpdateCount(-1);

		Object output = iterator.next();

		Assert.assertEquals(output, 0);
	}
}
