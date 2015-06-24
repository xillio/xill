package nl.xillio.xill.util.db.helpers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.sql.SQLRecoverableException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * This class represents the base class for all SQL database connection
 */
public abstract class StandardSQLConnection extends DBConnection {

	private final static String SELECT_PREFIX = "SELECT", UPDATE_PREFIX = "UPDATE", INSERT_PREFIX = "INSERT";
	private static final Logger log = Logger.getLogger("AH");
	private final LinkedHashMap<String, PreparedStatement> statementCache2 = new LinkedHashMap<>(50);

	public StandardSQLConnection(final String type, final String driverName, final String connectionString) throws SQLException {
		super(type, driverName, connectionString);
	}

	@Override
	public boolean isClosed() {
		try {
			return getConnection().isClosed();
		} catch (Exception e) {
			return true;
		}
	}

	private String createSelectObjectQuery(final String tableName, final List<String> keys) {

		// creates WHERE conditions SQL string
		StringBuilder qb = new StringBuilder();
		if (keys.size() > 0) {
			for (String k : keys) {
				qb.append(" ").append(escapeIdentifier(k)).append(" = ? AND ");
			}
			qb.append("END"); // because of right trim last " AND "
		} else {
			qb.append("1");
		}
		String constraintsSql = qb.toString().replace("AND END", "");

		// creates entire SQL query according to DB type
		String sqlQuery = null;
		switch (getType()) {
			case "oracle":
				sqlQuery = String.format("SELECT * FROM %1$s WHERE %2$s AND rownum <= 1", tableName, constraintsSql);
				break;
			case "mssql":
				sqlQuery = String.format("SELECT TOP 1 * FROM %1$s WHERE %2$s", tableName, constraintsSql);
				break;
			default:// MySQL..
				sqlQuery = String.format("SELECT* FROM `%1$s` WHERE %2$s LIMIT 1", tableName, constraintsSql);
		}

		return sqlQuery;
	}

	@Override
	public ResultSet getObject(final String table, final Map<String, String> constraints) throws SQLException {
		String hash = SELECT_PREFIX + "-" + table + "-" + constraints.keySet().toString();

		// Prepare statement
		PreparedStatement statement = null;
		if ((statement = statementCache2.get(hash)) != null && (!getDriverName().equals("org.sqlite.JDBC") && !statement.isClosed()) && !statement.getConnection().isClosed()) {
			statement.clearBatch();
		} else {
			String query = createSelectObjectQuery(table, new ArrayList<String>(constraints.keySet()));
			statement = getConnection().prepareStatement(query);
		}
		statementCache2.put(hash, statement); // Always push the statement back into the LRUCache to ensure it doesn't get popped in the process.

		// Fill out values
		int i = 1;
		for (String key : constraints.keySet()) {
			setValue(statement, key, constraints.get(key), i++);
		}

		// Perform query
		try {
			return (statement.executeQuery());
		} catch (SQLNonTransientConnectionException e) {
			// Statement was faulty/closed. Remove existing statement from cache and retry.
			statementCache2.remove(hash);
			return getObject(table, constraints);
		}

	}

	@Override
	public ResultSet setObject(final String table, final Map<String, String> keyValuePairs, final List<String> keys, final boolean overwrite) throws SQLException {
		if (keys == null || keys.isEmpty()) {
			return insertObject(table, keyValuePairs);
		}

		return updateObject(table, keyValuePairs, keys, overwrite, true);

	}

