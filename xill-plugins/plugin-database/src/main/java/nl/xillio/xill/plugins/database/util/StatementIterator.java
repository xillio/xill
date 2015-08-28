package nl.xillio.xill.plugins.database.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * 
 * Iterates over a {@link Statement}. When the current result of the Statement is a {@link ResultSet},
 * the iterator returns a Map<String,Object> with table column labels (the value of an AS clause if present, else the column name)
 * as key and row values as values.
 * When the current result is an update count it returns a single integer.
 * 
 * The iterator will have a next value until there are no more results in the statement.
 * 
 * 
 * @author Geert Konijnendijk
 * @author Sander Visser
 *
 */
public class StatementIterator implements Iterator<Object> {

	private Statement stmt;

	private ResultSet currentSet;
	private ResultSetMetaData currentMeta;
	private int currentUpdateCount = -1;
	private boolean hasNext;

	/**
	 * Create an iterator over all results of a {@link Statement}. When iterating is finished the statement is closed.
	 * 
	 * @param stmt
	 *        The statement to iterate over
	 */
	public StatementIterator(Statement stmt) {
		this.stmt = stmt;
		retrieveNextResult(true);
		hasNext = currentSet != null || currentUpdateCount != -1;
	}

	/**
	 * Sets the currentUpdateCount or the currentSet depending on the current result of the statement.
	 */
	void retrieveNextResult(boolean resultSetPossible) {
		try {
			if (resultSetPossible)
				currentSet = stmt.getResultSet();
			// If the result is no ResultSet it should be an update count
			if (currentSet == null)
				currentUpdateCount = stmt.getUpdateCount();
			else {
				// Initialise the ResultSet
				currentSet.next();
				currentMeta = currentSet.getMetaData();
			}
		} catch (SQLException e) {
			throw new StatementIterationException(e);
		}
	}

	/**
	 * Moves to the statement's next result and retrieves it
	 */
	void nextResult() {
		if (!hasNext())
			throw new NoSuchElementException("Iterator is empty");
		currentSet = null;
		currentUpdateCount = -1;
		try {
			boolean resultSet = stmt.getMoreResults();
			retrieveNextResult(resultSet);
			// If the next result is no result and no update count, iterating has finished
			hasNext = currentSet != null || currentUpdateCount != -1;
			if (!hasNext)
				stmt.close();
		} catch (SQLException e) {
			throw new StatementIterationException(e);
		}
	}
	
	/**
	 * Sets the update count, needed for testing purposes.
	 * @param value
	 * 					The value we want the currentUpdateCount to have
	 */
	void setCurrentUpdateCount(int value){
		currentUpdateCount = value;
	}
	
	/**
	 * Sets the currentMeta variable, needed for testing purposes.
	 * @param metadata
	 * 					The value we want to currentMeta to have.
	 */
	void setCurrentMeta(ResultSetMetaData metadata){
		currentMeta = metadata;
	}

	@Override
	public boolean hasNext() {
		return hasNext;
	}

	@Override
	public Object next() {
		if (currentUpdateCount != -1) {
			int result = currentUpdateCount;
			// Immediately move to the next result since there's only one update count per result
			nextResult();
			return result;
		}
		else {
			try {

				Map<String, Object> result = new LinkedHashMap<>();

				// Build the resulting map using all column labels
				for (int i = 1; i <= currentMeta.getColumnCount(); i++) {
					String columnLabel = currentMeta.getColumnLabel(i);
					Object obj = currentSet.getObject(columnLabel);
					result.put(columnLabel, TypeConvertor.convertJDBCType(obj));
				}

				// Advance the ResultSet
				currentSet.next();

				// Move to the next result when the set is empty
				if (currentSet.isAfterLast())
					nextResult();

				return result;
			} catch (SQLException e) {
				throw new StatementIterationException(e);
			}

		}
	}

	/**
	 * Thrown when a problem arises while iterating over a {@link Statement} in a {@link StatementIterator}.
	 * 
	 * @author Geert Konijnendijk
	 * @author Sander Visser
	 *
	 */
	@SuppressWarnings("javadoc")
	public static class StatementIterationException extends RuntimeException {

		private static final long serialVersionUID = -2585302170331144436L;

		public StatementIterationException() {
			super();
		}

		public StatementIterationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
			super(message, cause, enableSuppression, writableStackTrace);
		}

		public StatementIterationException(String message, Throwable cause) {
			super(message, cause);
		}

		public StatementIterationException(String message) {
			super(message);
		}

		public StatementIterationException(Throwable cause) {
			super(cause);
		}

	}
}
