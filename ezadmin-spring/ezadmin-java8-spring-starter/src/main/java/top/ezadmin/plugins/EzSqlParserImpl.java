package top.ezadmin.plugins;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.parser.SimpleNode;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.util.TablesNamesFinder;
import top.ezadmin.common.utils.JsoupUtil;
import top.ezadmin.common.utils.StringUtils;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.controller.FormEditController;
import top.ezadmin.dao.FormDao;
import top.ezadmin.dao.ListDao;
import top.ezadmin.plugins.sqlog.format.FormatStyle;
import top.ezadmin.plugins.sqlparser.EzSqlParser;
import top.ezadmin.web.EzResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EzSqlParserImpl implements EzSqlParser {
    public  String sqlToList(String sql, String listId,String datasource) throws Exception {
        EzResult ezResult= sqlToList2( sql,  listId,  "",  datasource);
        return (String) ezResult.getData();
    }
    public  String sqlToForm(String sql, String formId,String datasource) throws Exception {
        EzResult ezResult= sqlToList2( sql, "" ,  formId,  datasource);
        return (String) ezResult.getData();
    }
    private   EzResult sqlToList2(String sql, String listId, String formcode, String datasource) throws Exception {
        // String sql="select A.username as 用户名,password as 密码 ,from_unixtime(A.add_time/1000,'%y-%m')    from  T_USER A WHERE USER_ID=1 ";
        Statement statement = CCJSqlParserUtil.parse(sql);
        Select selectStatement = (Select) statement;

        PlainSelect plainSelect = (PlainSelect) selectStatement.getSelectBody();

        List<SelectItem> selectItemList = plainSelect.getSelectItems();
        if (selectItemList.size() == 1 && selectItemList.get(0) instanceof AllColumns) {
            return EzResult.instance().fail().message("请指定字段名及字段中文别名");
        }

        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
        List<String> tableNameList = tablesNamesFinder.getTableList(selectStatement);

        Map<String, Object> core = new HashMap<>();

        core.put("id", listId);
        core.put(JsoupUtil.DATASOURCE, "dataSource");

        List<Map<String, Object>> headList = new ArrayList<>();
        List<Map<String, Object>> tableBtnList = new ArrayList<>();
        List<Map<String, Object>> rowBtnList = new ArrayList<>();
        List<Map<String, Object>> searchList = new ArrayList<>();
        List<String> fieldNameList = new ArrayList<>();
        List<String> fieldLabelList = new ArrayList<>();
        for (int i = 0; i < selectItemList.size(); i++) {
            SelectExpressionItem expressionItem = (SelectExpressionItem) selectItemList.get(i);
            String colName = "";
            String tableName = "";
            if (expressionItem.getExpression() instanceof Column) {
                Column column = (Column) expressionItem.getExpression();
                colName = column.getColumnName();
                tableName = column.getTable() == null ? "" : column.getTable().getName();
            } else if (expressionItem.getExpression() instanceof Function) {
                Function function = (Function) expressionItem.getExpression();
                tableName = function.toString();
            }


            String alias = colName;
            if (expressionItem.getAlias() != null) {
                alias = expressionItem.getAlias().getName();
                try {
                    if (StringUtils.IsChinese(alias.charAt(0))) {
                        expressionItem.setAlias(null);
                    }
                } catch (Exception e) {
                }
            }

            fieldNameList.add(colName);
            fieldLabelList.add(alias);
            Map<String, Object> head = new HashMap<>();
            head.put(JsoupUtil.LABEL, alias);
            head.put(JsoupUtil.HEAD_PLUGIN_CODE, "th");
            if ("ID".equalsIgnoreCase(alias)) {
                head.put(JsoupUtil.ITEM_NAME, "ID");

                head.put(JsoupUtil.HEAD_PLUGIN_CODE, "th-numbers");
                head.put(JsoupUtil.BODY_PLUGIN_CODE, "th-numbers");
                head.put("fixed", "left");
                head.put(JsoupUtil.WIDTH, "60");

            } else {
                head.put(JsoupUtil.ITEM_NAME, StringUtils.upperCase(colName));
            }

            if (alias.contains("是否")) {
                head.put(JsoupUtil.BODY_PLUGIN_CODE, "td-select");
                head.put(JsoupUtil.DATA, "yesno");
            } else if (alias.contains("时间")) {
                head.put(JsoupUtil.JDBCTYPE, "DATETIME");
            } else {
                head.put(JsoupUtil.BODY_PLUGIN_CODE, "td-text");
            }

            headList.add(head);
            //
            Map<String, Object> search = new HashMap<>();
            search.put(JsoupUtil.LABEL, alias);
            search.put(JsoupUtil.ITEM_NAME, StringUtils.upperCase(colName));
            // search.put(JsoupUtil.NAME,StringUtils.upperCase(colName));
            //  search.put("ITEM_SORT",i*10+"");


            if (alias.contains("时间")) {
                search.put(JsoupUtil.TYPE, "daterange");
                search.put(JsoupUtil.OPER, "between");
            } else {
                search.put(JsoupUtil.TYPE, "input-text");
            }
            search.put(JsoupUtil.ALIAS, tableName);
            searchList.add(search);
        }
        StringBuilder sqlExpress = new StringBuilder();
        sqlExpress.append("\r\nStringBuilder sql=new StringBuilder();");
        sqlExpress.append("\r\nsql.append(\"" + FormatStyle.BASIC.getFormatter().format(selectStatement.toString()) + "\");");
        sqlExpress.append("\r\nreturn search(sql);");
        core.put("select_express", "\n<![CDATA[ \n" + sqlExpress.toString() + "\n]]>\n");
        if (StringUtils.isNotBlank(formcode)) {
            Map<String, Object> form = pureAddForm2(formcode, tableNameList.get(0), fieldNameList, fieldLabelList);
            String html = FormDao.getInstance().transEntityToHtmlConfig(form);
            if (StringUtils.isNotBlank(html)) {
                return EzResult.instance().data(html);
            }
        }
        Map<String, Object> list = new HashMap();
        list.put("core", core);
        list.put("search", searchList);
        list.put("tablebtn", tableBtnList);
        list.put("rowbtn", rowBtnList);
        list.put("col", headList);
        String listhtml = ListDao.getInstance().transEntityToHtmlConfig(list);
        return EzResult.instance().data(listhtml);
    }
    private   Map<String, Object> extractedRow2(String url, String name, String openType, String CC) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put(JsoupUtil.URL, url);
        map.put(JsoupUtil.OPENTYPE, openType);
        map.put(JsoupUtil.LABEL, name);
        map.put(JsoupUtil.ITEM_NAME, name);
        map.put(JsoupUtil.WINDOW_NAME, name);
        map.put(JsoupUtil.TYPE, "button-single");
        map.put(JsoupUtil.CLASS, CC);
        return map;
    }

    private   Map<String, Object> extracted2(String url, String name, String openType, String plugin) throws Exception {

        Map<String, Object> map = new HashMap<>();
        map.put(JsoupUtil.URL, url);
        map.put(JsoupUtil.OPENTYPE, openType);
        map.put(JsoupUtil.LABEL, name);
        map.put(JsoupUtil.ITEM_NAME, name);
        map.put(JsoupUtil.WINDOW_NAME, name);
        map.put(JsoupUtil.TYPE, "button-table");
        return map;
    }

    public   Map<String, Object> pureAddForm2(String formId, String table, List<String> fieldNameList, List<String> fieldLabelList) throws Exception {
        Map<String, Object> result = new HashMap<>();

        Map<String, String> form = new HashMap<>();
        result.put("core", form);
        form.put(JsoupUtil.DATASOURCE, "dataSource");
        form.put("formcode", formId);
        form.put(JsoupUtil.SUCCESS_URL,"reload");
        form.put(JsoupUtil.FORM_NAME.toLowerCase(), formId);
        StringBuilder sql = new StringBuilder("select ");
        String idName = "ID";
        for (int i = 0; i < fieldNameList.size(); i++) {
            if (fieldNameList.get(i).equals("COMPANY_ID")
            ||fieldNameList.get(i).equals("DELETE_FLAG")
                    ||fieldNameList.get(i).equals("UPDATE_ID")
                    ||fieldNameList.get(i).equals("ADD_ID")) {
                continue;
            }
            if (fieldLabelList.get(i).equals("ID")) {
                idName = fieldNameList.get(i);
                continue;
            }
            sql.append(fieldNameList.get(i));
            if (i < fieldNameList.size() - 1) {
                sql.append(",");
            }
        }
        sql.append(" from " + table + " where  " + idName + "=${ID}  ");
        StringBuilder sqlExpress = new StringBuilder(" ");
        if(fieldNameList.contains("COMPANY_ID")){
            sqlExpress.append("\r\ncompanyId=$$(\"COMPANY_ID\");\r\n");
        }
        sqlExpress.append("if(isBlank(\"ID\")){\n return new HashMap();\n}                 		\n");
        sqlExpress.append("StringBuilder sql=new StringBuilder();");
        sqlExpress.append("\nsql.append(\"" + sql.toString() + "\");");
        if(fieldNameList.contains("COMPANY_ID")){
            sqlExpress.append("\nsql.append( and COMPANY_ID=\"+companyId);");
        }


        sqlExpress.append("\nreturn selectOne(sql);");


        form.put(JsoupUtil.INIT_EXPRESS, sqlExpress.toString());


        String submitEx = generateFormExpress(table, idName, fieldNameList);
        form.put(JsoupUtil.SUBMIT_EXPRESS, submitEx);

        form.put(JsoupUtil.DELETE_EXPRESS, "sessionUserId=$$(\"EZ_SESSION_USER_ID_KEY\");\n     " +
                "companyId=$$(\"COMPANY_ID\");                  		\n" +
                "\nupdate(\"UPDATE " + table + " set delete_flag=1,UPDATE_TIME=NOW(),UPDATE_ID=\"+sessionUserId+\" where " + idName + "=${ID} and COMPANY_ID=\"+companyId);");

        form.put(JsoupUtil.STATUS_EXPRESS, "reload");

        List<Map<String, Object>> cardsList = new ArrayList<>();
        Map<String, Object> card = new HashMap<>();
        cardsList.add(card);
        List<Map<String, Object>> items = new ArrayList<>();
        card.put("items", items);

        for (int i = 0; i < fieldNameList.size(); i++) {
            Map<String, Object> item = new HashMap<>();
            String name = fieldNameList.get(i).toUpperCase();

            item.put(JsoupUtil.LABEL, fieldLabelList.get(i));
            item.put(JsoupUtil.ITEM_NAME, name);
            // item.put(JsoupUtil.NAME, name);
            if (fieldLabelList.get(i).equals("ID") || ignorField(name)) {
                continue;
            }
            if (fieldLabelList.get(i).endsWith("时间")) {
                item.put(JsoupUtil.TYPE, "input-date");
            } else {
                item.put(JsoupUtil.TYPE, "input-text");
            }
            items.add(item);
        }
        result.put("cards", cardsList);
        return result;
    }


    private   boolean ignorField(String name) {
        return name.equalsIgnoreCase("ADD_TIME")
                || name.equalsIgnoreCase("UPDATE_TIME")
                || name.equalsIgnoreCase("COMPANY_ID")
                || name.equalsIgnoreCase("ADD_ID")
                || name.equalsIgnoreCase("DELETE_FLAG")
                || name.equalsIgnoreCase("ADD_NAME");
    }

    private   String generateFormExpress(String table, String idName, List<String> fieldNameList) {
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
        ex.append("    param.table(\"" + table + "\");               		\n");

        for (int i = 0; i < fieldNameList.size(); i++) {
            if (fieldNameList.get(i).equalsIgnoreCase("ID")) {
                continue;
            }
            if (fieldNameList.get(i).equalsIgnoreCase("ADD_TIME")) {
                StringBuilder pa = new StringBuilder();
                pa.append("#{");
                pa.append(fieldNameList.get(i));
                pa.append("}");
                ex.append("   param.add(\"#{ADD_TIME,value=NOW()}\");               		\n");
                continue;
            }
            if (fieldNameList.get(i).equalsIgnoreCase("ADD_ID")) {
                StringBuilder pa = new StringBuilder();
                pa.append("#{");
                pa.append(fieldNameList.get(i));
                pa.append("}");
                ex.append("  param.add(\"#{ADD_ID,value='\"+sessionUserId+\"'}\");            		\n");
                continue;
            }
            if (fieldNameList.get(i).equalsIgnoreCase("UPDATE_TIME")) {
                StringBuilder pa = new StringBuilder();
                pa.append("#{");
                pa.append(fieldNameList.get(i));
                pa.append("}");
                ex.append("   param.add(\"#{UPDATE_TIME,value=NOW()}\");               		\n");
                continue;
            }
            if (fieldNameList.get(i).equalsIgnoreCase("UPDATE_ID")) {
                StringBuilder pa = new StringBuilder();
                pa.append("#{");
                pa.append(fieldNameList.get(i));
                pa.append("}");
                ex.append("  param.add(\"#{UPDATE_ID,value='\"+sessionUserId+\"'}\");            		\n");
                continue;
            }
            if (fieldNameList.get(i).equalsIgnoreCase("DELETE_FLAG")) {
                StringBuilder pa = new StringBuilder();
                pa.append("#{");
                pa.append(fieldNameList.get(i));
                pa.append("}");
                ex.append("  param.add(\"#{DELETE_FLAG,value=0}\");            		\n");
                continue;
            }
            if (fieldNameList.get(i).contains("STATUS")) {
                StringBuilder pa = new StringBuilder();
                pa.append("#{");
                pa.append(fieldNameList.get(i));
                pa.append("}");
                ex.append("  param.add(\"#{STATUS,value=1}\");            		\n");
                continue;
            }
            if (fieldNameList.get(i).equalsIgnoreCase("COMPANY_ID")) {
                StringBuilder pa = new StringBuilder();
                pa.append("#{");
                pa.append(fieldNameList.get(i));
                pa.append("}");
                ex.append("     param.add(\"#{COMPANY_ID,value=\"+companyId+\"}\");\n          		\n");
                continue;
            }
            StringBuilder pa = new StringBuilder();
            pa.append("#{");
            pa.append(fieldNameList.get(i));
            pa.append("}");
            ex.append("    param.add(\"" + pa.toString() + "\");               		\n");
        }


        ex.append("id=insertSimple(param);\n");
        ex.append("return id; \n");

        ex.append("}else{                 								\n");

        ex.append("    param=new UpdateParam();\n");
        ex.append("    param.table(\"" + table + "\");               		\n");

        for (int i = 0; i < fieldNameList.size(); i++) {
            if (fieldNameList.get(i).equalsIgnoreCase("ID")
                    || fieldNameList.get(i).equalsIgnoreCase("ADD_TIME")
                    || fieldNameList.get(i).equalsIgnoreCase("ADD_ID")
                    || fieldNameList.get(i).equalsIgnoreCase("COMPANY_ID")
            ) {
                continue;
            }
            if (fieldNameList.get(i).equalsIgnoreCase("UPDATE_TIME")) {
                StringBuilder pa = new StringBuilder();
                pa.append("#{");
                pa.append(fieldNameList.get(i));
                pa.append("}");
                ex.append("   param.add(\"#{UPDATE_TIME,value=NOW()}\");               		\n");
                continue;
            }
            if (fieldNameList.get(i).equalsIgnoreCase("UPDATE_ID")) {
                StringBuilder pa = new StringBuilder();
                pa.append("#{");
                pa.append(fieldNameList.get(i));
                pa.append("}");
                ex.append("  param.add(\"#{UPDATE_ID,value='\"+sessionUserId+\"'}\");            		\n");
                continue;
            }


            StringBuilder pa = new StringBuilder();
            pa.append("#{");
            pa.append(fieldNameList.get(i));
            pa.append("}");
            ex.append("    param.add(\"" + pa.toString() + "\");               		\n");
        }


        ex.append("  StringBuilder updateSql=new StringBuilder();\n");
        ex.append("  updateSql.append(\" where " + idName + "=#{ID} \");\n");
        if(fieldNameList.contains("COMPANY_ID")){
            ex.append("   updateSql.append(\" and COMPANY_ID= \"+companyId);\n");
        }


        ex.append(" param.where(updateSql.toString());\n");
        ex.append(" updateSimple(param);\n");
        ex.append(" return ID;\n");

        ex.append("}");
        return ex.toString();
    }

}