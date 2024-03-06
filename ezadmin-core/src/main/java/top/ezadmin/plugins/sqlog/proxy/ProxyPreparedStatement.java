package top.ezadmin.plugins.sqlog.proxy;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import top.ezadmin.plugins.sqlog.po.JdbcMethod;
import top.ezadmin.plugins.sqlog.po.Params;
import top.ezadmin.plugins.sqlog.po.Monitor;

/**
 *
 * @author ezadmin
 */
public class ProxyPreparedStatement implements PreparedStatement {

	private String preSQL = null;
	private Params param;
	private Map<Integer, Object> parameters = new ConcurrentHashMap<Integer, Object>();
	private PreparedStatement innerPreparedStatement;
	private Connection innerConnection;

	protected void setParamArray(Integer parameterIndex, Object obj) {
		try {
			parameters.put(parameterIndex, obj);
		} catch (Exception e) {
			// ignor
			parameters.put(parameterIndex, "");
		}
	}

	public ProxyPreparedStatement(Params param, PreparedStatement prepareStatement, Connection innerConnection,
			String sql) {
		innerPreparedStatement = prepareStatement;
		preSQL = sql;
		this.innerConnection = innerConnection;
		this.param = param;
	}

	@Override
	public ResultSet executeQuery(String sql) throws SQLException {
		Monitor monitor = new Monitor(param);
		ResultSet i = null;
		try {
			preSQL = sql;
			i = innerPreparedStatement.executeQuery(sql);
			return i;
		} catch (SQLException e) {
			throw e;
		} finally {
			fillMonitor(monitor, JdbcMethod.EXECUTEQUERY, (i != null ? i.getRow() : 0) + "");
		}

	}

	@Override
	public int executeUpdate(String sql) throws SQLException {

		Monitor monitor = new Monitor(param);
		int i = 0;
		try {
			preSQL = sql;
			i = innerPreparedStatement.executeUpdate(sql);
		} catch (SQLException e) {
			throw e;
		} finally {
			fillMonitor(monitor, JdbcMethod.EXECUTEUPDATE, i + "");
		}
		return i;
	}

	@Override
	public void close() throws SQLException {

		innerPreparedStatement.close();
		innerPreparedStatement = null;
	}

	@Override
	public int getMaxFieldSize() throws SQLException {

		return innerPreparedStatement.getMaxFieldSize();
	}

	@Override
	public void setMaxFieldSize(int max) throws SQLException {

		innerPreparedStatement.setMaxFieldSize(max);
	}

	@Override
	public int getMaxRows() throws SQLException {

		return innerPreparedStatement.getMaxRows();
	}

	@Override
	public void setMaxRows(int max) throws SQLException {

		innerPreparedStatement.setMaxRows(max);
	}

	@Override
	public void setEscapeProcessing(boolean enable) throws SQLException {

		innerPreparedStatement.setEscapeProcessing(enable);
	}

	@Override
	public int getQueryTimeout() throws SQLException {

		return innerPreparedStatement.getQueryTimeout();
	}

	@Override
	public void setQueryTimeout(int seconds) throws SQLException {
		innerPreparedStatement.setQueryTimeout(seconds);
	}

	@Override
	public void cancel() throws SQLException {
		innerPreparedStatement.cancel();
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		return innerPreparedStatement.getWarnings();
	}

	@Override
	public void clearWarnings() throws SQLException {
		innerPreparedStatement.clearWarnings();
	}

	@Override
	public void setCursorName(String name) throws SQLException {

		innerPreparedStatement.setCursorName(name);
	}

	@Override
	public boolean execute(String sql) throws SQLException {

		Monitor monitor = new Monitor(param);
		boolean i = false;
		try {
			preSQL = sql;
			i = innerPreparedStatement.execute(sql);
		} catch (SQLException e) {
			throw e;
		} finally {
			fillMonitor(monitor, JdbcMethod.EXECUTE, i + "");
		}
		return i;
	}

	@Override
	public ResultSet getResultSet() throws SQLException {

		return innerPreparedStatement.getResultSet();
	}

	@Override
	public int getUpdateCount() throws SQLException {

		return innerPreparedStatement.getUpdateCount();
	}

	@Override
	public boolean getMoreResults() throws SQLException {

		return innerPreparedStatement.getMoreResults();
	}

	@Override
	public void setFetchDirection(int direction) throws SQLException {

		innerPreparedStatement.setFetchDirection(direction);
	}

