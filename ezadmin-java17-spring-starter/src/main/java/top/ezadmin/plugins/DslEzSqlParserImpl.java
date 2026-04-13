package top.ezadmin.plugins;

import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.util.TablesNamesFinder;
import top.ezadmin.common.utils.JsoupUtil;
import top.ezadmin.common.utils.StringUtils;
import top.ezadmin.plugins.sqlog.format.FormatStyle;
import top.ezadmin.plugins.sqlparser.EzSqlParser;
import top.ezadmin.web.EzResult;

import java.util.*;

public class DslEzSqlParserImpl implements EzSqlParser {
    
    @Override
    public String sqlToList(String sql, String listId, String datasource) throws Exception {
        EzResult ezResult = parseSqlToList(sql, listId, datasource);
        return (String) ezResult.getData();
    }

    @Override
    public String sqlToForm(String sql, String formId, String datasource) throws Exception {
        EzResult ezResult = parseSqlToForm(sql, formId, datasource);
        return (String) ezResult.getData();
    }

    private EzResult parseSqlToList(String sql, String listId, String datasource) throws Exception {
        Statement statement = CCJSqlParserUtil.parse(sql);
        Select selectStatement = (Select) statement;

        PlainSelect plainSelect = (PlainSelect) selectStatement.getSelectBody();

        // 修复JSQLParser 4.9版本的类型不兼容问题
        List<SelectItem<?>> selectItemList = plainSelect.getSelectItems(); 
        // 修复AllColumns类型检查问题
        if (selectItemList.size() == 1 && selectItemList.get(0).toString().equals("*")) {
            return EzResult.instance().fail().message("请指定字段名及字段中文别名");
        }

        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
        List<String> tableNameList = tablesNamesFinder.getTableList((Statement) selectStatement);

        // 构建DSL JSON配置
        Map<String, Object> config = new HashMap<>();
        config.put("id", listId);
        config.put("name", listId);
        config.put("dataSource", "dataSource");

        // body配置
        Map<String, Object> body = new HashMap<>();
        body.put("emptyShow", "@");
        body.put("showIndex", true);
        body.put("selectable", false);
        body.put("rowActionWidth", 175);
        config.put("body", body);

        // 搜索项配置
        List<Map<String, Object>> searchList = new ArrayList<>();
        
        // 列配置
        List<Map<String, Object>> columnList = new ArrayList<>();
        
        // 表格按钮配置
        List<Map<String, Object>> tableButtonList = new ArrayList<>();
        
        // 行按钮配置
        List<Map<String, Object>> rowButtonList = new ArrayList<>();
        
        // 表达式配置
        Map<String, Object> express = new HashMap<>();
        StringBuilder sqlExpress = new StringBuilder();
        sqlExpress.append("StringBuilder sql=new StringBuilder();");
        String formattedSql = FormatStyle.BASIC.getFormatter().format(selectStatement.toString());
        sqlExpress.append("sql.append(\"").append(formattedSql.replace("\"", "\\\"")).append("\");");
        sqlExpress.append("return search(sql);");
        express.put("main", sqlExpress.toString());

        List<String> fieldNameList = new ArrayList<>();
        List<String> fieldLabelList = new ArrayList<>();

        for (int i = 0; i < selectItemList.size(); i++) {
            SelectItem<?> selectItem = selectItemList.get(i);
            String item_name = "UNKNOWN";
            String tablealias = "UNKNOWN";
            String label = "UNKNOWN";

            try {
                if(selectItem.getExpression() instanceof Column){
                    Column column=(Column)selectItem.getExpression();
                    item_name=column.getColumnName();
                    tablealias=column.getTable()==null?"":column.getTable().getName();
                }else if(selectItem.getExpression() instanceof Function){
                    Function column=(Function)selectItem.getExpression();
                    item_name=column.toString();
                    tablealias="";
                }else{
                    System.out.println("无法解析的情况"+selectItem.toString());
                }
                label=selectItem.getAlias()==null?item_name:selectItem.getAlias().getName();
            } catch (Exception e) {
                // 忽略异常
                e.printStackTrace();
            }

            fieldNameList.add(StringUtils.upperCase(item_name));
            fieldLabelList.add(label);

            // 添加列配置
            Map<String, Object> column = new HashMap<>();
            column.put("item_name", StringUtils.upperCase(item_name));
            column.put("label", label);
            column.put("component", "tdText");
            
            // 根据字段名称设置不同的组件类型
            if ("ID".equalsIgnoreCase(label)) {
                column.put("component", "tdText");
                Map<String, Object> props = new HashMap<>();
                props.put("width", 60);
                column.put("props", props);
            } else if (label.contains("是否")) {
                column.put("component", "tdSelect");
                Map<String, Object> initData = new HashMap<>();
                List<Map<String, String>> dataJson = new ArrayList<>();
                Map<String, String> yesOption = new HashMap<>();
                yesOption.put("label", "是");
                yesOption.put("value", "1");
                Map<String, String> noOption = new HashMap<>();
                noOption.put("label", "否");
                noOption.put("value", "0");
                dataJson.add(yesOption);
                dataJson.add(noOption);
                initData.put("dataJson", dataJson);
                column.put("initData", initData);
            } else if (label.contains("时间")) {
                column.put("component", "tdText");
                Map<String, Object> props = new HashMap<>();
                props.put("format", "yyyy-MM-dd HH:mm:ss");
                props.put("sort", true);
                column.put("props", props);
                column.put("jdbcType", "DATETIME");
            }
            
            if (!StringUtils.upperCase(item_name).equals("COMPANY_ID")
                && !StringUtils.upperCase(item_name).equals("DELETE_FLAG")) {
                columnList.add(column);
            }

            // 添加搜索项配置
            Map<String, Object> search = new HashMap<>();
            search.put("item_name", StringUtils.upperCase(item_name));
            search.put("label", label);
            search.put("component", "input");
            search.put("alias", tablealias + "." + item_name);
            
            if (label.contains("时间")) {
                search.put("component", "date");
                search.put("operator", "between");
                search.put("jdbcType", "DATETIME");
                Map<String, Object> props = new HashMap<>();
                props.put("placeholder", " - ");
                props.put("range", true);
                search.put("props", props);
            } else {
                search.put("component", "input");
                search.put("operator", "like");
                Map<String, Object> props = new HashMap<>();
                props.put("placeholder", "请输入" + label);
                search.put("props", props);
            }
            
            if (!StringUtils.upperCase(item_name).equals("ID")
                    && !StringUtils.upperCase(item_name).equals("COMPANY_ID")
                    && !StringUtils.upperCase(item_name).equals("DELETE_FLAG")) {
                searchList.add(search);
            }
        }

        // 添加默认的表格按钮
        Map<String, Object> addButton = new HashMap<>();
        addButton.put("item_name", "add");
        addButton.put("label", "新增");
        addButton.put("component", "button-toolbar");
        Map<String, Object> addProps = new HashMap<>();
        addProps.put("opentype", "FORM");
        addProps.put("url", "/topezadmin/form/form-" + listId);
        addButton.put("props", addProps);
        tableButtonList.add(addButton);

        // 添加默认的行按钮
        Map<String, Object> editButton = new HashMap<>();
        editButton.put("item_name", "edit");
        editButton.put("label", "编辑");
        editButton.put("component", "button-normal");
        Map<String, Object> editProps = new HashMap<>();
        editProps.put("opentype", "FORM");
        editProps.put("url", "/topezadmin/form/form-" + listId + "?ID={{= d.ID }}");
        editButton.put("props", editProps);
        rowButtonList.add(editButton);

        Map<String, Object> deleteButton = new HashMap<>();
        deleteButton.put("item_name", "delete");
        deleteButton.put("label", "删除");
        deleteButton.put("component", "button-normal");
        Map<String, Object> delProps = new HashMap<>();
        delProps.put("opentype", "CONFIRM_AJAX");
        delProps.put("url", "/topezadmin/form/doDelete-" + listId + "?ID={{= d.ID }}");
        deleteButton.put("props", delProps);
        rowButtonList.add(deleteButton);

        config.put("search", searchList);
        config.put("column", columnList);
        config.put("tableButton", tableButtonList);
        config.put("rowButton", rowButtonList);
        config.put("express", express);

        String jsonString = convertMapToJson(config);
        return EzResult.instance().data(jsonString);
    }

