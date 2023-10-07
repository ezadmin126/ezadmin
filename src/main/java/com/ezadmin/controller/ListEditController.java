package com.ezadmin.controller;

import com.ezadmin.EzBootstrap;
import com.ezadmin.dao.FormDao;
import com.ezadmin.dao.ListDao;
import com.ezadmin.dao.PluginsDao;
import com.ezadmin.service.ListService;
import com.ezadmin.common.annotation.EzMapping;
import com.ezadmin.common.utils.*;
import com.ezadmin.plugins.sqlog.format.FormatStyle;
import com.ezadmin.web.EzResult;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.util.TablesNamesFinder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @title: ListEditController
 * @Author EzAdmin
 * @Date: 2022/3/9 13:03
 */
@EzMapping("/ezadmin/list/")
public class ListEditController extends BaseController {

    ListService listService = EzProxy.singleInstance(ListService.class);


    @EzMapping("loadEdit.html")
    public String loadList(HttpServletRequest request, HttpServletResponse response) throws Exception {
             List<Map<String, Object>> searchPlugins= PluginsDao.getInstance().allListPlugin("search");
            List<Map<String, Object>> tdPlugins= PluginsDao.getInstance().allListPlugin("td");
            request.setAttribute("searchPlugins",searchPlugins);
            request.setAttribute("tdPlugins",tdPlugins);

        String ENCRYPT_LIST_ID = Utils.trimNull(request.getAttribute("ENCRYPT_LIST_ID"));

        Map<String, Object> requestParamMap =requestToMap(request);
        Map<String, String> sessionParamMap = sessionToMap(request.getSession());

        requestParamMap.put("loadDataFlag",0);
        Map<String, Object> list=new HashMap<>();
        if(StringUtils.isNotBlank(ENCRYPT_LIST_ID)){
            list=   JSONUtils.parseObjectMap(listService.selectAllListById(ENCRYPT_LIST_ID))  ;
        }
        if(!Utils.isNotEmpty(list)){
            list=new HashMap();
            list.put("core",new HashMap());
            list.put("search",new ArrayList<>());
            list.put("tab",new ArrayList<>());
            list.put("tablebtn",new ArrayList<>());
            list.put("rowbtn",new ArrayList<>());
            list.put("col",new ArrayList<>());
        }else{
            listService.fillListById(list,requestParamMap,sessionParamMap);
        }

            request.setAttribute("data",list);

            return "layui/listedit";
    }
    @EzMapping("submitEdit.html")
    public EzResult edit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //所有表单类型的插件
        System.out.println(request.getParameter("data"));
        Map<String, Object> form=JSONUtils.parseObjectMap(request.getParameter("data"));

        ListDao.getInstance().updateList(form);