	@Override
	public ResultSet insertObject(final String table, final Map<String, String> keyValuePairs) throws SQLException {
		String inserthash = INSERT_PREFIX + "-" + table + "-" + keyValuePairs.keySet().toString();

		// Prepare statement
		PreparedStatement statement;

		// Check if the statement is cached and whether the cached version and it's connection are not closed
		if ((statement = statementCache2.get(inserthash)) != null && (!getDriverName().equals("org.sqlite.JDBC") && !statement.isClosed()) && !statement.getConnection().isClosed()) {
			statement.clearParameters();
		} else {
			StringBuilder qb = new StringBuilder("INSERT INTO ");

			qb.append(getType().equals("oracle") || getType().equals("mssql") ? table : "`" + table + "`");
			qb.append(" (");

			for (String k : keyValuePairs.keySet()) {
				qb.append(escapeIdentifier(k)).append(", ");
			}
			qb.append(") VALUES (").append(StringUtils.repeat("?, ", keyValuePairs.keySet().size())).append(")");
			String query = qb.toString().replace(", )", ")");

			if (!getDriverName().equals("org.sqlite.JDBC")) {
				statement = getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			} else {
				statement = getConnection().prepareStatement(query);
			}
		}
		statementCache2.put(inserthash, statement);

		// Fill out values
		int i = 1;
		for (String key : keyValuePairs.keySet()) {
			setValue(statement, key, keyValuePairs.get(key), i++);
		}

		// Execute query
		try {
			statement.execute();
			try {
				return (statement.getGeneratedKeys());
			} catch (NullPointerException e) {
				return null;
			}
		} catch (SQLNonTransientConnectionException e) {
			// Statement was faulty/closed. Remove existing statement from cache and retry.
			statementCache2.remove(inserthash);
			return insertObject(table, keyValuePairs);
		}
	}

	@Override
	public ResultSet updateObject(final String table, final Map<String, String> keyValuePairs, final List<String> keys, final boolean overwrite, final boolean insert) throws SQLException {
		// Step 1: Check if the object already exists, but only if we are
		String updatehash = UPDATE_PREFIX + "-" + table + "-" + keyValuePairs.keySet().toString() + "-" + keys.toString();
		String selecthash = SELECT_PREFIX + "-" + table + "-" + keyValuePairs.keySet().toString() + "-" + keys.toString();
		PreparedStatement selectstatement = null;

		// Check if the statement is cached and whether the cached version and it's connection are not closed
		if ((selectstatement = statementCache2.get(selecthash)) != null && (!getDriverName().equals("org.sqlite.JDBC") && !selectstatement.isClosed()) && !selectstatement.getConnection().isClosed()) {
			try {
				selectstatement.clearParameters();
			} catch (SQLException e) {}
		} else {
			String query = createSelectObjectQuery(table, keys);

			selectstatement = getConnection().prepareStatement(query);
			statementCache2.put(selecthash, selectstatement);
		}

		boolean exists = false;

		int i = 1;
		if (!keys.isEmpty()) {
			for (String key : keys) {
				setValue(selectstatement, key, keyValuePairs.get(key), i++);
			}
		}

		// System.out.println(selectstatement.toString());
		if (selectstatement.execute()) {
			ResultSet rs = selectstatement.getResultSet();
			if (rs.getType() != ResultSet.TYPE_FORWARD_ONLY) {
				exists = rs.first();
			} else { // sqlite
				while (rs.next()) {
					exists = true;
					break;
				}
			}
		}

		// Step 2: If the object does not exist in the database: insert it
		if (!exists) {
			if (insert) {
				return insertObject(table, keyValuePairs);
			}
			return null;
		}
		if (!overwrite) {
			return null;
		}

		// Step 3: Object exists: update it!
		PreparedStatement updatestatement = null;

		// Check if the statement is cached and wether the cached version and it's connection are not closed
		if ((updatestatement = statementCache2.get(updatehash)) != null && (!getDriverName().equals("org.sqlite.JDBC") && !updatestatement.isClosed()) && !updatestatement.getConnection().isClosed()) {
			updatestatement.clearParameters();
		} else {
			StringBuilder qb = new StringBuilder("UPDATE ");
			qb.append(getType().equals("oracle") || getType().equals("mssql") ? table : "`" + table + "`");
			qb.append(" SET ");

			for (String key : keyValuePairs.keySet()) {
				qb.append(escapeIdentifier(key)).append(" = ?, ");
			}
			qb.append("WHERE");
			for (String key : keys) {
				qb.append(" ").append(escapeIdentifier(key)).append(" = ? AND");
			}
			qb.append(")");
			String query = qb.toString().replace(", WHERE", " WHERE").replace("AND)", "");

			if (!getDriverName().equals("org.sqlite.JDBC")) {
				updatestatement = getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			} else {
				updatestatement = getConnection().prepareStatement(query);
			}
			statementCache2.put(updatehash, updatestatement);

		}

		int j = 1;
		for (String key : keyValuePairs.keySet()) {
			setValue(updatestatement, key, keyValuePairs.get(key), j++);
		}

		// WHERE clause
		for (String key : keys) {
			setValue(updatestatement, key, keyValuePairs.get(key), j++);
		}

		try {
			updatestatement.execute();
			return (updatestatement.getGeneratedKeys());
		} catch (NullPointerException e) {
			if (e.getStackTrace().length > 0 && e.getStackTrace()[0].getMethodName().equals("setConnection")) {
				// Some weird random bug in JDBC: retry
				statementCache2.remove(updatehash);
				return updateObject(table, keyValuePairs, keys, overwrite, insert);
			}

			System.err.println("StandardSQLConnection.updateObject(): A nullpointer error occurred: \n" + "table: " + table + "\n" + "keyValuePairs: " + Arrays.toString(keyValuePairs.entrySet().toArray())
				+ "\n" + "keys: " + Arrays.toString(keys.toArray()));
			e.printStackTrace();
			return null;

		} catch (SQLNonTransientConnectionException e) {
			// Statement was faulty/closed. Remove existing statement from cache and retry.
			statementCache2.remove(updatehash);
			return updateObject(table, keyValuePairs, keys, overwrite, insert);
		}
	}

