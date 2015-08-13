package nl.xillio.xill.plugins.database.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.google.common.collect.Iterators;

import nl.xillio.xill.plugins.database.util.ConnectionMetadata;
import nl.xillio.xill.plugins.database.util.StatementIterator;
import nl.xillio.xill.plugins.database.util.Tuple;

@SuppressWarnings("unchecked")
public abstract class BaseDatabaseService implements DatabaseService {

	private static ConnectionMetadata lastConnection;
	
	/**
	 * Create a JDBC {@link Connection} to the database at the given URL
	 * if no URL is given then return the last connection that was made in the robot.
	 *
	 * @param url
	 *        URL to connect to
	 * @return A connection ready to execute queries on
	 * @throws SQLException 
	 */
	protected Connection connect(final String url) throws SQLException {
		return DriverManager.getConnection(url);
	}

	/**
	 * Create a JDBC {@link Connection} to the database at the given URL, using the given properties. Especially useful for connecting to an Oracle database.
	 *
	 * @param url
	 *        URL to connect to
	 * @param properties
	 *        Separate properties object
	 * @return A connection ready to execute queries on
	 *
	 */
	protected Connection connect(final String url, final Properties properties) throws SQLException {
		return DriverManager.getConnection(url,properties);
	}

	@Override
	public Object query(final Connection connection, final String query, final int timeout) throws SQLException {
		Statement stmt = connection.createStatement();
		stmt.setQueryTimeout(timeout);
		stmt.execute(query);

		// If the first result is the only result and an update count simply return that count, otherwise create an iterator over the statement
		int firstCount = stmt.getUpdateCount();
		if (firstCount != -1) {
			boolean more = stmt.getMoreResults();
			int secondCount = stmt.getUpdateCount();
			if (!more && secondCount == -1) {
				return firstCount;
			} else {
				// Append the already retrieved count to a new statement iterator
				return Iterators.concat(Iterators.forArray(firstCount), new StatementIterator(stmt));
			}
		}
		return new StatementIterator(stmt);
	}

	/**
	 * Create a connection string for a JDBC connection
	 *
	 * @param database
	 *        Database name
	 * @param user
	 *        Username
	 * @param pass
	 *        Password
	 * @param options
	 *        JDBC specific options
	 * @return Connection String
	 */
	protected abstract String createConnectionURL(String database, String user, String pass, Tuple<String, String>... options) throws SQLException;

	public abstract void loadDriver() throws ClassNotFoundException;

	/**
	 *
	 * @param type
	 *        JDBC name of the database
	 * @param database
	 *        host, port and database name in the correct format for the JDBC driver
	 * @param user
	 *        can be null
	 * @param pass
	 *        can be null
	 * @param options
	 *        Driver specific options
	 * @return
	 */
	protected String createJDBCURL(final String type, final String database, final String user, final String pass, final Tuple<String, String>... options) {
		String url = String.format("jdbc:%s://%s", type, database);
		// no other parameters, so return
		if (user == null && pass == null && options.length == 0) {
			return url;
		}
		// question mark for url parameters
		url = url.concat("?");
		if (user == null ^ pass == null) {
			throw new IllegalArgumentException("User and pass should be both null or both non-null");
		}
		if (user != null && pass != null) {
			// append username and password options
			url = String.format("%suser=%s&", url, user);
			url = String.format("%spassword=%s&", url, pass);
		}
		// append other options
		for (Tuple<String, String> option : options) {
			url = String.format("%s%s=%s&", url, option.getKey(), option.getValue());
		}
		return url;
	}

 @Override
	public Object getObject(final Connection connection, final String table, final LinkedHashMap<String, Object> constraints, final String name) throws SQLException {
		// prepare statement
		PreparedStatement statement = null;
		String query = createSelectQuery(table, new ArrayList<String>(constraints.keySet()), name);
		statement = connection.prepareStatement(query);

		// Fill out values
		int i = 1;
		for (String key : constraints.keySet()) {
			setValue(statement, key, constraints.get(key).toString(), i++);
		}

		// perform query

		ResultSet result = statement.executeQuery();
		ResultSetMetaData rs = result.getMetaData();
		LinkedHashMap<String, Object> map = new LinkedHashMap<>();
		result.next();

		for (Map.Entry<String, Object> e : constraints.entrySet()) {
			String here = rs.getColumnName(result.findColumn(e.getKey()));
			Object value = result.getObject(e.getKey());
			map.put(here, value);
		}

		return map;
	}

