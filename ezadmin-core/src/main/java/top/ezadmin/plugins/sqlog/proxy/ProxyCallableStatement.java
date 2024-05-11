/*
 * Copyright 2015 Focus Technology, Co., Ltd. All rights reserved.
 */
package top.ezadmin.plugins.sqlog.proxy;

import top.ezadmin.plugins.sqlog.po.Monitor;
import top.ezadmin.plugins.sqlog.po.Params;
import top.ezadmin.plugins.sqlog.po.JdbcMethod;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
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


/**
 * ProxyCallableStatement.java
 *
 * @author ezadmin
 */
public class ProxyCallableStatement implements CallableStatement {

	CallableStatement innerCallableStatement;
	private Connection innerConnection;
	private String sql;
	private Params param;

	public ProxyCallableStatement(Params param, CallableStatement prepareStatement, Connection innerConnection,
			String sql) {
		innerCallableStatement = prepareStatement;
		this.innerConnection = innerConnection;
		this.sql = sql;
		this.param = param;
	}

	@Override
	public ResultSet executeQuery() throws SQLException {

		return innerCallableStatement.executeQuery();
	}

	@Override
	public int executeUpdate() throws SQLException {

		return innerCallableStatement.executeUpdate();
	}

	@Override
	public void setNull(int parameterIndex, int sqlType) throws SQLException {

		innerCallableStatement.setNull(parameterIndex, sqlType);
	}

	@Override
	public void setBoolean(int parameterIndex, boolean x) throws SQLException {

		innerCallableStatement.setBoolean(parameterIndex, x);
	}

	@Override
	public void setByte(int parameterIndex, byte x) throws SQLException {

		innerCallableStatement.setByte(parameterIndex, x);
	}

	@Override
	public void setShort(int parameterIndex, short x) throws SQLException {

		innerCallableStatement.setShort(parameterIndex, x);
	}

	@Override
	public void setInt(int parameterIndex, int x) throws SQLException {

		innerCallableStatement.setInt(parameterIndex, x);
	}

	@Override
	public void setLong(int parameterIndex, long x) throws SQLException {

		innerCallableStatement.setLong(parameterIndex, x);
	}

	@Override
	public void setFloat(int parameterIndex, float x) throws SQLException {

		innerCallableStatement.setFloat(parameterIndex, x);
	}

	@Override
	public void setDouble(int parameterIndex, double x) throws SQLException {

		innerCallableStatement.setDouble(parameterIndex, x);
	}

	@Override
	public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {

		innerCallableStatement.setBigDecimal(parameterIndex, x);
	}

	@Override
	public void setString(int parameterIndex, String x) throws SQLException {

		innerCallableStatement.setString(parameterIndex, x);
	}

	@Override
	public void setBytes(int parameterIndex, byte[] x) throws SQLException {

		innerCallableStatement.setBytes(parameterIndex, x);
	}

	@Override
	public void setDate(int parameterIndex, Date x) throws SQLException {

		innerCallableStatement.setDate(parameterIndex, x);
	}

	@Override
	public void setTime(int parameterIndex, Time x) throws SQLException {

		innerCallableStatement.setTime(parameterIndex, x);
	}

