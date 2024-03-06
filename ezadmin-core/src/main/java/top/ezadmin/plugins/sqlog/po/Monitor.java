package top.ezadmin.plugins.sqlog.po;

import java.io.Serializable;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;


import top.ezadmin.plugins.sqlog.format.FormatStyle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * JdbcMethod.java
 * @author ezadmin
 */
public class Monitor implements Serializable {
	/**
	 * 
	 */
	private static Logger log = LoggerFactory.getLogger(Monitor.class);
	private static final long serialVersionUID = 4775388329454639981L;
	private Integer appId;
	private long start = System.currentTimeMillis();
	private long end = 0;
	private Map<Integer, Object> parameters = new HashMap<Integer, Object>();
	private String sql;
	private String row;
	private String connName;
	private String jdbcMethod;
	private String ip;
	private Long startParseTime;

	private Long totalParseTime;

	private String sqlType = "0";
	private Params params;

	private static String ID = "id:", TIME = "\t", CONN = "ms\tconn:", METHOD = "\tmethod:",
			SQL = "\tsql:\n";
	private Monitor( ) {}
	public Monitor(Params params) {
		this.params=params;
	}
	public Params getParams(){
		return params==null?new Params():params;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		try {
			sb.append(ID);
			sb.append(appId);
			sb.append(TIME);
			sb.append(System.currentTimeMillis() - start);
			sb.append(CONN);
			sb.append(connName);
			sb.append(METHOD);
			sb.append(jdbcMethod);
			sb.append(SQL);
			// 放到后台格式化
			// sb.append(fullSQL());
			sb.append(FormatStyle.BASIC.getFormatter().format(fullSQL()));
		} catch (Exception e) {
			log.warn("sql格式化 {}",fullSQL(), e);
			// ignor
			sb.append(fullSQL()+e.getMessage());
		}
		return sb.toString();
	}
	public String sqlTime() {
		StringBuilder sb = new StringBuilder();
		try {
			sb.append(TIME);
			sb.append(System.currentTimeMillis() - start);
			sb.append(SQL);
			sb.append(FormatStyle.BASIC.getFormatter().format(fullSQL()));
		} catch (Exception e) {
			log.warn("sql格式化 {}",sql, e);
			// ignor
			sb.append(sql+e.getMessage());
		}
		return sb.toString();
	}

	public String onlyShow() {
		return fullSQL();
	}

	public String productionShow() {

		StringBuilder sb = new StringBuilder();
		try {
			sb.append(ID);
			sb.append(appId);
			sb.append(TIME);
			sb.append(System.currentTimeMillis() - start);
			sb.append(CONN);
			sb.append(connName);
			sb.append(METHOD);
			sb.append(jdbcMethod);
			sb.append(SQL);
			// 放到后台格式化
			sb.append(fullSQL());
		} catch (Exception e) {
			log.warn("sql格式化 {}",sql, e);
			// ignor
			sb.append(sql+e.getMessage());
		}
		// sb.append(FormatStyle.BASIC.getFormatter().format(fullSQL()));
		return sb.toString();
	}

	public String fullSQL() {
		if (parameters.isEmpty()) {
			return sql;
		} else {
			int paramIndex = 1;
			StringBuilder sb = new StringBuilder();
			for (int pos = 0; pos < sql.length(); pos++) {
				char c = sql.charAt(pos);
				if (c == '?') {
					replaceValue(sb, paramIndex++, c);
				} else {
					sb.append(c);
				}
			}
			return sb.toString();
		}
	}

	public static final String DATE_START = "to_date('";
	public static final String DATE_END = "','yyyy-MM-dd hh24:mi:ss')";

	private void replaceValue(StringBuilder sb, int pos, char c) {
		Object obj = parameters.get(pos);
		if (obj instanceof String) {
			sb.append("'");
			sb.append(obj.toString());
			sb.append("'");
		} else if (obj instanceof Date || obj instanceof java.sql.Timestamp) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			sb.append(DATE_START);
			sb.append(sdf.format(obj));
			sb.append(DATE_END);
		} else {
			if (obj != null) {
				sb.append(obj.toString());
			} else {
				sb.append(" null ");
			}

		}
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getUpperTrimSql() {
		return getSql().toUpperCase().trim();
	}

	public void setRow(String row) {
		this.row = row;
	}

	public Integer getRow() {
		try {
			return Integer.parseInt(row);
		}catch (NumberFormatException e){
			return 0;
		}
	}

	public String getConnName() {
		return connName;
	}

	public void setConnName(String connName) {
		this.connName = connName;
	}

	public Integer getAppId() {
		return appId;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}

	public String getJdbcMethod() {
		return jdbcMethod;
	}

	public void setJdbcMethod(String jdbcMethod) {
		this.jdbcMethod = jdbcMethod;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Long getStartParseTime() {
		return startParseTime;
	}

	public void setStartParseTime(Long startParseTime) {
		this.startParseTime = startParseTime;
	}

	public Long getTotalParseTime() {
		return totalParseTime;
	}

	public void setTotalParseTime(Long totalParseTime) {
		this.totalParseTime = totalParseTime;
	}

	public String getSqlType() {
		return sqlType;
	}

	public Map<Integer, Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<Integer, Object> parameters) {
		this.parameters = parameters;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}
}