	@Override
	public int getFetchDirection() throws SQLException {

		return innerPreparedStatement.getFetchDirection();
	}

	@Override
	public void setFetchSize(int rows) throws SQLException {

		innerPreparedStatement.setFetchSize(rows);
	}

	@Override
	public int getFetchSize() throws SQLException {

		return innerPreparedStatement.getFetchSize();
	}

	@Override
	public int getResultSetConcurrency() throws SQLException {

		return innerPreparedStatement.getResultSetConcurrency();
	}

	@Override
	public int getResultSetType() throws SQLException {

		return innerPreparedStatement.getResultSetType();
	}

	@Override
	public void addBatch(String sql) throws SQLException {

		innerPreparedStatement.addBatch(sql);
	}

	@Override
	public void clearBatch() throws SQLException {

		innerPreparedStatement.clearBatch();
	}

	@Override
	public int[] executeBatch() throws SQLException {

		return innerPreparedStatement.executeBatch();
	}

	@Override
	public Connection getConnection() throws SQLException {

		return innerConnection;
	}

	@Override
	public boolean getMoreResults(int current) throws SQLException {

		return innerPreparedStatement.getMoreResults(current);
	}

	@Override
	public ResultSet getGeneratedKeys() throws SQLException {

		return innerPreparedStatement.getGeneratedKeys();
	}

	@Override
	public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {

		Monitor monitor = new Monitor(param);
		int i = 0;
		try {
			preSQL = sql;
			i = innerPreparedStatement.executeUpdate(sql, autoGeneratedKeys);
		} catch (SQLException e) {
			throw e;
		} finally {
			fillMonitor(monitor, JdbcMethod.EXECUTEUPDATE, i + "");
		}
		return i;
	}

	@Override
	public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {

		Monitor monitor = new Monitor(param);
		int i = 0;
		try {
			preSQL = sql;
			i = innerPreparedStatement.executeUpdate(sql, columnIndexes);
		} catch (SQLException e) {
			throw e;
		} finally {
			fillMonitor(monitor, JdbcMethod.EXECUTEUPDATE, i + "");
		}
		return i;
	}

	@Override
	public int executeUpdate(String sql, String[] columnNames) throws SQLException {

		Monitor monitor = new Monitor(param);
		int i = 0;
		try {
			preSQL = sql;
			i = innerPreparedStatement.executeUpdate(sql, columnNames);
		} catch (SQLException e) {
			throw e;
		} finally {
			fillMonitor(monitor, JdbcMethod.EXECUTEUPDATE, i + "");
		}
		return i;
	}

	public void fillMonitor(Monitor monitor, String method, String i) {
		monitor.setAppId(param.getAppId());
		monitor.setConnName(innerConnection.toString());
		monitor.setJdbcMethod(method);
		monitor.setParameters(parameters);
		monitor.setSql(preSQL);
		monitor.setRow("" + (i));
		SQLContext.monitor(monitor);
	}

	@Override
	public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {

		Monitor monitor = new Monitor(param);
		boolean i = false;
		try {
			preSQL = sql;
			i = innerPreparedStatement.execute(sql, autoGeneratedKeys);
		} catch (SQLException e) {
			throw e;
		} finally {
			fillMonitor(monitor, JdbcMethod.EXECUTE, i + "");
		}
		return i;
	}

	@Override
	public boolean execute(String sql, int[] columnIndexes) throws SQLException {

		Monitor monitor = new Monitor(param);
		boolean i = false;
		try {
			preSQL = sql;
			i = innerPreparedStatement.execute(sql, columnIndexes);
		} catch (SQLException e) {
			throw e;
		} finally {
			fillMonitor(monitor, JdbcMethod.EXECUTE, i + "");
		}
		return i;
	}

	@Override
	public boolean execute(String sql, String[] columnNames) throws SQLException {

		Monitor monitor = new Monitor(param);
		boolean i = false;
		try {
			preSQL = sql;
			i = innerPreparedStatement.execute(sql, columnNames);
		} catch (SQLException e) {
			throw e;
		} finally {
			fillMonitor(monitor, JdbcMethod.EXECUTE, i + "");
		}
		return i;
	}

	@Override
	public int getResultSetHoldability() throws SQLException {

		return innerPreparedStatement.getResultSetHoldability();
	}

	@Override
	public boolean isClosed() throws SQLException {

		return innerPreparedStatement.isClosed();
	}

	@Override
	public void setPoolable(boolean poolable) throws SQLException {

		innerPreparedStatement.setPoolable(poolable);
	}