        return EzResult.instance();
    }


    @EzMapping("importlist.html")
    public void importlist(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String listcode = request.getParameter("listcode");
        String formcode = request.getParameter("formcode");
        String fasttext = request.getParameter("listexpress");

        sqlToList2(fasttext,listcode,formcode);
        EzBootstrap.instance().getCache().clear();
        if(StringUtils.isNotBlank(listcode)) {
            response.sendRedirect(request.getContextPath() + "/ezadmin/list/loadEdit-" + listcode);
            return;
        }
        if(StringUtils.isNotBlank(formcode)) {
            response.sendRedirect(request.getContextPath() + "/ezadmin/form/loadEdit-" + formcode);
        }
    }

    public   void sqlToList2(String sql, String listId, String formcode) throws Exception {
        // String sql="select A.username as 用户名,password as 密码 ,from_unixtime(A.add_time/1000,'%y-%m')    from  T_USER A WHERE USER_ID=1 ";
        Statement statement = CCJSqlParserUtil.parse(sql);
        Select selectStatement = (Select) statement;

        PlainSelect plainSelect=(PlainSelect)selectStatement.getSelectBody();

        List<SelectItem> selectItemList= plainSelect.getSelectItems();

        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
        List<String> tableNameList = tablesNamesFinder.getTableList(selectStatement);

        Map<String,Object> core=new HashMap<>();

        core.put("listcode",listId);
        core.put(JsoupUtil.DATASOURCE,"dataSource");

        List<Map<String, Object>> headList=new ArrayList<>();
        List<Map<String, Object>> tableBtnList=new ArrayList<>();
        List<Map<String, Object>> rowBtnList=new ArrayList<>();
        List<Map<String, Object>> searchList=new ArrayList<>();




        List<String> fieldNameList=new ArrayList<>();
        List<String> fieldLabelList=new ArrayList<>();
        for (int i = 0; i < selectItemList.size(); i++) {
            SelectExpressionItem expressionItem=(SelectExpressionItem)selectItemList.get(i);
            String colName="";
            String tableName="";
            if(expressionItem.getExpression() instanceof Column){
                Column column=(Column)expressionItem.getExpression();
                colName=column.getColumnName();
                tableName=column.getTable()==null?"":column.getTable().getName();
            }else if(expressionItem.getExpression() instanceof Function){
                Function function =(Function)expressionItem.getExpression();
                tableName=function.toString();
            }


            String alias=colName;
            if(expressionItem.getAlias()!=null){
                alias=expressionItem.getAlias().getName();
                try {
                    if (StringUtils.IsChinese(alias.charAt(0))) {
                        expressionItem.setAlias(null);
                    }
                }catch (Exception e){}
            }

            fieldNameList.add(colName);
            fieldLabelList.add(alias);
            Map<String,Object> head=new HashMap<>();
            head.put(JsoupUtil.LABEL,alias);
            if("ID".equalsIgnoreCase(alias)) {
                head.put(JsoupUtil.ITEM_NAME,"ID");
            }else{
                head.put(JsoupUtil.ITEM_NAME, StringUtils.upperCase(colName));
            }
            head.put(JsoupUtil.HEAD_PLUGIN_CODE, "th");
            if(colName.equalsIgnoreCase("COMPANY_ID")){
                head.put(JsoupUtil.BODY_PLUGIN_CODE, "td-select");
                head.put(JsoupUtil.DATA, "SELECT COMPANY_ID K,COMPANY_NAME V FROM T_SYS_COMPANY");
                head.put(JsoupUtil.DATATYPE, "KVSQLCACHE");
            }
            else if(alias.contains("是否")){
                head.put(JsoupUtil.BODY_PLUGIN_CODE, "td-select");
                head.put(JsoupUtil.DATA, "yesno");
            }
            else  if(alias.contains("时间")){
                head.put(JsoupUtil.JDBCTYPE,"DATETIME");
            }
            else{
                head.put(JsoupUtil.BODY_PLUGIN_CODE, "td-text");
            }

            headList.add(head);
            //
            Map<String,Object> search=new HashMap<>();
            search.put(JsoupUtil.LABEL,alias);
            search.put(JsoupUtil.ITEM_NAME,StringUtils.upperCase(colName));
            search.put(JsoupUtil.NAME,StringUtils.upperCase(colName));
            //  search.put("ITEM_SORT",i*10+"");



            if(colName.equalsIgnoreCase("COMPANY_ID")){
                search.put(JsoupUtil.PLUGIN,"select-search");
                search.put(JsoupUtil.TYPE,"select-search");
                search.put(JsoupUtil.DATA, "SELECT COMPANY_ID K,COMPANY_NAME V FROM T_SYS_COMPANY");
                search.put(JsoupUtil.DATATYPE, "KVSQLCACHE");
            }
            else  if(alias.contains("时间")){
                search.put(JsoupUtil.PLUGIN,"daterange");
                search.put(JsoupUtil.TYPE,"daterange");
                search.put(JsoupUtil.OPER, "between");
            }
            else{
                search.put(JsoupUtil.PLUGIN,"input-text");
                search.put(JsoupUtil.TYPE,"input-text");
            }

            search.put(JsoupUtil.ALIAS,tableName);

            searchList.add(search);
        }
        StringBuilder sqlExpress=new StringBuilder();
        sqlExpress.append("\r\nStringBuilder sql=new StringBuilder();");

        // sqlExpress.append("\r\ncompanyId=$$(\"COMPANY_ID\");");
        sqlExpress.append("\r\nsql.append(\""+ FormatStyle.BASIC.getFormatter().format(selectStatement.toString())+"\");");
//        sqlExpress.append("\r\nif(isNotBlank(\"COMPANY_ID\",\"session\")){" +
//                "sql.append(\" AND COMPANY_ID=\"+companyId);" +
//                "}");
        sqlExpress.append("\r\nreturn search(sql);");

        core.put("select_express", sqlExpress.toString());



        if(StringUtils.isNotBlank(formcode)){
            tableBtnList.add(extracted2("/ezadmin/form/form-"+formcode, "新增","PARENT","button-table"));
            rowBtnList.add( extractedRow2("/ezadmin/form/form-"+formcode+"?ID=${ID}", "编辑","MODEL","layui-border-blue"));
            rowBtnList.add(extractedRow2("/ezadmin/form/doDelete-"+formcode+"?ID=${ID}", "删除","CONFIRM_AJAX","layui-border-red"));
            Map<String,Object> form= pureAddForm2( listId,tableNameList.get(0),fieldNameList,fieldLabelList);
            FormDao.getInstance().updateForm(form);
        }
        Map<String,Object> list=new HashMap();
        list.put("core",core);
        list.put("search",searchList);
        list.put("tablebtn",tableBtnList);
        list.put("rowbtn",rowBtnList);
        list.put("col",headList);
        ListDao.getInstance().updateList(list);
    }
    private Map<String,Object> extractedRow2(String url, String name,String openType,String CC) throws Exception {
        Map<String,Object> map=new HashMap<>();
        map.put(JsoupUtil.URL,url);
        map.put(JsoupUtil.OPENTYPE,openType);
        map.put(JsoupUtil.LABEL,name);
        map.put(JsoupUtil.ITEM_NAME,name);
        map.put(JsoupUtil.WINDOW_NAME,name);
         map.put(JsoupUtil.TYPE,"button-single");
        map.put(JsoupUtil.CLASS,CC);
        return map;
    }
    private  Map<String, Object>  extracted2(String url, String name,String openType,String plugin) throws Exception {

        Map<String,Object> map=new HashMap<>();
        map.put(JsoupUtil.URL,url);
        map.put(JsoupUtil.OPENTYPE,openType);
        map.put(JsoupUtil.LABEL,name);
        map.put(JsoupUtil.ITEM_NAME,name);
        map.put(JsoupUtil.WINDOW_NAME,name);
         map.put(JsoupUtil.TYPE,"button-table");
         return map;
    }

    public Map<String, Object> pureAddForm2(String formId,String table, List<String> fieldNameList,List<String> fieldLabelList) throws  Exception {
        Map<String, Object> result=new HashMap<>();

        Map<String, String> form=new HashMap<>();
        result.put("core",form);
        form.put(JsoupUtil.DATASOURCE, "dataSource");
        form.put("formcode", formId);
        form.put(JsoupUtil.FORM_NAME.toLowerCase(), formId);
        StringBuilder sql=new StringBuilder("select ");
        String idName="ID";
        for (int i = 0; i < fieldNameList.size(); i++) {
            if(fieldLabelList.get(i).equals("ID")){
                idName=fieldNameList.get(i);
                continue;
            }
            sql.append(fieldNameList.get(i));
            if(i<fieldNameList.size()-1){
                sql.append(",");
            }
        }
        sql.append(" from "+table +" where  "+idName+"=${ID}  " );

        StringBuilder sqlExpress=new StringBuilder(" ");

        sqlExpress.append("StringBuilder sql=new StringBuilder();");
        sqlExpress.append("\nsql.append(\""+sql.toString()+"\");");
        sqlExpress.append("\nreturn select(sql).get(0);");


        form.put("initcode", sqlExpress.toString());

        StringBuilder insertFields=new StringBuilder();
        StringBuilder insertValues=new StringBuilder();
        StringBuilder updateValues=new StringBuilder();
        for (int i = 0; i < fieldNameList.size(); i++) {
            String name=fieldNameList.get(i).toUpperCase();
            if(fieldLabelList.get(i).equals("ID")||ignorField(name)  ){
                continue;
            }
            insertFields.append(",\t\t"+fieldNameList.get(i)+"\n");
            insertValues.append(",\t\t"+"#{"+name+"}"+"\n") ;
            updateValues.append(",\t\t"+name+ " = #{" +name+"}"+"\n" );
        }

        String submitEx=generateFormExpress(table,idName,fieldNameList );
        form.put("subcode", submitEx);
        form.put("delcode", "\nupdate(\"UPDATE "+table+" set delete_flag=1 where "+idName+"=${ID}\");");

        form.put("successurl","reload");

        List<Map<String,Object>> cardsList=new ArrayList<>();
        Map<String,Object> card=new HashMap<>();cardsList.add(card);
        List<Map<String,Object>> items=new ArrayList<>();
        card.put("items",items);

        for (int i = 0; i < fieldNameList.size(); i++) {
            Map<String,Object> item=new HashMap<>();
            String name=fieldNameList.get(i).toUpperCase();

            item.put(JsoupUtil.LABEL, fieldLabelList.get(i));
            item.put(JsoupUtil.ITEM_NAME, name);
            item.put(JsoupUtil.NAME, name);
            if(fieldLabelList.get(i).equals("ID")||ignorField(name)  ){
                continue;
            }
            if(fieldLabelList.get(i).endsWith("时间")){
                item.put(JsoupUtil.PLUGIN, "input-date");
            }else{
                item.put(JsoupUtil.PLUGIN, "input-text");
            }
            items.add(item);
        }
        result.put("cards",cardsList);
        return result;
    }


    public boolean ignorField(String name){
        return name.equalsIgnoreCase("ADD_TIME")
                || name.equalsIgnoreCase("UPDATE_TIME")
                || name.equalsIgnoreCase("COMPANY_ID")
                || name.equalsIgnoreCase("ADD_ID")
                || name.equalsIgnoreCase("ADD_NAME");
    }

    private String generateFormExpress(String table,String idName,List<String> fieldNameList){
        StringBuilder ex=new StringBuilder();
        ex.append("\nimport com.ezadmin.plugins.express.jdbc.UpdateParam;\n");
        ex.append("import com.ezadmin.plugins.express.jdbc.InsertParam;                  		\n");
        ex.append("import com.ezadmin.web.EzResult;                  		\n");

        ex.append("companyId=$$(\"COMPANY_ID\");                 		\n");
        ex.append("sessionUserId=$$(\"EZ_SESSION_USER_ID_KEY\");        \n");
        ex.append("sessionUserName=$$(\"EZ_SESSION_USER_NAME_KEY\");    \n");
        ex.append("if(!isNotBlank(\"ID\")){                  		\n");
        ex.append("    param=new InsertParam();                		\n");
        ex.append("    param.table(\""+table+"\");               		\n");

        for (int i=0;i<fieldNameList.size();i++){
            StringBuilder pa=new StringBuilder();
            pa.append("#{");
            pa.append(fieldNameList.get(i));
            pa.append("}");
            ex.append("    param.add(\""+pa.toString()+"\");               		\n");
        }
       // ex.append("    param.add(\"#{COMPANY_ID,value=\"+companyId+\"}\"); \n");
        ex.append("    param.add(\"#{ADD_TIME,value=NOW()}\"); \n");
        ex.append("    param.add(\"#{ADD_ID,value='\"+sessionUserId+\"'}\"); \n");
        ex.append("    param.add(\"#{ADD_NAME,value='\"+sessionUserName+\"'}\"); \n");

        ex.append("id=insertSimple(param);\n");
        ex.append("return id; \n");

        ex.append("}else{                 								\n");

        ex.append("    param=new UpdateParam();\n");
        ex.append("    param.table(\""+table+"\");               		\n");

        for (int i=0;i<fieldNameList.size();i++){
            StringBuilder pa=new StringBuilder();
            pa.append("#{");
            pa.append(fieldNameList.get(i));
            pa.append("}");
            ex.append("    param.add(\""+pa.toString()+"\");               		\n");
        }
       ;
        ex.append("   param.add(\"#{UPDATE_TIME,value=NOW()}\");\n");
        ex.append("   param.add(\"#{UPDATE_ID,value='\"+sessionUserId+\"'}\");\n");
        ex.append("   param.add(\"#{UPDATE_NAME,value='\"+sessionUserName+\"'}\");\n");
        ex.append("  StringBuilder updateSql=new StringBuilder();\n");
        ex.append("  updateSql.append(\" where "+idName+"=#{ID} \");\n");
        ex.append("  if(companyId>1){\n");
        ex.append("      updateSql.append(\" and COMPANY_ID= \"+companyId);\n");
        ex.append("  }\n");
        ex.append(" param.where(updateSql.toString());\n");
        ex.append(" updateSimple(param);\n");
        ex.append(" return ID;\n");

        ex.append("}");
        return ex.toString();
    }
}
