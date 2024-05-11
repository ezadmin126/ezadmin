/*
 * Copyright 2017 Focus Technology, Co., Ltd. All rights reserved.
 */
package top.ezadmin.plugins.sqlog.proxy;

import top.ezadmin.common.utils.Utils;
import top.ezadmin.plugins.sqlog.po.Monitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SQLContext.java
 *
 * @author ezadmin
 */
public class SQLContext {
	private static Logger log = LoggerFactory.getLogger(SQLContext.class);



	protected static void monitor(Monitor monitor) {
		try {
			monitor.setEnd(System.currentTimeMillis());
			if(Utils.getLog()!=null){
				Utils.addLog("ezsql\tinfo：： "+ monitor.sqlTime());
			}
			if (monitor.getParams().log()) {
				log.info("{} {}",Utils.getRequestUrl(),monitor.toString());
			}
			if (monitor.getParams().logOverTime() ) {
				int overTime = Integer.parseInt(monitor.getParams().getLogType().split("-")[1]);
				if(monitor.getEnd() - monitor.getStart()>=overTime){
					log.warn("ezsql\ttimeout:{}@@{}",Utils.getRequestUrl(),monitor.toString());
				}
			}
			if(monitor.getParams().deletePrevent()||monitor.getParams().deleteLog()){
				char method=getMethod(monitor.getSql());
				if((monitor.getParams().deletePrevent()&&'u'==method||'d'==method||'t'==method)&&monitor.getSql().toLowerCase().indexOf("where")<0){
					log.error("ezsql-error:no where condition::{} {}",Utils.getRequestUrl(),monitor.toString());
					throw new IllegalArgumentException ("ezsql-error:no where condition::"+monitor.toString());
				}
				if(monitor.getParams().deleteLog()&&('u'==method||'d'==method||'t'==method)&&monitor.getSql().toLowerCase().indexOf("where")<0){
					log.error("ezsql-error:no where condition::{} {}",Utils.getRequestUrl(),monitor.toString());
				}
			}
			if(monitor.getParams().getCallback()!=null){
				monitor.getParams().getCallback().callback(monitor);
				return;
			}

		}catch (IllegalArgumentException e2){
			throw e2;
		}
		catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error(monitor.productionShow(), e);
			}
		}
	}

	public static char getMethod(String sql) {
		for (int i = 0; i < sql.length(); i++) {
			char c = sql.charAt(i);
			if (Character.isLetter(c)) {
				return Character.toLowerCase(c);
			}
		}
		return 's';
	}

}
