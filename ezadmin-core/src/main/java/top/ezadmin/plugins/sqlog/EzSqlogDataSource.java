package top.ezadmin.plugins.sqlog;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import top.ezadmin.common.EzAdminRuntimeException;
import top.ezadmin.common.enums.ExceptionCode;
import top.ezadmin.plugins.sqlog.po.Params;
import top.ezadmin.plugins.sqlog.proxy.ProxyConnection;


/**
 * datasource proxy
 * 

 */
public class EzSqlogDataSource implements  DataSource {

	/**
	 * 被代理的连接池
	 */
	private DataSource realDataSource;
	/**
	 * 应用ID
	 */
	private int appId = 1;
	/**
	 * 1 是否记录日志  2  是否打印删除  3 是否拒绝删除 4 打印超过10秒的
	 * 1000 仅打印日志
	 * 0001-10000 打印超过10秒的sql
	 * 0100 打印没有where的 delete 或者update 语句
	 * 0010 如果遇到没有where的delete 或者update语句，抛出异常
	 */
	private String logType = "0000-10000";// 99 拦截delete

	/**
	 * 获取到sql之后的处理操作
	 */
	private EzSqlogCallBack callback;

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	private String driver;

	public int appId() {
		return appId;
	}
	Params parm = new Params();
	public EzSqlogDataSource() {
		parm.setAppId(appId);
		parm.setLogType(logType);
		parm.setCallback(callback);
	}

	/**
	 * check if use sqlog
	 */
	@Override
	public Connection getConnection() throws SQLException {
		if(getRealDataSource()==null){
			throw new EzAdminRuntimeException(ExceptionCode.DATASOURCE_IS_NULL,null);
		}
		return new ProxyConnection(getRealDataSource().getConnection(), parm);
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return new ProxyConnection(realDataSource.getConnection(username,password),parm);
	}

	public void init() {
		if (realDataSource == null) {
			throw new IllegalArgumentException(" realDataSource is null ");
		}
	}

	public DataSource getRealDataSource() {
		return realDataSource;
	}

	public void setRealDataSource(DataSource realDataSource) {
		this.realDataSource = realDataSource;
	}

	public void setAppId(int appId) {
		parm.setAppId(appId);
		this.appId = appId;
	}

	public String getLogType() {
		return logType;
	}

	public void setLogType(String logType) {
		parm.setLogType(logType);
		this.logType = logType;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return realDataSource.unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return realDataSource.isWrapperFor(iface);
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return realDataSource.getLogWriter();
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		realDataSource.setLogWriter(out);
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		realDataSource.setLoginTimeout(seconds);
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return realDataSource.getLoginTimeout();
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return realDataSource.getParentLogger();
	}

	public EzSqlogCallBack getCallback() {
		return callback;
	}

	public void setCallback(EzSqlogCallBack callback) {
		parm.setCallback(callback);
		this.callback = callback;
	}
}
