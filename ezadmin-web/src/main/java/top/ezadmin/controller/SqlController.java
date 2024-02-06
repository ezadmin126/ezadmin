package top.ezadmin.controller;


import com.alibaba.fastjson.JSON;
import com.ezcloud.common.utils.JsoupUtil;
import com.ezcloud.common.utils.NumberUtils;
import com.ezcloud.common.utils.StringUtils;
import com.ezcloud.plugins.sqlog.format.FormatStyle;
import com.ezcloud.web.EzResult;
import top.ezadmin.domain.model.CoreSlowLog;
import top.ezadmin.domain.model.CoreSlowLogExample;
import top.ezadmin.domain.model.CoreSql;
import top.ezadmin.domain.mapper.CoreSlowLogMapper;
import top.ezadmin.domain.mapper.ext.CoreSqlExtMapper;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.*;


import net.sf.jsqlparser.statement.update.Update;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.apache.commons.codec.digest.DigestUtils;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/sql")
public class SqlController {
	/**
	 * 日志
	 */
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	CoreSqlExtMapper coreSqlMapper;
	@Autowired
	CoreSlowLogMapper slowLogMapper;
	BlockingQueue<CoreSql> queue=new ArrayBlockingQueue<>(10000);

	@RequestMapping("/add")
	@ResponseBody
	public EzResult add(String sql,String appName,String time ) throws JSQLParserException, InterruptedException {
		Calendar c=Calendar.getInstance();
		if((c.get(Calendar.HOUR_OF_DAY)>0&&c.get(Calendar.HOUR_OF_DAY)<7)||c.get(Calendar.HOUR_OF_DAY)>=22){
			//1点定时任务数据 抛弃
			return EzResult.instance();
		}
		if(StringUtils.isBlank(sql)||sql.trim().toLowerCase().startsWith("update")||sql.trim().toLowerCase().startsWith("insert")||sql.trim().toLowerCase().startsWith("delete")){
			return EzResult.instance();
		}
		String shortSql=shortSql(sql);
		if(shortSql.length()>1500){
			shortSql=shortSql.substring(0,1489);
		}
		if(shortSql.startsWith("update:")||shortSql.startsWith("insert:")||shortSql.startsWith("delete:")){
			return EzResult.instance();
		}
		CoreSql sqlqueue=new CoreSql();
		String encode=DigestUtils.md5Hex(shortSql);
		sqlqueue.setAppname(appName);
		sqlqueue.setShortSql(shortSql);
		sqlqueue.setFullSql(sql);
		sqlqueue.setEncode(encode);
		sqlqueue.setExeTime(NumberUtils.toLong(time));
		queue.offer(sqlqueue,2, TimeUnit.SECONDS);
		return EzResult.instance();
	}
	@PostConstruct
	public void tack(){

		CompletableFuture.runAsync(new Runnable() {
			@Override
			public void run() {

				while(true){
					log.info("开始获取1000条执行SQL");
					try {
						Map<String,CoreSql> map=new HashMap<>();
						for (int i = 0; i < 1000; i++) {
							CoreSql message = queue.take();
							if(map.containsKey(message.getEncode()+message.getAppname())){
								CoreSql exist=map.get(message.getEncode()+message.getAppname());
								exist.setExeCount(NumberUtils.toLong(exist.getExeCount()+"")+1);
								exist.setExeTime((exist.getExeTime()+ message.getExeTime())/2);
								map.put(message.getEncode()+message.getAppname(),exist);
							}else{
								message.setExeCount(1L);
								map.put(message.getEncode()+message.getAppname(),message);
							}
						}
						log.info("获取到1000条执行SQL");
						map.forEach((k,v)->{
							CoreSql  dbCoreSql=coreSqlMapper.selectOneSql(v.getEncode(),v.getAppname());
							if(dbCoreSql!=null){
								dbCoreSql.setExeCount(NumberUtils.toLong(dbCoreSql.getExeCount()+"")+1);
								dbCoreSql.setUpdateTime(new Date());
								dbCoreSql.setExeTime((NumberUtils.toLong(v.getExeTime()+"")+NumberUtils.toLong(dbCoreSql.getExeTime()+""))/2);
								coreSqlMapper.updateByPrimaryKeySelective(dbCoreSql);
							}else{
								CoreSql coreSql=new CoreSql();
								coreSql.setAddTime(new Date());
								coreSql.setFullSql(v.getFullSql());
								coreSql.setShortSql(v.getShortSql());
								coreSql.setEncode(v.getEncode());
								coreSql.setExeCount(0l);
								coreSql.setExeTime(NumberUtils.toLong(v.getExeTime()+""));
								coreSql.setAppname(v.getAppname());
								coreSqlMapper.insert(coreSql);
							}
						});
						log.info("结束执行1000条执行SQL");

					} catch (Exception e) {
						log.error("",e);
					}
				}
			}
		})
		;

	}
	@RequestMapping("/slow")
	public String slow(){
		return "slowlog";
	}