	@Override
	public boolean isPoolable() throws SQLException {

		return innerPreparedStatement.isPoolable();
	}

	public void closeOnCompletion() throws SQLException {
		innerPreparedStatement.closeOnCompletion();

	}

	public boolean isCloseOnCompletion() throws SQLException {
		return innerPreparedStatement.isCloseOnCompletion();
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return innerPreparedStatement.unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {

		return innerPreparedStatement.isWrapperFor(iface);
	}

	@Override
	public ResultSet executeQuery() throws SQLException {
		Monitor monitor = new Monitor(param);
		ResultSet i = null;
		try {
			i = innerPreparedStatement.executeQuery();
		} catch (SQLException e) {
			throw e;
		} finally {
			fillMonitor(monitor, JdbcMethod.EXECUTEQUERY, (i != null ? i.getRow() : 0) + "");
		}
		return i;
	}

	@Override
	public int executeUpdate() throws SQLException {

		Monitor monitor = new Monitor(param);
		int i = 0;
		try {
			i = innerPreparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw e;
		} finally {
			fillMonitor(monitor, JdbcMethod.EXECUTEUPDATE, (i) + "");
		}
		return i;
	}

	@Override
	public void setNull(int parameterIndex, int sqlType) throws SQLException {

		innerPreparedStatement.setNull(parameterIndex, sqlType);
	}

	@Override
	public void setBoolean(int parameterIndex, boolean x) throws SQLException {

		setParamArray(parameterIndex, x);
		innerPreparedStatement.setBoolean(parameterIndex, x);
	}

	@Override
	public void setByte(int parameterIndex, byte x) throws SQLException {

		setParamArray(parameterIndex, x);
		innerPreparedStatement.setByte(parameterIndex, x);
	}

	@Override
	public void setShort(int parameterIndex, short x) throws SQLException {

		setParamArray(parameterIndex, x);
		innerPreparedStatement.setShort(parameterIndex, x);
	}

	@Override
	public void setInt(int parameterIndex, int x) throws SQLException {

		setParamArray(parameterIndex, x);
		innerPreparedStatement.setInt(parameterIndex, x);
	}

	@Override
	public void setLong(int parameterIndex, long x) throws SQLException {

		setParamArray(parameterIndex, x);
		innerPreparedStatement.setLong(parameterIndex, x);
	}

	@Override
	public void setFloat(int parameterIndex, float x) throws SQLException {

		setParamArray(parameterIndex, x);
		innerPreparedStatement.setFloat(parameterIndex, x);
	}

	@Override
	public void setDouble(int parameterIndex, double x) throws SQLException {

		setParamArray(parameterIndex, x);
		innerPreparedStatement.setDouble(parameterIndex, x);
	}

	@Override
	public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {

		setParamArray(parameterIndex, x);
		innerPreparedStatement.setBigDecimal(parameterIndex, x);
	}

	@Override
	public void setString(int parameterIndex, String x) throws SQLException {

		setParamArray(parameterIndex, x);
		innerPreparedStatement.setString(parameterIndex, x);
	}

	@Override
	public void setBytes(int parameterIndex, byte[] x) throws SQLException {

		setParamArray(parameterIndex, x);
		innerPreparedStatement.setBytes(parameterIndex, x);
	}

	@Override
	public void setDate(int parameterIndex, Date x) throws SQLException {

		setParamArray(parameterIndex, x);
		innerPreparedStatement.setDate(parameterIndex, x);
	}

	@Override
	public void setTime(int parameterIndex, Time x) throws SQLException {

		setParamArray(parameterIndex, x);
		innerPreparedStatement.setTime(parameterIndex, x);
	}

	@Override
	public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {

		setParamArray(parameterIndex, x);
		innerPreparedStatement.setTimestamp(parameterIndex, x);
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {

		setParamArray(parameterIndex, x);
		innerPreparedStatement.setAsciiStream(parameterIndex, x, length);
	}

	@Override
	@Deprecated
	public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {

		setParamArray(parameterIndex, x);
		innerPreparedStatement.setUnicodeStream(parameterIndex, x, length);
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
		innerPreparedStatement.setBinaryStream(parameterIndex, x, length);
	}

	@Override
	public void clearParameters() throws SQLException {
		parameters.clear();
		innerPreparedStatement.clearParameters();
	}

	@Override
	public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
		innerPreparedStatement.setObject(parameterIndex, x, targetSqlType);
	}

	@Override
	public void setObject(int parameterIndex, Object x) throws SQLException {

		setParamArray(parameterIndex, x);
		innerPreparedStatement.setObject(parameterIndex, x);
	}

	@Override
	public boolean execute() throws SQLException {

		Monitor monitor = new Monitor(param);
		boolean i = false;
		try {
			i = innerPreparedStatement.execute();
		} catch (SQLException e) {
			throw e;
		} finally {
			fillMonitor(monitor, JdbcMethod.EXECUTE, (i) + "");
		}
		return i;
	}

	@Override
	public void addBatch() throws SQLException {
		innerPreparedStatement.addBatch();
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
		innerPreparedStatement.setCharacterStream(parameterIndex, reader, length);
	}

	@Override
	public void setRef(int parameterIndex, Ref x) throws SQLException {
		innerPreparedStatement.setRef(parameterIndex, x);
	}

	@Override
	public void setBlob(int parameterIndex, Blob x) throws SQLException {
		setParamArray(parameterIndex, "Blob");
		innerPreparedStatement.setBlob(parameterIndex, x);
	}

	@Override
	public void setClob(int parameterIndex, Clob x) throws SQLException {
		setParamArray(parameterIndex, "Clob");
		innerPreparedStatement.setClob(parameterIndex, x);
	}

	@Override
	public void setArray(int parameterIndex, Array x) throws SQLException {
		setParamArray(parameterIndex, "Array");
		innerPreparedStatement.setArray(parameterIndex, x);
	}

	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		return innerPreparedStatement.getMetaData();
	}

