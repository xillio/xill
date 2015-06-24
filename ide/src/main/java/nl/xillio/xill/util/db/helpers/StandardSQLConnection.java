package nl.xillio.xill.util.db.helpers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.sql.SQLRecoverableException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nl.xillio.sharedlibrary.lrucache.LRUCache;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;


public abstract class StandardSQLConnection extends DBConnection {

	public final static String USERPREFIX = "USER", SELECTPREFIX = "SELECT", UPDATEPREFIX = "UPDATE", INSERTPREFIX = "INSERT", REPLACEPREFIX = "REPLACE";
	private static final Logger log = Logger.getLogger("AH"); //$NON-NLS-1$
	private final LinkedHashMap<String, PreparedStatement> statementCache2 = new LinkedHashMap<>(50);
	private final LRUCache<String, LinkedList<String>> primaryKeyCache = new LRUCache<>(20);

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

	@Override
	public ResultSet getObject(final String table, final Map<String, String> constraints) throws SQLException {
		String hash = SELECTPREFIX + "-" + table + "-" + constraints.keySet().toString();

		// Prepare statement
		PreparedStatement statement = null;
		if ((statement = statementCache2.get(hash)) != null && (!getDriverName().equals("org.sqlite.JDBC") && !statement.isClosed()) && !statement.getConnection().isClosed()) {
			statement.clearBatch();
		} else {
			StringBuilder qb = new StringBuilder("SELECT * FROM ");
			qb.append(getType().equals("oracle") ? table : "`" + table + "`");

			qb.append(" WHERE");
			if (constraints.size() > 0) {
				for (String k : constraints.keySet()) {
					qb.append(" ").append(escapeIdentifier(k)).append(" = ? AND ");
				}
			} else {
				qb.append("1");
			}

			qb.append(getType().equals("oracle") ? "AND rownum <= 1" : "LIMIT 1");
			String query = qb.toString().replace("AND LIMIT", " LIMIT").replace("AND AND", " AND");

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
		if (keys == null || keys.size() == 0) {
			return insertObject(table, keyValuePairs);
		} else {
			return updateObject(table, keyValuePairs, keys, overwrite, true);
		}
	}

	@Override
	public ResultSet insertObject(final String table, final Map<String, String> keyValuePairs) throws SQLException {
		String inserthash = INSERTPREFIX + "-" + table + "-" + keyValuePairs.keySet().toString();

		// Prepare statement
		PreparedStatement statement = null;

		// Check if the statement is cached and wether the cached version and it's connection are not closed
		if ((statement = statementCache2.get(inserthash)) != null && (!getDriverName().equals("org.sqlite.JDBC") && !statement.isClosed()) && !statement.getConnection().isClosed()) {
			statement.clearParameters();
		} else {
			StringBuilder qb = new StringBuilder("INSERT INTO ");

			qb.append(getType().equals("oracle") ? table : "`" + table + "`");
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

		// System.out.println(statement.toString());

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
		String updatehash = UPDATEPREFIX + "-" + table + "-" + keyValuePairs.keySet().toString() + "-" + keys.toString();
		String selecthash = SELECTPREFIX + "-" + table + "-" + keyValuePairs.keySet().toString() + "-" + keys.toString();
		PreparedStatement selectstatement = null;

		// Check if the statement is cached and whether the cached version and it's connection are not closed
		if ((selectstatement = statementCache2.get(selecthash)) != null && (!getDriverName().equals("org.sqlite.JDBC") && !selectstatement.isClosed()) && !selectstatement.getConnection().isClosed()) {
			try {
				selectstatement.clearParameters();
			} catch (SQLException e) {}
		} else {
			// note oracle doesn't support backticks
			StringBuilder qb = new StringBuilder("SELECT * FROM ");
			qb.append(getType().equals("oracle") ? table : "`" + table + "`");

			qb.append(" WHERE ");
			for (String key : keys) {
				qb.append(" ").append(escapeIdentifier(key)).append(" = ? AND");
			}
			qb.append(") ").append(getType().equals("oracle") ? "AND rownum <= 1" : "LIMIT 1");

			String query = qb.toString().replace("AND)", "");
			selectstatement = getConnection().prepareStatement(query);
			statementCache2.put(selecthash, selectstatement);

		}

		boolean exists = false;

		int i = 1;
		if (keys.size() > 0) {
			for (String key : keys) {
				setValue(selectstatement, key, keyValuePairs.get(key), i++);
			}
		}

		// System.out.println(selectstatement.toString());
		if (selectstatement.execute()) {
			ResultSet rs = selectstatement.getResultSet();
			if (rs.getType() != ResultSet.TYPE_FORWARD_ONLY) {
				exists = rs.first();
			} else // sqlite
			{
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
			} else {
				return null;
			}
		} else {
			if (!overwrite) {
				return null;
			}
		}

		// Step 3: Object exists: update it!
		PreparedStatement updatestatement = null;

		// Check if the statement is cached and wether the cached version and it's connection are not closed
		if ((updatestatement = statementCache2.get(updatehash)) != null && (!getDriverName().equals("org.sqlite.JDBC") && !updatestatement.isClosed()) && !updatestatement.getConnection().isClosed()) {
			updatestatement.clearParameters();
		} else {
			StringBuilder qb = new StringBuilder("UPDATE ");
			qb.append(getType().equals("oracle") ? table : "`" + table + "`");
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

		// System.out.println(updatestatement.toString());
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

	private void setValue(final PreparedStatement statement, final String key, final String value, final int i) throws SQLException {
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

	private void setNull(final PreparedStatement statement, final String key, final int i) throws SQLException {
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
