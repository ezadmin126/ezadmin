package com.example.functions;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Resource;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import top.ezadmin.EzBootstrap;
import top.ezadmin.common.utils.EzProxy;
import top.ezadmin.common.utils.JSONUtils;
import top.ezadmin.common.utils.StringUtils;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.dao.Dao;
import top.ezadmin.dao.ListDao;
import top.ezadmin.plugins.EzSqlParserImpl;
import top.ezadmin.plugins.express.InsertSimpleOperator;
import top.ezadmin.plugins.express.OperatorParam;
import top.ezadmin.plugins.express.UpdateSimpleOperator;
import top.ezadmin.plugins.express.jdbc.InsertParam;
import top.ezadmin.plugins.express.jdbc.UpdateParam;
import top.ezadmin.plugins.sqlog.proxy.SQLContext;
import top.ezadmin.service.ListService;
import top.ezadmin.web.EzResult;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EzadminFunctions {

    @Resource
    DataSource dataSource;

    private static final Logger log = LoggerFactory.getLogger(EzadminFunctions.class);

    @Tool(name = "createHtml",
            description = "通过传入的sql(格式：select id id,name 用户名 from users)与全局唯一列表编码listCode, 生成一个ezadmin列表配置文件 " +
            "参数：sql (生成的sql)  listCode :列表唯一编码",
            resultConverter = MyToolCallResultConverter.class)
    public String createHtml(@JsonProperty("sql") String sql, @JsonProperty("listCode") String listCode) throws Exception {
        log.info("=== 调用工具: createHtml ===");
        log.info("参数 - SQL: {}", sql);
        log.info("参数 - 列表编码: {}", listCode);

        String html = "";
        try {
            html = new EzSqlParserImpl().sqlToList(sql,listCode ,"dataSource");
            log.info("SQL解析成功，生成HTML配置");

            long count = Dao.getInstance().executeCountQuery(dataSource, "select count(1) from  T_EZADMIN_EDIT where EZ_CODE=? ", new Object[]{listCode});
            if (count > 0) {
                log.warn("列表编码已存在: {}", listCode);
                return "列表编码已存在";
            }
            Dao.getInstance().executeUpdate(dataSource, "insert into  T_EZADMIN_EDIT (ez_code,ez_name,dataSource,ez_type,ez_config,is_del)values(?,?,?,?,?,?) ",
                    new Object[]{listCode, listCode, "dataSource", 1, html, 0});
            log.info("列表配置保存成功");
        } catch (Exception e) {
            log.error("初始化ezadmin列表失败", e);
            return "初始化ezadmin列表失败:" + ExceptionUtils.getStackTrace(e);
        }
        log.info("=== 工具调用结束: createHtml ===");
        return "通过sql生成ezadmin列表成功，列表编码为"+listCode;
    }

    @Tool(name = "getHtml", description = " 全局唯一列表编码listCode, 获取ezadmin列表配置文件 内容 ，参数：   listCode :列表唯一编码",resultConverter = MyToolCallResultConverter.class)
    public EzResult getHtml(@JsonProperty("listCode") String listCode) throws Exception {
        log.info("=== 调用工具: getHtml ===");
        log.info("参数 - 列表编码: {}", listCode);

        try {
            long count = Dao.getInstance().executeCountQuery(dataSource, "select count(1) from  T_EZADMIN_EDIT where EZ_CODE=? ", new Object[]{listCode});
            if (count <= 0) {
                log.warn("列表编码不存在: {}", listCode);
                return EzResult.instance().fail("列表编码不存在"+listCode);
            }
            Map<String, Object> config = Dao.getInstance().executeQueryOne(dataSource, " select  EZ_CONFIG from T_EZADMIN_EDIT where ez_code=? and EZ_TYPE=1 and IS_DEL=0 ",
                    new Object[]{listCode});

            if(config==null||StringUtils.isBlank(Utils.trimNull(config.get("EZ_CONFIG")))){
                return EzResult.instance().data("html","")
                        .message("当前"+listCode+"没有配置，请调用createHtml生成新的配置");
            }
            String result = Utils.trimNull(config.get("EZ_CONFIG"));
            log.info("成功获取列表配置，长度: {}", result.length());
            log.info("=== 工具调用结束: getHtml ===");
           return EzResult.instance().data("html",result).message("成功获取列表配置html");
        } catch (Exception e) {
            log.error("获取ezadmin列表失败", e);
            return EzResult.instance().fail("获取ezadmin列表失败:" + ExceptionUtils.getStackTrace(e));
        }
    }

    @Tool(name = "save", description = "全局唯一列表编码listCode,保存ezadmin列表配置文件 参数：   listCode :列表唯一编码 html: 最新的配置html",resultConverter = MyToolCallResultConverter.class)
    public EzResult save(@JsonProperty("listCode") String listCode, @JsonProperty("html") String html) throws Exception {
        log.info("=== 调用工具: save ===");
        log.info("参数 - 列表编码: {} {}", listCode,html);
        log.info("参数 - HTML配置长度: {}", html != null ? html.length() : 0);

        try {

            Map<String, Object> list = ListDao.getInstance().selectAllListByHtml(html);
            String htmlFormat = ListDao.getInstance().transEntityToHtmlConfig(list);
            Dao.getInstance().executeUpdate(dataSource, "UPDATE T_EZADMIN_EDIT SET EZ_CONFIG=? WHERE ez_code=? and EZ_TYPE=1 and IS_DEL=0 ",
                    new Object[]{htmlFormat, listCode});
            publish(listCode);
            log.info("列表配置保存成功");
            log.info("=== 工具调用结束: save ===");
            return EzResult.instance().message("更新ezadmin列表成功");
        } catch (Exception e) {
            log.error("保存ezadmin列表失败", e);
            return EzResult.instance().message("获取ezadmin列表失败"+e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println(new EzadminFunctions().generateListCode());
    }

    // 新增：根据用户需求智能生成列表编码
    @Tool(name = "generateListCode", description = "根据列表名称生成全局唯一的列表编码 ",resultConverter = MyToolCallResultConverter.class)
    public EzResult generateListCode() throws Exception {
        return  EzResult.instance().data("列表唯一编码",System.currentTimeMillis() + "");
    }

    // 新增：获取常用数据类型映射
    @Tool(name = "getDataTypeMapping", description = "获取数据库字段类型到ezadmin组件类型的映射建议",resultConverter = MyToolCallResultConverter.class)
    public String getDataTypeMapping() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("VARCHAR", "input-text");
        map.put("TEXT", "input-text");
        map.put("INT", "input-text");
        map.put("BIGINT", "input-text");
        map.put("DECIMAL", "input-text");
        map.put("DATE", "date");
        map.put("DATETIME", "datetime");
        map.put("TINYINT", "radio");
        return JSON.toJSONString(map);
    }

    @Tool(name = "mysql-mcp-server", description = "执行传入的sql,并返回数据，参数 sql (需要执行的sql语句)",resultConverter = MyToolCallResultConverter.class)
    public EzResult executeSql(SqlRequest req) throws Exception {
        // 从请求上下文获取 Authorization header
        String sql = req.sql();
        log.info("mysql-mcp-server::executeSql:", req.sql());
        char method = SQLContext.getMethod(sql);
        if ('i' == method || 'u' == method || 'd' == method || 't' == method) {
            log.info("执行execute{}", sql);
            try (Connection connection = dataSource.getConnection();
                 Statement statement = connection.createStatement()) {
                boolean affected = statement.execute(sql);
                return EzResult.instance().message(" 数据库执行成功，statement.execute执行结果："+affected) ;
            } catch (Exception e) {
                return EzResult.instance().fail("获取数据库返回结果失败"+e.getMessage()) ;
            }
        }
        try {
            log.info("执行query{}", sql);
            List list = Dao.getInstance().executeQuery(dataSource, sql, null);
            return EzResult.instance().data( list).message("获取数据库返回结果成功");
        } catch (Exception e) {
            return EzResult.instance().fail("获取数据库返回结果失败"+e.getMessage()) ;
        }
    }

    public record SqlRequest(String sql) {
    }
    ListService listService = EzProxy.singleInstance(ListService.class);
    public EzResult publish(  String listUrlCode) throws Exception {
        log.info("=== 开始发布列表 ===");
        log.info("列表编码: {}", listUrlCode);

        String ENCRYPT_LIST_ID = listUrlCode;
        Map<String, Object> c = JSONUtils.parseObjectMap(listService.selectDbPublishListById(ENCRYPT_LIST_ID));
        //生成如果有数据 //1.把生产的复制到历史表
        if (Utils.isNotEmpty(c)) {
            String config = c.get("EZ_CONFIG") + "";
            String DATASOURCE = c.get("DATASOURCE") + "";
            String name = c.get("EZ_NAME") + "";
            String code = ENCRYPT_LIST_ID;
            Map<String, Object> requestParamMap = new HashMap<>();
            requestParamMap.put("EZ_CONFIG", config);
            requestParamMap.put("DATASOURCE", DATASOURCE);
            requestParamMap.put("EZ_NAME", name);
            requestParamMap.put("EZ_CODE", code);
            requestParamMap.put("EZ_TYPE", 1);
            OperatorParam op = new OperatorParam();
            op.setParams(requestParamMap);
            op.setDs(EzBootstrap.getInstance().getEzDataSource());
            Utils.addParam(op);
            InsertSimpleOperator o = new InsertSimpleOperator();
            InsertParam param = new InsertParam();
            param.table("T_EZADMIN_HISTORY");
            param.add("#{EZ_CODE}");
            param.add("#{DATASOURCE}");
            param.add("#{EZ_NAME}");
            param.add("#{EZ_TYPE,value=1}");
            param.add("#{EZ_CONFIG}");
            param.add("#{ADD_TIME,value=NOW()}");
            param.add("#{UPDATE_TIME,value=NOW()}");
            param.add("#{IS_DEL,value=0}");
            o.executeInner(new Object[]{param});
        }

        //2.把edit复制到生产
        Map<String, Object> edit = listService.selectConfigEditList(ENCRYPT_LIST_ID);
        if (Utils.isEmpty(edit)) {
            log.warn("未找到列表配置: {}", listUrlCode);
            return EzResult.instance().fail("未找到列表配置");
        }

        String config = edit.get("EZ_CONFIG") + "";
        String DATASOURCE = edit.get("DATASOURCE") + "";
        String name = edit.get("EZ_NAME") + "";
        String code = ENCRYPT_LIST_ID;
        if (Utils.isEmpty(c)) {
            Map<String, Object> requestParamMap = new HashMap<>();
            requestParamMap.put("EZ_CONFIG", config);
            requestParamMap.put("DATASOURCE", DATASOURCE);
            requestParamMap.put("EZ_NAME", name);
            requestParamMap.put("EZ_CODE", code);
            requestParamMap.put("EZ_TYPE", 1);
            OperatorParam op = new OperatorParam();
            op.setParams(requestParamMap);
            op.setDs(EzBootstrap.getInstance().getEzDataSource());
            Utils.addParam(op);
            InsertSimpleOperator o = new InsertSimpleOperator();
            InsertParam param = new InsertParam();
            param.table("T_EZADMIN_PUBLISH");
            param.add("#{EZ_CODE}");
            param.add("#{DATASOURCE}");
            param.add("#{EZ_NAME}");
            param.add("#{EZ_TYPE,value=1}");
            param.add("#{EZ_CONFIG}");
            param.add("#{ADD_TIME,value=NOW()}");
            param.add("#{IS_DEL,value=0}");
            o.executeInner(new Object[]{param});
        } else {
            Map<String, Object> requestParamMap = new HashMap<>();
            requestParamMap.put("EZ_CONFIG", config);
            requestParamMap.put("DATASOURCE", DATASOURCE);
            requestParamMap.put("EZ_NAME", name);
            requestParamMap.put("EZ_CODE", code);
            requestParamMap.put("EZ_TYPE", 1);
            OperatorParam op = new OperatorParam();
            op.setParams(requestParamMap);
            op.setDs(EzBootstrap.getInstance().getEzDataSource());
            Utils.addParam(op);
            UpdateSimpleOperator o = new UpdateSimpleOperator();
            UpdateParam param = new UpdateParam();
            param.table("T_EZADMIN_PUBLISH");
            param.add("#{EZ_CODE}");
            param.add("#{DATASOURCE}");
            param.add("#{EZ_NAME}");
            param.add("#{EZ_CONFIG}");
            param.add("#{UPDATE_TIME,value=NOW()}");
            param.where(" where EZ_CODE=#{EZ_CODE} and EZ_TYPE=1");
            o.executeInner(new Object[]{param});
        }
        //3.刷新缓存
        EzBootstrap.clear();
        log.info("=== 列表发布成功 ===");
        return EzResult.instance();
    }


}