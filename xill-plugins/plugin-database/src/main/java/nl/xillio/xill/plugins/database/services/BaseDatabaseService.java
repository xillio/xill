package nl.xillio.xill.plugins.database.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.xillio.xill.plugins.database.util.StatementIterator;
import nl.xillio.xill.plugins.database.util.Tuple;
import nl.xillio.xill.plugins.database.util.TypeConvertor;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Iterators;

/**
 * The base service for any databaseService.
 */
@SuppressWarnings("unchecked")
public abstract class BaseDatabaseService implements DatabaseService {

	private static final Pattern PARAMETER_PATTERN = Pattern.compile("(?!\\\\):([a-zA-Z]+)");

	/**
	 * Cache for delimiters, prevents from constantly getrting the metadata
	 */
	private LinkedHashMap<Connection, String> delimiter = new LinkedHashMap<>();

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
	public Object query(final Connection connection, final String query, final List<LinkedHashMap<String, Object>> parameters, final int timeout) throws SQLException {
		PreparedStatement stmt = parseNamedParameters(connection, query);
		stmt.setQueryTimeout(timeout);

		if (parameters == null || parameters.isEmpty()) {
			if (!extractParameterNames(query).isEmpty()) {
				throw new IllegalArgumentException("Parameters is empty for parametrised query.");
			}
			stmt.execute();
		} else if (parameters.size() == 1) {
			LinkedHashMap<String, Object> parameter = parameters.get(0);
			fillStatement(parameter, stmt, 1);
			stmt.execute();
		} else {
			// convert int[] to Integer[] to be able to create an iterator.
			Integer[] updateCounts = ArrayUtils.toObject(executeBatch(stmt, extractParameterNames(query), parameters));

			return (Arrays.asList(updateCounts)).iterator();
		}

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
	 * Parse a {@link PreparedStatement} from a query with named parameters
	 *
	 * @param connection
	 * @param query
	 *        The query to parse
	 * @return An unused {@link PreparedStatement}.
	 * @throws SQLException
	 */
	PreparedStatement parseNamedParameters(final Connection connection, final String query) throws SQLException {
		Matcher m = PARAMETER_PATTERN.matcher(query);

		String preparedQuery = m.replaceAll("?");
		return connection.prepareStatement(preparedQuery);
	}

	/**
	 *
	 * @param query
	 * @return The names of the parameters in the given query in order of appearance
	 */
	List<String> extractParameterNames(final String query) {
		Matcher m = PARAMETER_PATTERN.matcher(query);
		List<String> indexedParameters = new ArrayList<>();
		while (m.find()) {
			String paramName = m.group(1);
			indexedParameters.add(paramName);
		}
		return indexedParameters;
	}

	int[] executeBatch(final PreparedStatement stmt, final List<String> indexedParameters, final List<LinkedHashMap<String, Object>> parameters) throws SQLException {
		for (LinkedHashMap<String, Object> parameter : parameters) {
			for (int i = 0; i < indexedParameters.size(); i++) {
				String indexedParameter = indexedParameters.get(i);
				// returns null on null value and when key is not contained in map
				if (!parameter.containsKey(indexedParameter)) {
					throw new IllegalArgumentException("The Parameters argument should contain: \"" + indexedParameter + "\"");
				}
				
				Object value = parameter.get(indexedParameter);
				stmt.setObject(i + 1, value);
			}
			stmt.addBatch();
		}
		return stmt.executeBatch();
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
	 * @throws SQLException
	 *         When a database error occurs
	 */
	protected abstract String createConnectionURL(String database, String user, String pass, Tuple<String, String>... options) throws SQLException;

	/**
	 * Loads the JDBC driver needed for this service to function. Should use {@link Class#forName(String)} in most cases.
	 * 
	 * @throws ClassNotFoundException
	 */
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
	 * @return A URL to connect to the database
	 */
	String createJDBCURL(final String type, final String database, final String user, final String pass, String optionsMarker, String optionsSeparator,final Tuple<String, String>... options) {
		String url = String.format("jdbc:%s://%s", type, database);
		
		// question mark for url parameters
		url = url+optionsMarker;

		// append user
		if (user != null) {
			url = String.format("%suser=%s%s", url, user, optionsSeparator);
		}
		// append password
		if (pass != null) {
			url = String.format("%spassword=%s%s", url, pass, optionsSeparator);
		}
		// append other options
		for (Tuple<String, String> option : options) {
			url = String.format("%s%s=%s%s", url, option.getKey(), option.getValue(), optionsSeparator);
		}
		return url;
	}

	@Override
	public LinkedHashMap<String, Object> getObject(final Connection connection, final String table, final Map<String, Object> constraints) throws SQLException {
		// prepare statement
		final LinkedHashMap<String, Object> notNullConstraints = new LinkedHashMap<>();
		constraints.forEach((k, v) -> {
			if (v != null) {
				notNullConstraints.put(k, v);
			}
		});

		String query = createSelectQuery(connection, table, new ArrayList<String>(notNullConstraints.keySet()));
		PreparedStatement statement = connection.prepareStatement(query);

		// Fill out values
		fillStatement(notNullConstraints, statement, 1);

		// perform query

		ResultSet result = statement.executeQuery();
		ResultSetMetaData rs = result.getMetaData();

		LinkedHashMap<String, Object> map = new LinkedHashMap<>();
		if (result.next()) {

			for (String s : constraints.keySet()) {
				String here = rs.getColumnName(result.findColumn(s));
				Object value = result.getObject(s);
				map.put(here, TypeConvertor.convertJDBCType(value));
			}
			statement.close();
			return map;
		} else {
			statement.close();
			throw new IllegalArgumentException("No objects found with given constraints.");
		}
	}

	void setValue(final PreparedStatement statement, final String key, final Object value, final int i) throws SQLException {
		try {
			// All supported databases allow setObject(i, null), so no setNull needed
			statement.setObject(i, value);
		} catch (Exception e) {
			throw new SQLException("Failed to set value '" + value + "' for column '" + key + "'.", e);
		}
	}

	String createSelectQuery(final Connection connection, final String table, final List<String> keys) throws SQLException {

		// creates WHERE conditions SQL string
		String constraintsSql;
		if (!keys.isEmpty()) {
			constraintsSql = createQueryPart(connection, keys, " AND ");
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
	String createSelectQuery(final String table, final String constraintsSql) {
		return String.format("SELECT * FROM %1$s WHERE %2$s LIMIT 1", table, constraintsSql);
	}

	@Override
	public void storeObject(final Connection connection, final String table, final Map<String, Object> newObject, final List<String> keys, final boolean overwrite)
			throws SQLException {
		if (keys.isEmpty()|| !overwrite) {
			// insert into table
			insertObject(connection, table, newObject);
		} else {
			// update the table
			updateObject(connection, table, newObject, keys);
		}

	}

	void insertObject(final Connection connection, final String table, final Map<String, Object> newObject) throws SQLException {
		
		List<String> escaped = new ArrayList<>();
		for (String key : newObject.keySet()){
			escaped.add(escapeIdentifier(key, connection));
		}
		
		String keyString = StringUtils.join(escaped, ",");
		
		// Create the same number of prepared statement markers as there are keys
		char[] markers = new char[newObject.size()];
		Arrays.fill(markers, '?');
		String valueString = StringUtils.join(markers, ',');

		String sql = "INSERT INTO " + table + " (" + keyString + ") VALUES (" + valueString + ")";

		PreparedStatement statement = connection.prepareStatement(sql);
		fillStatement(newObject, statement, 1);
		statement.execute();
		statement.close();
	}

	void updateObject(final Connection connection, final String table, final Map<String, Object> newObject, final List<String> keys)
			throws SQLException {
		String setString = createQueryPart(connection, newObject.keySet(), ",");
		String whereString = createQueryPart(connection, keys, " AND ");

		String sql = "UPDATE " + table + " SET " + setString + " WHERE " + whereString;

		PreparedStatement statement = connection.prepareStatement(sql);

		fillStatement(newObject, statement, 1);
		LinkedHashMap<String, Object> constraintsValues = new LinkedHashMap<>();
		keys.forEach(e -> constraintsValues.put(e, newObject.get(e)));
		fillStatement(constraintsValues, statement, newObject.size() + 1);

		statement.execute();
		// if no rows were affected by an update, insert a new row
		if (statement.getUpdateCount() == 0) {
			insertObject(connection, table, newObject);
		}
		statement.close();
	}

	/**
	 * Fills a {@link PreparedStatement} from a map
	 *
	 * @param newObject
	 *        The map of which all keys represent columns
	 * @param statement
	 *        Prepared statements with as many '?' markers as entries in the newObject map
	 * @param firstMarkerNumber
	 *        The index of the '?' to start setting values
	 * @throws SQLException
	 */
	void fillStatement(final Map<String, Object> newObject, final PreparedStatement statement, final int firstMarkerNumber) throws SQLException {
		int i = firstMarkerNumber;
		for (Entry<String, Object> e : newObject.entrySet()) {
			setValue(statement, e.getKey(), e.getValue(), i++);
		}
	}

	/**
	 * Create a String in this form (where "," is the separator in this case): "key1 = ?,key2 = ?,key3 = ? "
	 */
	String createQueryPart(Connection connection, final Iterable<String> keys, final String separator) throws SQLException {
		List<String> escaped = new ArrayList<String>();
		for (String identifier : keys) {
			escaped.add(escapeIdentifier(identifier, connection));
		}
		return escaped.stream()
			.map(k -> k + " = ?")
			.reduce((q, k) -> q + separator + k).get();
	}

	String escapeIdentifier(final String identifier, Connection connection) throws SQLException {
		String delimiterString = null;

		if (!delimiter.containsKey(connection)) {
			delimiter.put(connection, connection.getMetaData().getIdentifierQuoteString());
		}
		delimiterString = delimiter.get(connection);

		return delimiterString + identifier + delimiterString;
	}

}
