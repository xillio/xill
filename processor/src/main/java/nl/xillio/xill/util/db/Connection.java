package nl.xillio.xill.util.db;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface Connection {
	public String getType();

	public String getConnectionString();

	public ResultSet getObject(final String table, final Map<String, String> constraints) throws SQLException;

	public ResultSet setObject(final String table, final Map<String, String> keyValuePairs, final List<String> keys, final boolean overwrite) throws SQLException;

	public ResultSet insertObject(final String table, final Map<String, String> keyValuePairs) throws SQLException;

	public ResultSet updateObject(final String table, final Map<String, String> keyValuePairs, final List<String> keys, final boolean overwrite, final boolean insert) throws SQLException;

	public ResultSet query(final String query) throws SQLException;

	public ResultSet query(final String query, final int timeout) throws SQLException;

	public int batchQuery(final String query, final int timeout, final List<List<String>> data) throws Exception;

	// public Variable prepareStatementQuery(final String query, final int timeout, List<List<String>> data) throws Exception;

	public ResultSet query(final String preparedStatement, final List<String> parameters, final int timeout) throws SQLException;

	public CallableStatement prepareCall(final String sql) throws SQLException;

	public String getIdentifierQuoteString();

	public String escapeValue(final String value);

	public void connect();

	public void reset();

	public boolean isClosed();

	public void close();

	public void enableTransactions() throws SQLException;

	public void commit() throws SQLException;

	public void rollback() throws SQLException;

	public void disableTransactions() throws SQLException;

	public int getAffectedRows();
}