	@RequestMapping("/slowUpload")
	public String slowsql(@RequestParam("file") MultipartFile file,String date, Model model) throws JSQLParserException, IOException {
		List<String> list=new ArrayList<>();
		if(file.isEmpty()){
			CoreSlowLogExample example=new CoreSlowLogExample();
			example.createCriteria().andAddDateEqualTo(date);
			List<CoreSlowLog> slowLog= slowLogMapper.selectByExampleWithBLOBs(example);
			if(!CollectionUtils.isEmpty(slowLog)){
				String blo=slowLog.get(0).getSlowlog();
				String[] lines=blo.split("\n");
				for (int i = 0; i <lines.length; i++) {
						list.add(lines[i]);
				}
			}
		}else{
			list=IOUtils.readLines(file.getInputStream());
		}
		List<String> sqls=new ArrayList<>();
		StringBuilder sqlLine=new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			if(list.get(i).indexOf("# Time:")>=0){
				sqls.add(sqlLine.toString());
				sqlLine=new StringBuilder();
			}
			sqlLine.append(list.get(i)+"\n");
		}
		Map<String,Map<String,Object>> kvMap=new HashMap<>();
		Map<String,Integer> count=new LinkedHashMap<>();

		Map<String, BigDecimal> query_time_map=new LinkedHashMap<>();
		Map<String,BigDecimal> lock_time_map=new LinkedHashMap<>();
		Map<String,BigDecimal> row_send_map=new LinkedHashMap<>();
		Map<String,BigDecimal> row_ex_map=new LinkedHashMap<>();
		Map<String,BigDecimal> row_aff_map=new LinkedHashMap<>();