    private EzResult parseSqlToForm(String sql, String formId, String datasource) throws Exception {
        Statement statement = CCJSqlParserUtil.parse(sql);
        Select selectStatement = (Select) statement;

        PlainSelect plainSelect = (PlainSelect) selectStatement.getSelectBody();

        // 修复JSQLParser 4.9版本的类型不兼容问题
        List<SelectItem<?>> selectItemList = plainSelect.getSelectItems(); 
        // 修复AllColumns类型检查问题
        if (selectItemList.size() == 1 && selectItemList.get(0).toString().equals("*")) {
            return EzResult.instance().fail().message("请指定字段名及字段中文别名");
        }

        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
        List<String> tableNameList = tablesNamesFinder.getTableList((Statement) selectStatement);

        // 构建表单DSL JSON配置
        Map<String, Object> config = new HashMap<>();
        config.put("id", formId);
        config.put("name", formId + "表单");
        config.put("dataSource", "dataSource");

        // 初始化表达式
        List<String> initExpress = new ArrayList<>();
        StringBuilder initSql = new StringBuilder("StringBuilder sql=new StringBuilder(); ");
        String formattedSql = FormatStyle.BASIC.getFormatter().format(selectStatement.toString());
        initSql.append("sql.append(\"").append(formattedSql.replace("\"", "\\\"")).append(" and ID=${ID}\"); return selectOne(sql);");
        initExpress.add(initSql.toString());
        config.put("initExpress", initExpress);

        // 提交表达式
        List<String> submitExpress = new ArrayList<>();
        String submitStr = generateFormSubmitExpression(tableNameList.get(0), selectItemList);
        submitExpress.add(submitStr);
        config.put("submitExpress", submitExpress);

        // 删除表达式
        List<String> deleteExpress = new ArrayList<>();
        String deleteStr = "update(\"UPDATE " + tableNameList.get(0) + " set delete_flag=1 where ID=${ID}\");";
        deleteExpress.add(deleteStr);
        config.put("deleteExpress", deleteExpress);

        // 状态表达式
        List<String> statusExpress = new ArrayList<>();
        statusExpress.add("reload");
        config.put("statusExpress", statusExpress);

        // 卡片列表配置
        List<Map<String, Object>> cardList = new ArrayList<>();
        Map<String, Object> card = new HashMap<>();
        card.put("item_name", "basic_info");
        card.put("label", "基本信息");
        card.put("description", "基本表单信息");
        card.put("col", "12");
        card.put("type", "card");

        // 字段列表
        List<Map<String, Object>> fieldList = new ArrayList<>();
        
        // 按钮列表
        List<Map<String, Object>> buttonList = new ArrayList<>();

        List<String> fieldNameList = new ArrayList<>();
        List<String> fieldLabelList = new ArrayList<>();

        for (int i = 0; i < selectItemList.size(); i++) {
            SelectItem<?> selectItem = selectItemList.get(i);
            String item_name = "UNKNOWN";
            String label = "UNKNOWN";

            try {
                if(selectItem.getExpression() instanceof Column){
                    Column column=(Column)selectItem.getExpression();
                    item_name=column.getColumnName();
                }else if(selectItem.getExpression() instanceof Function){
                    Function column=(Function)selectItem.getExpression();
                    item_name=column.toString();
                }else{
                    System.out.println("无法解析的情况"+selectItem.toString());
                }
                label=selectItem.getAlias()==null?item_name:selectItem.getAlias().getName();
            } catch (Exception e) {
                // 忽略异常
                e.printStackTrace();
            }

            fieldNameList.add(StringUtils.upperCase(item_name));
            fieldLabelList.add(label);

            // 添加字段配置
            Map<String, Object> field = new HashMap<>();
            field.put("item_name", StringUtils.upperCase(item_name));
            field.put("label", label);
            field.put("col", "12");

            // 根据字段名称设置不同的组件类型
            if ("ID".equalsIgnoreCase(label)) {
                field.put("component", "hidden");
                Map<String, Object> props = new HashMap<>();
                props.put("type", "hidden");
                field.put("props", props);
            } else if (label.contains("是否")) {
                field.put("component", "select");
                Map<String, Object> props = new HashMap<>();
                props.put("placeholder", "请选择" + label);
                field.put("props", props);
                
                // 初始化数据
                Map<String, Object> initData = new HashMap<>();
                List<Map<String, String>> dataJson = new ArrayList<>();
                Map<String, String> yesOption = new HashMap<>();
                yesOption.put("label", "是");
                yesOption.put("value", "1");
                Map<String, String> noOption = new HashMap<>();
                noOption.put("label", "否");
                noOption.put("value", "0");
                dataJson.add(yesOption);
                dataJson.add(noOption);
                initData.put("dataJson", dataJson);
                field.put("initData", initData);
            } else if (label.contains("时间")) {
                field.put("component", "date");
                Map<String, Object> props = new HashMap<>();
                props.put("placeholder", "请选择" + label);
                props.put("format", "yyyy-MM-dd HH:mm:ss");
                field.put("props", props);
                field.put("jdbcType", "DATETIME");
            } else {
                field.put("component", "input");
                Map<String, Object> props = new HashMap<>();
                props.put("placeholder", "请输入" + label);
                field.put("props", props);
            }
            
            fieldList.add(field);
        }

        card.put("fieldList", fieldList);
        card.put("buttonList", buttonList);
        cardList.add(card);
        
        config.put("cardList", cardList);
        config.put("buttonList", buttonList);

        String jsonString = convertMapToJson(config);
        return EzResult.instance().data(jsonString);
    }