	@Override
	public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
		innerPreparedStatement.setDate(parameterIndex, x, cal);
	}

	@Override
	public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
		innerPreparedStatement.setTime(parameterIndex, x, cal);
	}

	@Override
	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
		innerPreparedStatement.setTimestamp(parameterIndex, x, cal);
	}

	@Override
	public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
		innerPreparedStatement.setNull(parameterIndex, sqlType, typeName);
	}

	@Override
	public void setURL(int parameterIndex, URL x) throws SQLException {
		innerPreparedStatement.setURL(parameterIndex, x);
	}

	@Override
	public ParameterMetaData getParameterMetaData() throws SQLException {
		return innerPreparedStatement.getParameterMetaData();
	}

	@Override
	public void setRowId(int parameterIndex, RowId x) throws SQLException {
		innerPreparedStatement.setRowId(parameterIndex, x);
	}

	@Override
	public void setNString(int parameterIndex, String value) throws SQLException {
		innerPreparedStatement.setNString(parameterIndex, value);
	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
		innerPreparedStatement.setNCharacterStream(parameterIndex, value, length);
	}

	@Override
	public void setNClob(int parameterIndex, NClob value) throws SQLException {
		innerPreparedStatement.setNClob(parameterIndex, value);
	}

	@Override
	public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
		innerPreparedStatement.setClob(parameterIndex, reader, length);
	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
		innerPreparedStatement.setBlob(parameterIndex, inputStream, length);
	}

	@Override
	public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
		innerPreparedStatement.setNClob(parameterIndex, reader, length);
	}

	@Override
	public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
		innerPreparedStatement.setSQLXML(parameterIndex, xmlObject);
	}

	@Override
	public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
		innerPreparedStatement.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
		innerPreparedStatement.setAsciiStream(parameterIndex, x, length);
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
		innerPreparedStatement.setBinaryStream(parameterIndex, x, length);
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
		innerPreparedStatement.setCharacterStream(parameterIndex, reader, length);
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
		innerPreparedStatement.setAsciiStream(parameterIndex, x);
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
		innerPreparedStatement.setBinaryStream(parameterIndex, x);
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
		innerPreparedStatement.setCharacterStream(parameterIndex, reader);
	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
		innerPreparedStatement.setNCharacterStream(parameterIndex, value);
	}

	@Override
	public void setClob(int parameterIndex, Reader reader) throws SQLException {
		innerPreparedStatement.setClob(parameterIndex, reader);
	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
		innerPreparedStatement.setBlob(parameterIndex, inputStream);
	}

	@Override
	public void setNClob(int parameterIndex, Reader reader) throws SQLException {
		innerPreparedStatement.setNClob(parameterIndex, reader);
	}

	public void setInnerConnection(Connection innerConnection) {
		this.innerConnection = innerConnection;
	}

	public Connection getInnerConnection() {
		return innerConnection;
	}

	public Params getParam() {
		return param;
	}

	public void setParam(Params param) {
		this.param = param;
	}

}