		List<String> backup=new ArrayList<>();
		for (int i = 0; i < sqls.size(); i++) {
			 String x=sqls.get(i);
			 if(StringUtils.isBlank(x)){
				 continue;
			 }
			 String[] items=x.split("\n");
			 if(items==null||items.length<=0){
				continue;
			 }
			 String time="";
			 String user="";
			 String query="";
			 String scheme="";
			 StringBuilder selectRow=new StringBuilder();
			for (int j = 0; j < items.length; j++) {
				if(items[j].startsWith("# Time")){
					time=items[j];
				}else
				if(items[j].startsWith("# User")){
					user=items[j];
				}else
				if(items[j].startsWith("# Query_time")){
					query=items[j];
				}else if(items[j].startsWith("use ")){
					scheme=items[j];
				}  else if(items[j].startsWith("SET timestamp")||items[j].startsWith("# Schema")||items[j].startsWith("# Bytes_sent")){
					System.out.println(items[j]);
				}
				else{
					selectRow.append(items[j]+"\n");
				}
			}
			if(StringUtils.isBlank(selectRow.toString())){
				continue;
			}
			String sqlinner=shortDDL(selectRow.toString());
			Map<String,Object> sqlDetail=new HashMap<>();
			try{
			String ffsql= FormatStyle.BASIC.getFormatter().format(selectRow.toString());
 			sqlDetail.put("fullsql",ffsql);
			}catch (Exception e){
				sqlDetail.put(sqlinner,selectRow.toString());
			}
			if(user.indexOf("backup")>=0
			){
				backup.add(x);
				continue;
			}
			//计算总数
			if(!count.containsKey(sqlinner)){
				count.put(sqlinner,1);
			}else{
				count.put(sqlinner,count.get(sqlinner)+1);
			}
			if(StringUtils.isBlank(query)||query.indexOf("Query_time: ")<0){
				continue;
			}


			sqlDetail.put("scheme",scheme);
			sqlDetail.put("user",user);
			sqlDetail.put("exetime",time);

			kvMap.put(sqlinner,sqlDetail);

			//System.out.println(Rows_examined);
			try {
				String Query_time=query.substring(query.indexOf("Query_time: ")+"Query_time: ".length(),query.indexOf("  Lock_time: "));
				BigDecimal query_time=new BigDecimal(Query_time);
				mapmax(query_time_map, sqlinner,query_time );
			}catch (Exception e){
				e.printStackTrace();
			}


			try {
				String Lock_time=query.substring(query.indexOf("Lock_time: ")+"Lock_time: ".length(),query.indexOf(" Rows_sent"));
				BigDecimal lock_time=new BigDecimal(Lock_time.trim());
				mapmax(lock_time_map, sqlinner,lock_time );
			}catch (Exception e){
				e.printStackTrace();
			}

			try {
				String Rows_sent=query.substring(query.indexOf("Rows_sent: ")+"Rows_sent: ".length(),query.indexOf("  Rows_examined"));
				BigDecimal row_send=new BigDecimal(Rows_sent);
				mapmax(row_send_map, sqlinner,row_send );
			}catch (Exception e){
				e.printStackTrace();
			}
			try {
				Integer Rows_examined=NumberUtils.toInt(query.substring(query.indexOf("Rows_examined: ")+"Rows_examined: ".length(),query.indexOf("  Rows_affected:")));
				BigDecimal row_ex=new BigDecimal(Rows_examined);
				mapmax(row_ex_map, sqlinner,row_ex );
			}catch (Exception e){
				e.printStackTrace();
			}

			try {
				Integer Rows_affected = NumberUtils.toInt(query.substring(query.indexOf("Rows_affected: ") + "Rows_affected: ".length()));
				BigDecimal row_aff = new BigDecimal(Rows_affected);
				mapmax(row_aff_map, sqlinner, row_aff);
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		Map<String,Map<String,Object>> showlist=new LinkedHashMap<>();
		count.forEach((k,v)->{
			Map<String,Object> obj=new HashMap<>();
			obj.put("count",v);
			try {
				obj.put("queryTime", query_time_map.get(k));
				obj.put("lock_time", lock_time_map.get(k));
				obj.put("row_send", row_send_map.get(k));
				obj.put("row_ex", row_ex_map.get(k));
				obj.put("row_aff", row_aff_map.get(k));
				obj.put("user", kvMap.get(k).get("user"));
				obj.put("scheme", kvMap.get(k).get("scheme"));
				obj.put("exetime", kvMap.get(k).get("exetime"));
				showlist.put(kvMap.get(k).get("fullsql").toString(),obj);
			}catch (Exception e){}
		});
		model.addAttribute("showlist",showlist);
		return "slowlog";
	}

	static String shortDDL(String sql) throws JSQLParserException {
		sql=sql.toLowerCase().trim();
		String sqlinner="";
		if(sql.startsWith("select")){
			 return shortSql(sql);
		}else{
			if(sql.startsWith("delete")){
				if(sql.lastIndexOf("where")==-1){
					sqlinner=sql;
				}else{
					sqlinner=sql.substring(sql.indexOf("delete"), sql.lastIndexOf("where"));
				}
			}else
			if(sql.startsWith("update")){
				try {
					sqlinner = sql.substring(sql.indexOf("update"), sql.indexOf("=",sql.indexOf("update")));
				}catch (Exception e){
					System.out.println(sql);
					e.printStackTrace();;
				}
			}else{
				System.out.println(sql);
			}
		}
		return sqlinner;
	}

	static String shortSql(String sql) throws JSQLParserException {
		if(StringUtils.isBlank(sql)){
			return "";
		}
		StringBuilder result = new StringBuilder();
		try {
			Statement statement = CCJSqlParserUtil.parse(sql);
			if(statement instanceof Update){
				Update update=(Update)statement;
				result.append("update:"+update.getTable()+",");
				update.getUpdateSets().forEach(item->{
					result.append(JSON.toJSONString(item.getColumns())+",");
				});
				Expression whereExpression = update.getWhere();
				if (whereExpression != null) {
					whereExpression.accept(new ExpressionVisitorAdapter() {
						public void visit(Column column) {
							result.append(column.toString() + ",");
						}
					});
				}

			}else if(statement instanceof Delete){
				Delete delete=(Delete)statement;
				result.append("delete:"+delete.getTable()+",");
				Expression whereExpression = delete.getWhere();
				if (whereExpression != null) {
					whereExpression.accept(new ExpressionVisitorAdapter() {
						public void visit(Column column) {
							result.append(column.toString() + ",");
						}
					});
				}
			}else if(statement instanceof Insert){
				Insert insert=(Insert)statement;
				result.append("insert:"+insert.getTable()+",");
				result.append(JSON.toJSONString(insert.getColumns()));
			}
			else if(statement instanceof Select){
				Select selectStatement = (Select) statement;
				SelectBody select = selectStatement.getSelectBody();
				PlainSelect plainSelect = null;
				if (select instanceof PlainSelect) {
					plainSelect = (PlainSelect) select;
				} else if (select instanceof SetOperationList) {
					SetOperationList setOperationList = (SetOperationList) select;
					if (setOperationList.getSelects().get(0) instanceof PlainSelect) {
						plainSelect = (PlainSelect) setOperationList.getSelects().get(0);
					} else {
						return select.toString();
					}
				} else {
					return select.toString();
				}
				List<SelectItem> selectItemList = plainSelect.getSelectItems();
				selectItemList.forEach(item -> {
					if(item instanceof AllTableColumns){
						result.append("*,");
					}else
					if (item instanceof AllColumns) {
						result.append("*,");
					} else {
						SelectExpressionItem expressionItem = (SelectExpressionItem) item;
						String colName = "";
						if (expressionItem.getExpression() instanceof Column) {
							Column column = (Column) expressionItem.getExpression();
							colName = column.getColumnName();
							result.append(colName + ",");

						} else if (expressionItem.getExpression() instanceof Function) {
							Function function = (Function) expressionItem.getExpression();
							result.append(function.toString() + ",");
						}
					}
				});
				FromItem table = plainSelect.getFromItem();
				if (table instanceof SubSelect) {
					SubSelect tableSubSelect = (SubSelect) table;
					result.append(shortSql(tableSubSelect.getSelectBody().toString()));
				} else {
					result.append(plainSelect.getFromItem() == null ? "" : plainSelect.getFromItem().toString() + ",");
				}
				Expression whereExpression = plainSelect.getWhere();
				if (whereExpression != null) {
					whereExpression.accept(new ExpressionVisitorAdapter() {
						public void visit(Column column) {
							result.append(column.toString() + ",");
						}
					});
				}

			}
		}catch (Exception e){
				return "errorsql";
		}
		return result.toString();
	}


	public static void mapmax(Map<String,BigDecimal> m,String k,BigDecimal b){
		if(!m.containsKey(k)){
			m.put(k,new BigDecimal(0));
		}
		m.put(k,  b.compareTo(m.get(k))>0?b:m.get(k));
	}



}
