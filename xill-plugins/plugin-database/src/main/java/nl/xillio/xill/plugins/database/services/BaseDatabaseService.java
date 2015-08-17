package nl.xillio.xill.plugins.database.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import nl.xillio.xill.plugins.database.util.ConnectionMetadata;
import nl.xillio.xill.plugins.database.util.StatementIterator;
import nl.xillio.xill.plugins.database.util.Tuple;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.config.plugins.validation.ConstraintValidator;

import com.google.common.collect.Iterators;

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
		return DriverManager.getConnection(url, properties);
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
	public LinkedHashMap<String,Object> getObject(final Connection connection, final String table, final LinkedHashMap<String, Object> constraints) throws SQLException {
		// prepare statement
		PreparedStatement statement = null;
		LinkedHashMap<String,Object> notNullConstraints = new LinkedHashMap<>
		(constraints.entrySet().stream()
				.filter(e -> e.getValue() != null)
				.collect(Collectors.toMap(e->e.getKey(),e->e.getValue())));
		
		String query = createSelectQuery(table, new ArrayList<String>(notNullConstraints.keySet()));
		statement = connection.prepareStatement(query);

		
		// Fill out values
		fillStatement(notNullConstraints, statement,1);

		// perform query

		ResultSet result = statement.executeQuery();
		ResultSetMetaData rs = result.getMetaData();
		
		LinkedHashMap<String, Object> map = new LinkedHashMap<>();
		if (result.next()) {
			
			
			for (String s : constraints.keySet()) {
				String here = rs.getColumnName(result.findColumn(s));
				Object value = result.getObject(s);
				map.put(here, value);
			}

			return map;
		} else {
			throw new IllegalArgumentException("No objects found with given constraints.");
		}
	}

	private void setValue(final PreparedStatement statement, final String key, final Object value, final int i) throws SQLException {
		try {
			// All supported databases allow setObject(i, null), so no setNull needed
			statement.setObject(i, value);
		} catch (Exception e1) {
			throw new SQLException("Failed to set value '" + value + "' for column '" + key + "'.");
		}
	}

	private String createSelectQuery(final String table, final List<String> keys) {

		// creates WHERE conditions SQL string
		String constraintsSql;
		if (keys.size() > 0) {
			constraintsSql = createQueryPart(keys, " AND ");
		} else {
			constraintsSql = "1";
		}

		// creates entire SQL query according to DB type
		return createSelectQuery(table, constraintsSql);
	}

	/**
	 * Create a select query for getting one row from the database
	 * 
	 * @param constraintsSql
	 *        Constrainsts for selecting (containing AND, OR, etc.)
	 * @param table
	 *        The table to select from
	 * @return A SQL query that selects one row using the given constraints
	 */
	protected String createSelectQuery(String table, String constraintsSql) {
		return String.format("SELECT * FROM %1$s WHERE %2$s LIMIT 1", table, constraintsSql);
	}

	@Override
	public void storeObject(final Connection connection, final String table, final LinkedHashMap<String, Object> newObject, final List<String> keys, final boolean overwrite)
	    throws SQLException {
		PreparedStatement statement;

		if (keys.size() == 0 || !overwrite) {
			// insert into table
			insertObject(connection, table, newObject);
		} else {
			// update the table
			updateObject(connection, table, newObject, keys);
		}

	}

	private void insertObject(final Connection connection, final String table, final LinkedHashMap<String, Object> newObject) throws SQLException {
		String ks = StringUtils.join(newObject.keySet(), ',');

		// Create the same number of prepared statement markers as there are keys
		char[] markers = new char[newObject.size()];
		Arrays.fill(markers, '?');
		String vs = StringUtils.join(markers, ',');

		String sql = "INSERT INTO " + table + " (" + ks + ") VALUES (" + vs + ")";

		PreparedStatement statement = connection.prepareStatement(sql);
		fillStatement(newObject, statement,1);
		statement.execute();
	}

	private void updateObject(final Connection connection, final String table, final LinkedHashMap<String, Object> newObject, final List<String> keys)
	    throws SQLException {
		String ss = createQueryPart(newObject.keySet(), ",");
		String ws = createQueryPart(keys, " AND ");

		String sql = "UPDATE " + table + " SET " + ss + " WHERE " + ws;

		PreparedStatement statement = connection.prepareStatement(sql);
		
		fillStatement(newObject, statement,1);
		LinkedHashMap<String, Object > constraintsValues = new LinkedHashMap<>();
		keys.forEach(e->constraintsValues.put(e, newObject.get(e)));
		fillStatement(constraintsValues, statement,newObject.size() + 1);
		
		statement.execute();
		// if no rows were affected by an update, insert a new row
		if (statement.getUpdateCount() == 0) {
			insertObject(connection, table, newObject);
		}
	}

	/**
	 * Fills a {@link PreparedStatement} from a map
	 * 
	 * @param newObject
	 *        The map of which all keys represent columns
	 * @param statement
	 *        Prepared statements with as many '?' markers as entries in the newObject map
	 * @throws SQLException
	 */
	private void fillStatement(final LinkedHashMap<String, Object> newObject, PreparedStatement statement, int firstMarkerNumber) throws SQLException {
		int i = firstMarkerNumber;
		for (Entry<String, Object> e : newObject.entrySet()) {
			setValue(statement, e.getKey(), e.getValue(), i++);
		}
	}

	/**
	 * Create a String in this form (where "," is the separator in this case): "key1 = ?,key2 = ?,key3 = ? "
	 */
	private String createQueryPart(final Iterable<String> keys, String separator) {
		return StreamSupport.stream(keys.spliterator(), false).map(k -> k + " = ?").reduce((q, k) -> q + separator + k).get();
	}

	public static void setLastConnection(ConnectionMetadata connection) {
		lastConnection = connection;
	}

	public static ConnectionMetadata getLastConnection() {
		return lastConnection;
	}
}