	// /**
	// * Function runs 'select' prepared statement query - for multiple input rows and merge all resultset for each input row and returns one result
	// *
	// * @param query
	// * SQL select query with ? signs
	// * @param timeout
	// * Query timeout
	// * @param data
	// * List of lists of string which represents the input data to query
	// * @return Merged result from all resultsets as ListVariable
	// * @throws Exception
	// * When any of row operation fails
	// */
	// @Override
	// public Variable prepareStatementQuery(final String query, final int timeout, final List<List<String>> data) throws Exception {
	//
	// ListVariable resultVar = new ListVariable();
	//
	// PreparedStatement statement = getConnection().prepareStatement(query);
	// if (timeout > 0) {
	// statement.setQueryTimeout(timeout);
	// }
	//
	// for (List<String> values : data) {
	// int index = 1;
	// for (String val : values) {
	// setValue(statement, "n/a", val, index);
	// index++;
	// }
	//
	// ResultSet rs = statement.executeQuery();
	// ResultSetMetaData meta = rs.getMetaData();
	//
	// if (rs.getType() != ResultSet.TYPE_FORWARD_ONLY) {
	// rs.beforeFirst();
	// }
	//
	// if (meta.getColumnCount() == 1) {
	// while (rs.next()) {
	// Variable row = SQL_ResultVariable.processResult(1, meta, rs);
	// resultVar.addVariable(row);
	// }
	// } else {
	// while (rs.next()) {
	// ListVariable row = new ListVariable();
	// for (int i = 1; i <= meta.getColumnCount(); i++) {
	// row.addVariable(meta.getColumnLabel(i).toLowerCase(), SQL_ResultVariable.processResult(i, meta, rs));
	// }
	// resultVar.addVariable(row);
	// }
	// }
	// }
	//
	// return resultVar;
	// }

	/**
	 * Function does batch data processing using prepared statement query
	 *
	 * @param query
	 *        SQL insert/update/delete query with ? signs
	 * @param timeout
	 *        Query timeout
	 * @param data
	 *        List of lists of string which represents the input data to query
	 * @return Total number of rows affected
	 * @throws Exception
	 *         When any of row operation fails
	 */
	@Override
	public int batchQuery(final String query, final int timeout, final List<List<String>> data) throws Exception {

		PreparedStatement statement = getConnection().prepareStatement(query);
		if (timeout > 0) {
			statement.setQueryTimeout(timeout);
		}

		for (List<String> values : data) {
			int i = 1;
			for (String val : values) {
				setValue(statement, "n/a", val, i);
				i++;
			}
			statement.addBatch();
		}

		int total = 0, unknw = 0, failed = 0;
		int[] batchResult = statement.executeBatch();
		for (int val : batchResult) {
			if (val >= 0) {
				total += val;
			} else if (val == Statement.SUCCESS_NO_INFO) {
				unknw++;
			} else if (val == Statement.EXECUTE_FAILED) {
				failed++;
			}
		}

		if (failed > 0) {
			throw new Exception(String.format("Number of batch failures: %1$d, success: %2$d, unknown: %3$d", failed, total, unknw));
		}
		// else

		if (unknw > 0) {
			log.warn(String.format("Function query (batch) - number of unknown results: %1$d", unknw));
		}

		return total;
	}