    private String generateFormSubmitExpression(String tableName, List<SelectItem<?>> selectItemList) {
        StringBuilder ex = new StringBuilder();
        ex.append("\nimport top.ezadmin.plugins.express.jdbc.UpdateParam;\n");
        ex.append("import top.ezadmin.plugins.express.jdbc.InsertParam;                  		\n");
        ex.append("import top.ezadmin.web.EzResult;                  		\n");

        ex.append("ID=$(\"ID\");                  		\n");
        ex.append("companyId=$$(\"COMPANY_ID\");                  		\n");
        ex.append("sessionUserId=$$(\"EZ_SESSION_USER_ID_KEY\");                  		\n");
        ex.append("sessionUserName=$$(\"EZ_SESSION_USER_NAME_KEY\");                  		\n");

        ex.append("if(!isNotBlank(\"ID\")){                  		\n");
        ex.append("    param=new InsertParam();                		\n");
        ex.append("    param.table(\"" + tableName + "\");               		\n");

        for (int i = 0; i < selectItemList.size(); i++) {
            SelectItem<?> selectItem = selectItemList.get(i);
            String item_name = "UNKNOWN";
            String label = "UNKNOWN";

            try {
                if(selectItem.getExpression() instanceof Column){
                    Column column=(Column)selectItem.getExpression();
                    item_name=column.getColumnName();
                }else if(selectItem.getExpression() instanceof Function){
                    Function column=(Function)selectItem.getExpression();
                    item_name=column.toString();
                }else{
                    System.out.println("无法解析的情况"+selectItem.toString());
                }
                label=selectItem.getAlias()==null?item_name:selectItem.getAlias().getName();
            } catch (Exception e) {
                // 忽略异常
                e.printStackTrace();
            }

            if ("ID".equalsIgnoreCase(label)) {
                continue;
            }
            if ("ADD_TIME".equalsIgnoreCase(item_name)) {
                ex.append("   param.add(\"#{ADD_TIME,value=NOW()}\");               		\n");
                continue;
            }
            if ("ADD_ID".equalsIgnoreCase(item_name)) {
                ex.append("  param.add(\"#{ADD_ID,value='\"+sessionUserId+\"'}\");            		\n");
                continue;
            }
            if ("UPDATE_TIME".equalsIgnoreCase(item_name)) {
                ex.append("   param.add(\"#{UPDATE_TIME,value=NOW()}\");               		\n");
                continue;
            }
            if ("UPDATE_ID".equalsIgnoreCase(item_name)) {
                ex.append("  param.add(\"#{UPDATE_ID,value='\"+sessionUserId+\"'}\");            		\n");
                continue;
            }
            if ("DELETE_FLAG".equalsIgnoreCase(item_name)) {
                ex.append("  param.add(\"#{DELETE_FLAG,value=0}\");            		\n");
                continue;
            }
            if (item_name.toUpperCase().contains("STATUS")) {
                ex.append("  param.add(\"#{STATUS,value=1}\");            		\n");
                continue;
            }
            if ("COMPANY_ID".equalsIgnoreCase(item_name)) {
                ex.append("     param.add(\"#{COMPANY_ID,value=\"+companyId+\"}\");\n          		\n");
                continue;
            }
            
            StringBuilder pa = new StringBuilder();
            pa.append("#{");
            pa.append(item_name);
            pa.append("}");
            ex.append("    param.add(\"" + pa.toString() + "\");               		\n");
        }

        ex.append("id=insertSimple(param);\n");
        ex.append("return id; \n");

        ex.append("}else{                 								\n");

        ex.append("    param=new UpdateParam();\n");
        ex.append("    param.table(\"" + tableName + "\");               		\n");

        for (int i = 0; i < selectItemList.size(); i++) {
            SelectItem<?> selectItem = selectItemList.get(i);
            String item_name = "UNKNOWN";
            String label = "UNKNOWN";

            try {
                if(selectItem.getExpression() instanceof Column){
                    Column column=(Column)selectItem.getExpression();
                    item_name=column.getColumnName();
                }else if(selectItem.getExpression() instanceof Function){
                    Function column=(Function)selectItem.getExpression();
                    item_name=column.toString();
                }else{
                    System.out.println("无法解析的情况"+selectItem.toString());
                }
                label=selectItem.getAlias()==null?item_name:selectItem.getAlias().getName();
            } catch (Exception e) {
                // 忽略异常
                e.printStackTrace();
            }

            if ("ID".equalsIgnoreCase(label) || "ADD_TIME".equalsIgnoreCase(item_name)
                    || "ADD_ID".equalsIgnoreCase(item_name) || "COMPANY_ID".equalsIgnoreCase(item_name)) {
                continue;
            }
            if ("UPDATE_TIME".equalsIgnoreCase(item_name)) {
                ex.append("   param.add(\"#{UPDATE_TIME,value=NOW()}\");               		\n");
                continue;
            }
            if ("UPDATE_ID".equalsIgnoreCase(item_name)) {
                ex.append("  param.add(\"#{UPDATE_ID,value='\"+sessionUserId+\"'}\");            		\n");
                continue;
            }

            StringBuilder pa = new StringBuilder();
            pa.append("#{");
            pa.append(item_name);
            pa.append("}");
            ex.append("    param.add(\"" + pa.toString() + "\");               		\n");
        }

        ex.append("  StringBuilder updateSql=new StringBuilder();\n");
        ex.append("  updateSql.append(\" where ID=#{ID} \");\n");
        ex.append("   updateSql.append(\" and COMPANY_ID= \"+companyId);\n");

        ex.append(" param.where(updateSql.toString());\n");
        ex.append(" updateSimple(param);\n");
        ex.append(" return ID;\n");

        ex.append("}");
        return ex.toString();
    }