	@Override
	public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {

		innerCallableStatement.setTimestamp(parameterIndex, x);
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {

		innerCallableStatement.setAsciiStream(parameterIndex, x);
	}

	@Override
	@Deprecated
	public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {

		innerCallableStatement.setUnicodeStream(parameterIndex, x, length);
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {

		innerCallableStatement.setBinaryStream(parameterIndex, x);
	}

	@Override
	public void clearParameters() throws SQLException {

		innerCallableStatement.clearParameters();
	}

	@Override
	public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {

		innerCallableStatement.setObject(parameterIndex, x);
	}

	@Override
	public void setObject(int parameterIndex, Object x) throws SQLException {

		innerCallableStatement.setObject(parameterIndex, x);
	}

	@Override
	public boolean execute() throws SQLException {
		boolean flag = false;
		Monitor monitor = new Monitor(param);
		try {
			flag = innerCallableStatement.execute();
		} catch (SQLException e) {
			throw e;
		} finally {
			fillMonitor(monitor, JdbcMethod.EXECUTE, sql, (flag) + "");
		}
		return flag;
	}

	@Override
	public void addBatch() throws SQLException {

		innerCallableStatement.addBatch();
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {

		innerCallableStatement.setCharacterStream(parameterIndex, reader);
	}

	@Override
	public void setRef(int parameterIndex, Ref x) throws SQLException {

		innerCallableStatement.setRef(parameterIndex, x);
	}

	@Override
	public void setBlob(int parameterIndex, Blob x) throws SQLException {

		innerCallableStatement.setBlob(parameterIndex, x);
	}

	@Override
	public void setClob(int parameterIndex, Clob x) throws SQLException {

		innerCallableStatement.setClob(parameterIndex, x);
	}

	@Override
	public void setArray(int parameterIndex, Array x) throws SQLException {

		innerCallableStatement.setArray(parameterIndex, x);
	}

	@Override
	public ResultSetMetaData getMetaData() throws SQLException {

		return innerCallableStatement.getMetaData();
	}

	@Override
	public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {

		innerCallableStatement.setDate(parameterIndex, x, cal);
	}

	@Override
	public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {

		innerCallableStatement.setTime(parameterIndex, x, cal);
	}

	@Override
	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {

		innerCallableStatement.setTimestamp(parameterIndex, x, cal);
	}

	@Override
	public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {

		innerCallableStatement.setNull(parameterIndex, sqlType, typeName);
	}

	@Override
	public void setURL(int parameterIndex, URL x) throws SQLException {

		innerCallableStatement.setURL(parameterIndex, x);
	}

	@Override
	public ParameterMetaData getParameterMetaData() throws SQLException {

		return innerCallableStatement.getParameterMetaData();
	}

	@Override
	public void setRowId(int parameterIndex, RowId x) throws SQLException {

		innerCallableStatement.setRowId(parameterIndex, x);
	}

	@Override
	public void setNString(int parameterIndex, String value) throws SQLException {

		innerCallableStatement.setNString(parameterIndex, value);
	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {

		innerCallableStatement.setNCharacterStream(parameterIndex, value, length);
	}

	@Override
	public void setNClob(int parameterIndex, NClob value) throws SQLException {

		innerCallableStatement.setNClob(parameterIndex, value);
	}

	@Override
	public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {

		innerCallableStatement.setClob(parameterIndex, reader, length);
	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {

		innerCallableStatement.setBlob(parameterIndex, inputStream, length);
	}

	@Override
	public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
		innerCallableStatement.setNClob(parameterIndex, reader, length);
	}

	@Override
	public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
		innerCallableStatement.setSQLXML(parameterIndex, xmlObject);
	}

	@Override
	public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
		innerCallableStatement.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
		innerCallableStatement.setAsciiStream(parameterIndex, x, length);
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
		innerCallableStatement.setBinaryStream(parameterIndex, x, length);
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
		innerCallableStatement.setCharacterStream(parameterIndex, reader, length);
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
		innerCallableStatement.setAsciiStream(parameterIndex, x);
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
		innerCallableStatement.setBinaryStream(parameterIndex, x);
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
		innerCallableStatement.setCharacterStream(parameterIndex, reader);
	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
		innerCallableStatement.setNCharacterStream(parameterIndex, value);
	}

	@Override
	public void setClob(int parameterIndex, Reader reader) throws SQLException {
		innerCallableStatement.setClob(parameterIndex, reader);
	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
		innerCallableStatement.setBlob(parameterIndex, inputStream);
	}

	@Override
	public void setNClob(int parameterIndex, Reader reader) throws SQLException {
		innerCallableStatement.setNClob(parameterIndex, reader);
	}

	@Override
	public ResultSet executeQuery(String sql) throws SQLException {
		Monitor monitor = new Monitor(param);
		ResultSet rs = null;
		try {
			rs = innerCallableStatement.executeQuery(sql);
		} catch (SQLException e) {
			throw e;
		} finally {
			fillMonitor(monitor, JdbcMethod.EXECUTEQUERY, sql, (rs != null ? rs.getRow() : 0) + "");
		}
		return rs;
	}

	@Override
	public int executeUpdate(String sql) throws SQLException {

		return innerCallableStatement.executeUpdate(sql);
	}

	@Override
	public void close() throws SQLException {

		innerCallableStatement.close();
	}

	@Override
	public int getMaxFieldSize() throws SQLException {

		return innerCallableStatement.getMaxFieldSize();
	}

	@Override
	public void setMaxFieldSize(int max) throws SQLException {

		innerCallableStatement.setMaxFieldSize(max);
	}

	@Override
	public int getMaxRows() throws SQLException {

		return innerCallableStatement.getMaxRows();
	}

	@Override
	public void setMaxRows(int max) throws SQLException {

		innerCallableStatement.setMaxRows(max);
	}

	@Override
	public void setEscapeProcessing(boolean enable) throws SQLException {

		innerCallableStatement.setEscapeProcessing(enable);
	}

	@Override
	public int getQueryTimeout() throws SQLException {

		return innerCallableStatement.getQueryTimeout();
	}

	@Override
	public void setQueryTimeout(int seconds) throws SQLException {

		innerCallableStatement.setQueryTimeout(seconds);
	}

	@Override
	public void cancel() throws SQLException {

		innerCallableStatement.cancel();
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {

		return innerCallableStatement.getWarnings();
	}

	public void fillMonitor(Monitor monitor, String method, String sql, String i) {
		monitor.setAppId(param.getAppId());
		monitor.setConnName(innerConnection.toString());
		monitor.setJdbcMethod(method);
		monitor.setRow("" + (i));
		monitor.setSql(sql);
		SQLContext.monitor(monitor);
	}

	@Override
	public void clearWarnings() throws SQLException {

		innerCallableStatement.clearWarnings();
	}

	@Override
	public void setCursorName(String name) throws SQLException {

		innerCallableStatement.setCursorName(name);
	}

	@Override
	public boolean execute(String sql) throws SQLException {
		boolean flag = false;
		Monitor monitor = new Monitor(param);
		try {
			flag = innerCallableStatement.execute(sql);
		} catch (SQLException e) {
			throw e;
		} finally {
			fillMonitor(monitor, JdbcMethod.EXECUTE, sql, flag + "");
		}
		return flag;
	}

	@Override
	public ResultSet getResultSet() throws SQLException {

		return innerCallableStatement.getResultSet();
	}

	@Override
	public int getUpdateCount() throws SQLException {

		return innerCallableStatement.getUpdateCount();
	}

	@Override
	public boolean getMoreResults() throws SQLException {

		return innerCallableStatement.getMoreResults();
	}

	@Override
	public void setFetchDirection(int direction) throws SQLException {

		innerCallableStatement.setFetchDirection(direction);
	}

	@Override
	public int getFetchDirection() throws SQLException {

		return innerCallableStatement.getFetchDirection();
	}

	@Override
	public void setFetchSize(int rows) throws SQLException {

		innerCallableStatement.setFetchSize(rows);
	}

	@Override
	public int getFetchSize() throws SQLException {

		return innerCallableStatement.getFetchSize();
	}

	@Override
	public int getResultSetConcurrency() throws SQLException {

		return innerCallableStatement.getResultSetConcurrency();
	}

	@Override
	public int getResultSetType() throws SQLException {

		return innerCallableStatement.getResultSetType();
	}

	@Override
	public void addBatch(String sql) throws SQLException {

		innerCallableStatement.addBatch(sql);
	}

	@Override
	public void clearBatch() throws SQLException {

		innerCallableStatement.clearBatch();
	}

	@Override
	public int[] executeBatch() throws SQLException {

		return innerCallableStatement.executeBatch();
	}

	@Override
	public Connection getConnection() throws SQLException {

		return innerConnection;
	}

	@Override
	public boolean getMoreResults(int current) throws SQLException {

		return innerCallableStatement.getMoreResults(current);
	}

	@Override
	public ResultSet getGeneratedKeys() throws SQLException {

		return innerCallableStatement.getGeneratedKeys();
	}

	@Override
	public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {

		return innerCallableStatement.executeUpdate(sql, autoGeneratedKeys);
	}

	@Override
	public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {

		return innerCallableStatement.executeUpdate(sql, columnIndexes);
	}

	@Override
	public int executeUpdate(String sql, String[] columnNames) throws SQLException {

		return innerCallableStatement.executeUpdate(sql, columnNames);
	}

	@Override
	public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {

		return innerCallableStatement.execute(sql, autoGeneratedKeys);
	}

	@Override
	public boolean execute(String sql, int[] columnIndexes) throws SQLException {

		return innerCallableStatement.execute(sql, columnIndexes);
	}

	@Override
	public boolean execute(String sql, String[] columnNames) throws SQLException {

		return innerCallableStatement.execute(sql, columnNames);
	}

	@Override
	public int getResultSetHoldability() throws SQLException {

		return innerCallableStatement.getResultSetHoldability();
	}

	@Override
	public boolean isClosed() throws SQLException {

		return innerCallableStatement.isClosed();
	}

	@Override
	public void setPoolable(boolean poolable) throws SQLException {
		innerCallableStatement.setPoolable(poolable);
	}

	@Override
	public boolean isPoolable() throws SQLException {

		return innerCallableStatement.isPoolable();
	}

	public void closeOnCompletion() throws SQLException {
		innerCallableStatement.closeOnCompletion();
	}

	public boolean isCloseOnCompletion() throws SQLException {
		return innerCallableStatement.isCloseOnCompletion();
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {

		return innerCallableStatement.unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {

		return innerCallableStatement.isWrapperFor(iface);
	}

	@Override
	public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
		innerCallableStatement.registerOutParameter(parameterIndex, sqlType);
	}

	@Override
	public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
		innerCallableStatement.registerOutParameter(parameterIndex, sqlType, scale);
	}

	@Override
	public boolean wasNull() throws SQLException {

		return innerCallableStatement.wasNull();
	}

	@Override
	public String getString(int parameterIndex) throws SQLException {

		return innerCallableStatement.getString(parameterIndex);
	}

	@Override
	public boolean getBoolean(int parameterIndex) throws SQLException {

		return innerCallableStatement.getBoolean(parameterIndex);
	}

	@Override
	public byte getByte(int parameterIndex) throws SQLException {

		return innerCallableStatement.getByte(parameterIndex);
	}

	@Override
	public short getShort(int parameterIndex) throws SQLException {

		return innerCallableStatement.getShort(parameterIndex);
	}

	@Override
	public int getInt(int parameterIndex) throws SQLException {

		return innerCallableStatement.getInt(parameterIndex);
	}

	@Override
	public long getLong(int parameterIndex) throws SQLException {

		return innerCallableStatement.getLong(parameterIndex);
	}

	@Override
	public float getFloat(int parameterIndex) throws SQLException {

		return innerCallableStatement.getFloat(parameterIndex);
	}

	@Override
	public double getDouble(int parameterIndex) throws SQLException {

		return innerCallableStatement.getDouble(parameterIndex);
	}

	@Override
	@Deprecated
	public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {

		return innerCallableStatement.getBigDecimal(parameterIndex, scale);
	}

	@Override
	public byte[] getBytes(int parameterIndex) throws SQLException {

		return innerCallableStatement.getBytes(parameterIndex);
	}

	@Override
	public Date getDate(int parameterIndex) throws SQLException {

		return innerCallableStatement.getDate(parameterIndex);
	}

	@Override
	public Time getTime(int parameterIndex) throws SQLException {

		return innerCallableStatement.getTime(parameterIndex);
	}

	@Override
	public Timestamp getTimestamp(int parameterIndex) throws SQLException {

		return innerCallableStatement.getTimestamp(parameterIndex);
	}

	@Override
	public Object getObject(int parameterIndex) throws SQLException {

		return innerCallableStatement.getObject(parameterIndex);
	}

	@Override
	public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {

		return innerCallableStatement.getBigDecimal(parameterIndex);
	}

	@Override
	public Object getObject(int parameterIndex, Map<String, Class<?>> map) throws SQLException {

		return innerCallableStatement.getObject(parameterIndex, map);
	}

	@Override
	public Ref getRef(int parameterIndex) throws SQLException {

		return innerCallableStatement.getRef(parameterIndex);
	}

	@Override
	public Blob getBlob(int parameterIndex) throws SQLException {

		return innerCallableStatement.getBlob(parameterIndex);
	}

	@Override
	public Clob getClob(int parameterIndex) throws SQLException {

		return innerCallableStatement.getClob(parameterIndex);
	}

	@Override
	public Array getArray(int parameterIndex) throws SQLException {

		return innerCallableStatement.getArray(parameterIndex);
	}

	@Override
	public Date getDate(int parameterIndex, Calendar cal) throws SQLException {

		return innerCallableStatement.getDate(parameterIndex, cal);
	}

	@Override
	public Time getTime(int parameterIndex, Calendar cal) throws SQLException {

		return innerCallableStatement.getTime(parameterIndex, cal);
	}

	@Override
	public Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {

		return innerCallableStatement.getTimestamp(parameterIndex, cal);
	}

	@Override
	public void registerOutParameter(int parameterIndex, int sqlType, String typeName) throws SQLException {
		innerCallableStatement.registerOutParameter(parameterIndex, sqlType, typeName);
	}

	@Override
	public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
		innerCallableStatement.registerOutParameter(parameterName, sqlType);
	}

	@Override
	public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
		innerCallableStatement.registerOutParameter(parameterName, sqlType, scale);
	}

	@Override
	public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
		innerCallableStatement.registerOutParameter(parameterName, sqlType, typeName);
	}