	private void setValue(final PreparedStatement statement, final String key, final String value, final int i) throws SQLException {
		if (value.equals("null")) {
			setNull(statement, key, i);
		} else {
			try {
				statement.setObject(i, value.replaceAll("\"", ""));
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

	private String createSelectQuery(final String table, final List<String> keys, final String name) {

		// creates WHERE conditions SQL string
		StringBuilder qb = new StringBuilder();
		if (keys.size() > 0) {
			for (String k : keys) {
				qb.append(" ").append(k).append(" = ? AND ");
			}
			qb.append("END"); // because of right trim last " AND "
		} else {
			qb.append("1");
		}
		String constraintsSql = qb.toString().replace("AND END", "");

		// creates entire SQL query according to DB type
		String sqlQuery = null;
		switch (name) {
			case "oracle":
				sqlQuery = String.format("SELECT * FROM %1$s WHERE %2$s AND rownum <= 1", table, constraintsSql);
				break;
			case "mssql":
				sqlQuery = String.format("SELECT TOP 1 * FROM %1$s WHERE %2$s", table, constraintsSql);
				break;
			default:// MySQL..
				sqlQuery = String.format("SELECT * FROM %1$s WHERE %2$s LIMIT 1", table, constraintsSql);
		}

		return sqlQuery;
	}

 @Override
	public void storeObject(final Connection connection, final String table, final LinkedHashMap<String, Object> newObject, final List<String> keys, final boolean overwrite, final String name) throws SQLException {
		PreparedStatement statement;

		if (keys.size() == 0) {
			// insert into table
			statement = connection.prepareStatement(insertObject(connection, table, newObject, keys));

		} else {
			// update the table
			statement = connection.prepareStatement(updateObject(connection, table, newObject, keys, overwrite, name));

		}

		statement.execute();
	}

	private String insertObject(final Connection connection, final String table, final Map<String, Object> newObject, final List<String> keys) {
		// insert into table
		StringBuilder keyString = new StringBuilder();
		StringBuilder valueString = new StringBuilder();

		for (Map.Entry<String, Object> e : newObject.entrySet()) {
			keyString.append(e.getKey() + ",");
			valueString.append(e.getValue() + ",");
		}

		keyString.append("END");
		valueString.append("END");

		String ks = keyString.toString().replace(",END", "");
		String vs = valueString.toString().replace(",END", "");

		return "INSERT INTO " + table + " (" + ks + ") VALUES (" + vs + ")";
	}

	private String updateObject(final Connection connection, final String table, final Map<String, Object> newObject, final List<String> keys, final boolean overwrite, final String name) throws SQLException {
		String output = "";
		boolean exists = false;
		PreparedStatement statementExists = null;
		String query = createSelectQuery(table, keys, name);
		statementExists = connection.prepareStatement(query);

		int i = 1;
		for (String key : keys) {
			setValue(statementExists, key, newObject.get(key).toString(), i++);
		}

		if (statementExists.execute()) {
			ResultSet rs = statementExists.getResultSet();
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

		if (exists && overwrite) {
			// actually update one of the things.
			StringBuilder set = new StringBuilder();
			StringBuilder where = new StringBuilder();

			for (Map.Entry<String, Object> e : newObject.entrySet()) {
				set.append(e.getKey() + "=" + newObject.get(e.getKey()) + ",");
			}

			for (String s : keys) {
				where.append(s + "=" + newObject.get(s).toString() + " AND ");
			}
			where.append("END");
			set.append("END");

			String ss = set.toString().replace(",END", "");
			String ws = where.toString().replace("AND END", "");

			output = "UPDATE " + table + " SET " + ss + " WHERE " + ws;
		} else {
			output = insertObject(connection, table, newObject, keys); // create new
		}

		return output;
	}
	
	public static void setLastConnection(ConnectionMetadata connection){
		lastConnection = connection;
	}
	public static ConnectionMetadata getLastConnection(){
		return lastConnection;
	}
}