    private String convertMapToJson(Map<String, Object> map) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        int size = map.size();
        int count = 0;
        
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            json.append("  \"").append(entry.getKey()).append("\": ");
            
            Object value = entry.getValue();
            if (value instanceof String) {
                json.append("\"").append(value.toString().replace("\\", "\\\\").replace("\"", "\\\"")).append("\"");
            } else if (value instanceof Integer || value instanceof Long || value instanceof Double || value instanceof Boolean) {
                json.append(value.toString());
            } else if (value instanceof List) {
                json.append(convertListToJson((List<?>) value));
            } else if (value instanceof Map) {
                json.append(convertMapToJson((Map<String, Object>) value));
            } else {
                json.append("\"").append(value != null ? value.toString() : "").append("\"");
            }
            
            count++;
            if (count < size) {
                json.append(",");
            }
            json.append("\n");
        }
        json.append("}");
        return json.toString();
    }

    private String convertListToJson(List<?> list) {
        StringBuilder json = new StringBuilder();
        json.append("[\n");
        int size = list.size();
        for (int i = 0; i < size; i++) {
            Object item = list.get(i);
            if (item instanceof String) {
                json.append("    \"").append(item.toString().replace("\\", "\\\\").replace("\"", "\\\"")).append("\"");
            } else if (item instanceof Integer || item instanceof Long || item instanceof Double || item instanceof Boolean) {
                json.append("    ").append(item.toString());
            } else if (item instanceof List) {
                json.append("    ").append(convertListToJson((List<?>) item));
            } else if (item instanceof Map) {
                json.append("    ").append(convertMapToJson((Map<String, Object>) item));
            } else {
                json.append("    \"").append(item != null ? item.toString() : "").append("\"");
            }
            
            if (i < size - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        json.append("  ]");
        return json.toString();
    }
}