	@Override
	public URL getURL(int parameterIndex) throws SQLException {
		return innerCallableStatement.getURL(parameterIndex);

	}

	@Override
	public void setURL(String parameterName, URL val) throws SQLException {
		innerCallableStatement.setURL(parameterName, val);
	}

	@Override
	public void setNull(String parameterName, int sqlType) throws SQLException {
		innerCallableStatement.setNull(parameterName, sqlType);
	}

	@Override
	public void setBoolean(String parameterName, boolean x) throws SQLException {
		innerCallableStatement.setBoolean(parameterName, x);
	}

	@Override
	public void setByte(String parameterName, byte x) throws SQLException {
		innerCallableStatement.setByte(parameterName, x);
	}

	@Override
	public void setShort(String parameterName, short x) throws SQLException {
		innerCallableStatement.setShort(parameterName, x);
	}

	@Override
	public void setInt(String parameterName, int x) throws SQLException {
		innerCallableStatement.setInt(parameterName, x);
	}

	@Override
	public void setLong(String parameterName, long x) throws SQLException {
		innerCallableStatement.setLong(parameterName, x);
	}

	@Override
	public void setFloat(String parameterName, float x) throws SQLException {
		innerCallableStatement.setFloat(parameterName, x);
	}

	@Override
	public void setDouble(String parameterName, double x) throws SQLException {
		innerCallableStatement.setDouble(parameterName, x);
	}