	@Override
	public ResultSet query(final String query) throws SQLException {
		return query(query, 0);
	}

	@Override
	public ResultSet query(final String query, final int timeout) throws SQLException {
		Statement statement = null;
		boolean bResults;

		try {
			statement = getConnection().createStatement();
			if (timeout > 0) {
				statement.setQueryTimeout(timeout);
			}
			bResults = statement.execute(query);
		} catch (SQLRecoverableException e) {
			// Reset connection and retry if the error appears to be recoverable.
			statement = resetConnection().createStatement();
			if (timeout > 0) {
				statement.setQueryTimeout(timeout);
			}
			bResults = statement.execute(query);
		}

		if (bResults) {
			ResultSet rs = statement.getResultSet();
			if (rs.getType() != ResultSet.TYPE_FORWARD_ONLY) {
				if (!rs.first()) {
					return null;
				}
				rs.beforeFirst();
			}
			return rs;
		} else {
			affectedRows = statement.getUpdateCount();
			return null;
		}

	}

	@Override
	public ResultSet query(final String query, final List<String> parameters, final int timeout) throws SQLException {
		PreparedStatement statement = null;
		boolean bResults;

		try {
			statement = getConnection().prepareStatement(query);

			if (timeout > 0) {
				statement.setQueryTimeout(timeout);
			}

			int i = 1;
			for (String param : parameters) {
				statement.setObject(i++, param);
			}

			bResults = statement.execute();
		} catch (SQLRecoverableException e) {
			throw new SQLException("Recoverable SQL Error: " + e.getMessage());
		}

		if (bResults) {
			ResultSet rs = statement.getResultSet();
			if (rs.getType() != ResultSet.TYPE_FORWARD_ONLY) {
				if (!rs.first()) {
					return null;
				}
				rs.beforeFirst();
			}
			return rs;
		} else {
			affectedRows = statement.getUpdateCount();
			return null;
		}
	}

	private String delimiter = null;

	public String escapeIdentifier(final String identifier) {
		if (delimiter == null) {
			try {
				delimiter = getConnection().getMetaData().getIdentifierQuoteString();
			} catch (SQLException e1) {
				delimiter = "";
			}
		}
		return delimiter + identifier + delimiter;
	}

	private static void setValue(final PreparedStatement statement, final String key, final String value, final int i) throws SQLException {
		if (value == null) {
			setNull(statement, key, i);
		} else {
			try {
				statement.setObject(i, value.toString());
			} catch (Exception e1) {
				throw new SQLException("Failed to set value '" + value + "' for column '" + key + "'.");
			}
		}
	}

	private static void setNull(final PreparedStatement statement, final String key, final int i) throws SQLException {
		try {
			statement.setString(i, null);
		} catch (SQLException e) {
			for (int type : new int[] {Types.INTEGER, Types.DECIMAL, Types.DOUBLE, Types.BOOLEAN, Types.BIGINT, Types.TINYINT, Types.FLOAT}) {
				try {
					statement.setNull(i, type);
					break;
				} catch (SQLException e1) {
					throw new SQLException("Datatype for column '" + key + "' not implemented.");
				}
			}
		}
	}

	@Override
	public void finalize() throws Throwable {
		for (Statement statement : statementCache2.values()) {
			try {
				statement.close();
			} catch (SQLException e) {}
		}
	}

	/**
	 * Pretty prints details about a SQL Exception.
	 *
	 * @param ex
	 *        the exception
	 */
	public static void logSQLException(final SQLException ex) {
		for (Throwable e : ex) {
			if (e instanceof SQLException) {

				e.printStackTrace(System.err);
				log.error("SQLState: " + ((SQLException) e).getSQLState());
				log.error("Error Code: " + ((SQLException) e).getErrorCode());
				log.error("Message: " + e.getMessage());

				Throwable t = ex.getCause();
				while (t != null) {
					log.error("Cause: " + t);
					t = t.getCause();
				}
			}
		}
	}

}
