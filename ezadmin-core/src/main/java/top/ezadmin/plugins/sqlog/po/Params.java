package top.ezadmin.plugins.sqlog.po;

import top.ezadmin.plugins.sqlog.EzSqlogCallBack;

public class Params {
	private Integer appId;
	private String logType;//0000
	//
	private EzSqlogCallBack callback;

	//1 是否记录日志  2  是否打印删除  3 是否拒绝删除 4 打印超过10秒的
	//public static String DELETE_PREVENT="99";

	public boolean deletePrevent(){
		return is(logType,2);//DELETE_PREVENT.equals(logType)||deleteLog();
	}

	public static String DELETE_LOG="98";

	public boolean deleteLog(){
		return is(logType,1);
	}
	public boolean log(){
		return is(logType,0);
	}
	public boolean logOverTime(){
		return is(logType,3);
	}

	public Integer getAppId() {
		return appId;
	}

	public void setAppId(Integer appId) {
		this.appId = appId;
	}

	public String getLogType() {
		return logType;
	}

	public void setLogType(String logType) {
		this.logType = logType;
	}

	boolean is(String c,int i){

		return c!=null&&c.length()>i&&c.charAt(i)=='1';
	}

	public EzSqlogCallBack getCallback() {
		return callback;
	}

	public void setCallback(EzSqlogCallBack callback) {
		this.callback = callback;
	}
}