	@Override
	public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
		innerCallableStatement.setBigDecimal(parameterName, x);
	}

	@Override
	public void setString(String parameterName, String x) throws SQLException {
		innerCallableStatement.setString(parameterName, x);
	}

	@Override
	public void setBytes(String parameterName, byte[] x) throws SQLException {
		innerCallableStatement.setBytes(parameterName, x);
	}

	@Override
	public void setDate(String parameterName, Date x) throws SQLException {
		innerCallableStatement.setDate(parameterName, x);
	}

	@Override
	public void setTime(String parameterName, Time x) throws SQLException {
		innerCallableStatement.setTime(parameterName, x);
	}

	@Override
	public void setTimestamp(String parameterName, Timestamp x) throws SQLException {
		innerCallableStatement.setTimestamp(parameterName, x);
	}

	@Override
	public void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {
		innerCallableStatement.setAsciiStream(parameterName, x, length);
	}

	@Override
	public void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException {
		innerCallableStatement.setBinaryStream(parameterName, x, length);
	}

	@Override
	public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
		innerCallableStatement.setObject(parameterName, x, targetSqlType, scale);
	}

	@Override
	public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
		innerCallableStatement.setObject(parameterName, x, targetSqlType);
	}

	@Override
	public void setObject(String parameterName, Object x) throws SQLException {
		innerCallableStatement.setObject(parameterName, x);
	}

	@Override
	public void setCharacterStream(String parameterName, Reader reader, int length) throws SQLException {
		innerCallableStatement.setCharacterStream(parameterName, reader, length);
	}

	@Override
	public void setDate(String parameterName, Date x, Calendar cal) throws SQLException {
		innerCallableStatement.setDate(parameterName, x, cal);
	}

	@Override
	public void setTime(String parameterName, Time x, Calendar cal) throws SQLException {
		innerCallableStatement.setTime(parameterName, x, cal);
	}

	@Override
	public void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException {
		innerCallableStatement.setTimestamp(parameterName, x, cal);
	}

	@Override
	public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
		innerCallableStatement.setNull(parameterName, sqlType, typeName);
	}

	@Override
	public String getString(String parameterName) throws SQLException {
		return innerCallableStatement.getString(parameterName);
	}

	@Override
	public boolean getBoolean(String parameterName) throws SQLException {
		return innerCallableStatement.getBoolean(parameterName);
	}

	@Override
	public byte getByte(String parameterName) throws SQLException {

		return innerCallableStatement.getByte(parameterName);
	}

	@Override
	public short getShort(String parameterName) throws SQLException {

		return innerCallableStatement.getShort(parameterName);
	}

	@Override
	public int getInt(String parameterName) throws SQLException {

		return innerCallableStatement.getInt(parameterName);
	}

	@Override
	public long getLong(String parameterName) throws SQLException {

		return innerCallableStatement.getLong(parameterName);
	}

	@Override
	public float getFloat(String parameterName) throws SQLException {

		return innerCallableStatement.getFloat(parameterName);
	}

	@Override
	public double getDouble(String parameterName) throws SQLException {

		return innerCallableStatement.getDouble(parameterName);
	}

	@Override
	public byte[] getBytes(String parameterName) throws SQLException {

		return innerCallableStatement.getBytes(parameterName);
	}

	@Override
	public Date getDate(String parameterName) throws SQLException {

		return innerCallableStatement.getDate(parameterName);
	}

	@Override
	public Time getTime(String parameterName) throws SQLException {

		return innerCallableStatement.getTime(parameterName);
	}

	@Override
	public Timestamp getTimestamp(String parameterName) throws SQLException {

		return innerCallableStatement.getTimestamp(parameterName);
	}

	@Override
	public Object getObject(String parameterName) throws SQLException {

		return innerCallableStatement.getObject(parameterName);
	}

	@Override
	public BigDecimal getBigDecimal(String parameterName) throws SQLException {

		return innerCallableStatement.getBigDecimal(parameterName);
	}

	@Override
	public Object getObject(String parameterName, Map<String, Class<?>> map) throws SQLException {

		return innerCallableStatement.getObject(parameterName, map);
	}

	@Override
	public Ref getRef(String parameterName) throws SQLException {

		return innerCallableStatement.getRef(parameterName);
	}

	@Override
	public Blob getBlob(String parameterName) throws SQLException {

		return innerCallableStatement.getBlob(parameterName);
	}

	@Override
	public Clob getClob(String parameterName) throws SQLException {

		return innerCallableStatement.getClob(parameterName);
	}

	@Override
	public Array getArray(String parameterName) throws SQLException {

		return innerCallableStatement.getArray(parameterName);
	}

	@Override
	public Date getDate(String parameterName, Calendar cal) throws SQLException {

		return innerCallableStatement.getDate(parameterName, cal);
	}

	@Override
	public Time getTime(String parameterName, Calendar cal) throws SQLException {

		return innerCallableStatement.getTime(parameterName, cal);
	}

	@Override
	public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {

		return innerCallableStatement.getTimestamp(parameterName, cal);
	}

	@Override
	public URL getURL(String parameterName) throws SQLException {

		return innerCallableStatement.getURL(parameterName);
	}

	@Override
	public RowId getRowId(int parameterIndex) throws SQLException {

		return innerCallableStatement.getRowId(parameterIndex);
	}

	@Override
	public RowId getRowId(String parameterName) throws SQLException {

		return innerCallableStatement.getRowId(parameterName);
	}

	@Override
	public void setRowId(String parameterName, RowId x) throws SQLException {
		innerCallableStatement.setRowId(parameterName, x);
	}

	@Override
	public void setNString(String parameterName, String value) throws SQLException {
		innerCallableStatement.setNString(parameterName, value);
	}

	@Override
	public void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException {
		innerCallableStatement.setNCharacterStream(parameterName, value, length);
	}

	@Override
	public void setNClob(String parameterName, NClob value) throws SQLException {
		innerCallableStatement.setNClob(parameterName, value);
	}

	@Override
	public void setClob(String parameterName, Reader reader, long length) throws SQLException {
		innerCallableStatement.setClob(parameterName, reader, length);
	}

	@Override
	public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {
		innerCallableStatement.setBlob(parameterName, inputStream, length);
	}

	@Override
	public void setNClob(String parameterName, Reader reader, long length) throws SQLException {
		innerCallableStatement.setNClob(parameterName, reader, length);
	}

	@Override
	public NClob getNClob(int parameterIndex) throws SQLException {

		return innerCallableStatement.getNClob(parameterIndex);
	}

	@Override
	public NClob getNClob(String parameterName) throws SQLException {

		return innerCallableStatement.getNClob(parameterName);
	}

	@Override
	public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
		innerCallableStatement.setSQLXML(parameterName, xmlObject);
	}

	@Override
	public SQLXML getSQLXML(int parameterIndex) throws SQLException {

		return innerCallableStatement.getSQLXML(parameterIndex);
	}

	@Override
	public SQLXML getSQLXML(String parameterName) throws SQLException {

		return innerCallableStatement.getSQLXML(parameterName);
	}

	@Override
	public String getNString(int parameterIndex) throws SQLException {

		return innerCallableStatement.getNString(parameterIndex);
	}

	@Override
	public String getNString(String parameterName) throws SQLException {

		return innerCallableStatement.getNString(parameterName);
	}

	@Override
	public Reader getNCharacterStream(int parameterIndex) throws SQLException {

		return innerCallableStatement.getNCharacterStream(parameterIndex);
	}

	@Override
	public Reader getNCharacterStream(String parameterName) throws SQLException {

		return innerCallableStatement.getNCharacterStream(parameterName);
	}

	@Override
	public Reader getCharacterStream(int parameterIndex) throws SQLException {

		return innerCallableStatement.getCharacterStream(parameterIndex);
	}

	@Override
	public Reader getCharacterStream(String parameterName) throws SQLException {

		return innerCallableStatement.getCharacterStream(parameterName);
	}

	@Override
	public void setBlob(String parameterName, Blob x) throws SQLException {
		innerCallableStatement.setBlob(parameterName, x);
	}

	@Override
	public void setClob(String parameterName, Clob x) throws SQLException {
		innerCallableStatement.setClob(parameterName, x);
	}

	@Override
	public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {
		innerCallableStatement.setAsciiStream(parameterName, x, length);
	}

	@Override
	public void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException {
		innerCallableStatement.setBinaryStream(parameterName, x, length);
	}

	@Override
	public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
		innerCallableStatement.setCharacterStream(parameterName, reader, length);
	}

	@Override
	public void setAsciiStream(String parameterName, InputStream x) throws SQLException {
		innerCallableStatement.setAsciiStream(parameterName, x);
	}

	@Override
	public void setBinaryStream(String parameterName, InputStream x) throws SQLException {
		innerCallableStatement.setBinaryStream(parameterName, x);
	}

	@Override
	public void setCharacterStream(String parameterName, Reader reader) throws SQLException {
		innerCallableStatement.setCharacterStream(parameterName, reader);
	}

	@Override
	public void setNCharacterStream(String parameterName, Reader value) throws SQLException {
		innerCallableStatement.setNCharacterStream(parameterName, value);
	}

	@Override
	public void setClob(String parameterName, Reader reader) throws SQLException {
		innerCallableStatement.setClob(parameterName, reader);
	}

	@Override
	public void setBlob(String parameterName, InputStream inputStream) throws SQLException {
		innerCallableStatement.setBlob(parameterName, inputStream);
	}

	@Override
	public void setNClob(String parameterName, Reader reader) throws SQLException {
		innerCallableStatement.setNClob(parameterName, reader);
	}

	public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException {

		return innerCallableStatement.getObject(parameterIndex, type);
	}

	public <T> T getObject(String parameterName, Class<T> type) throws SQLException {

		return innerCallableStatement.getObject(parameterName, type);
	}

	public Params getParam() {
		return param;
	}

	public void setParam(Params param) {
		this.param = param;
	}

}
