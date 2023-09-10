package com.ezadmin.biz.edit.controller;

import com.ezadmin.EzBootstrap;
import com.ezadmin.biz.base.controller.BaseController;
import com.ezadmin.biz.dao.FormDao;
import com.ezadmin.biz.dao.ListDao;
import com.ezadmin.biz.dao.PluginsDao;
import com.ezadmin.biz.edit.service.EditService;
import com.ezadmin.biz.form.service.FormService;
import com.ezadmin.biz.list.emmber.list.DefaultEzList;
import com.ezadmin.biz.list.emmber.list.EzList;
import com.ezadmin.biz.list.service.ListService;
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
import java.io.IOException;
import java.util.*;

/**
 * @title: ListEditController
 * @Author EzAdmin
 * @Date: 2022/3/9 13:03
 */
@EzMapping("/ezadmin/list/")
public class ListEditController extends BaseController {
    EzBootstrap bootstrap=EzBootstrap.instance();
    EditService editService = EzProxy.singleInstance(EditService.class);

    ListService listService = EzProxy.singleInstance(ListService.class);
    FormService formService = EzProxy.singleInstance(FormService.class);

    @EzMapping("listEdit.html")
    public String listEdit2(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String listId = Utils.trimNull(request.getAttribute("LIST_ID")) ;
        String ENCRYPT_LIST_ID =Utils.trimNull(request.getAttribute("ENCRYPT_LIST_ID")) ;
        Utils.addLog("start list html list_id="+listId);
//        if(!checkEditAuth()){
//         // notauth(false,request,response);
//            return  "403";
//        }

        Map<String, Object> requestParamMap =requestToMap(request);
        //session
        requestParamMap.put("perPageInt","1");
        requestParamMap.put("IS_DEBUG","1");
        Map<String, String> sessionParamMap = sessionToMap(request.getSession());
        requestParamMap.put("ContextPath", request.getContextPath());
        EzList list = new DefaultEzList( ENCRYPT_LIST_ID, bootstrap.getOriginDataSource(), requestParamMap,sessionParamMap);
        if (list.getEzListDto()==null) {
            if(Utils.getLog()!=null){
                Utils.addLog("cannot find list where list_id="+listId);
            }
          return  "404";

        }
        if(StringUtils.isNotBlank(listId)&&
                EzBootstrap.instance().getDataSourceByKey(list.getEzListDto().getDataSource())==null){

            // throw new IllegalStateException("数据库配置错误"+list.getEzListDto().getDataSource());
        }
        list.renderHtml();
        list.getEzListDto().setAppendHead(list.getEzListDto().getAppendHead()+ "<input type='hidden' id='LIST_ID' value='"+list.getEzListDto().getListId()+"'>");

        list.getEzListDto().setAppendFoot(list.getEzListDto().getAppendFoot()+
                "<script type='text/javascript'  src='"+request.getContextPath()+"/webjars/ezadmin/plugins/dragula/dragula.min.js'></script>"+
                        "<script src='"+request.getContextPath()+"/webjars/ezadmin/js/ez-list-edit-html.js?v="+System.currentTimeMillis()+"'></script>");

        request.setAttribute("data",list.getEzListDto());
        request.setAttribute("IS_DEBUG",1);


        request.setAttribute("_SEARCH_ITEM_DISPLAY",request.getParameter("_SEARCH_ITEM_DISPLAY"));
        request.setAttribute("_EZ_LIST_EMPTY_NAME",list.getEzListDto().getListEmptyName());
        request.setAttribute("listUrl",request.getContextPath()+"/ezadmin/list/list-"+ENCRYPT_LIST_ID);
        return "layui/list/list";
     }
    @EzMapping("loadEdit.html")
    public String loadList(HttpServletRequest request, HttpServletResponse response){
             List<Map<String, Object>> searchPlugins= PluginsDao.getInstance().allListPlugin("search");
            List<Map<String, Object>> tdPlugins= PluginsDao.getInstance().allListPlugin("td");
            request.setAttribute("searchPlugins",searchPlugins);
            request.setAttribute("tdPlugins",tdPlugins);
        HashMap list=new HashMap();
        list.put("core",new HashMap());
        list.put("search",new HashMap());
        list.put("tab",new HashMap());
        list.put("tablebtn",new HashMap());
        list.put("rowbtn",new HashMap());
        list.put("col",new HashMap());
            request.setAttribute("list",list);

            return "layui/pages/listedit";
    }
    @EzMapping("submitEdit.html")
    public EzResult edit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //所有表单类型的插件
        System.out.println(request.getParameter("data"));
        Map<String, Object> form=JSONUtils.parseObjectMap(request.getParameter("data"));

        ListDao.getInstance().updateList(form);

        return EzResult.instance();
    }

    @EzMapping("fastlist.html")
    public void fastlist(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String listId = request.getParameter("encodeId");
        String fasttext = request.getParameter("fasttext");
        String generateForm = request.getParameter("generateForm");
        sqlToList(fasttext,listId,generateForm);
        EzBootstrap.instance().getCache().clear();
        response.sendRedirect(request.getContextPath()+"/ezadmin/list/listEdit-"+listId);
    }

    public static void main(String[] args) throws  Exception {
        String sql="select a.AD_ID ID, AD_TITLE 标题,AD_PIC 图片,AD_STATUS 是否上架,AD_POSITION 位置,DISPLAY_ORDER 排序值,\n" +
                "         UPDATE_DATE 编辑时间  from V_APPLET_AD a where IS_DEL=0";
        ListEditController a=new ListEditController()   ;
        a.sqlToList(sql,"aaa","1");
    }

    public   void sqlToList(String sql, String listId, String generateForm) throws Exception {
        // String sql="select A.username as 用户名,password as 密码 ,from_unixtime(A.add_time/1000,'%y-%m')    from  T_USER A WHERE USER_ID=1 ";
        Statement statement = CCJSqlParserUtil.parse(sql);
        Select selectStatement = (Select) statement;

        PlainSelect plainSelect=(PlainSelect)selectStatement.getSelectBody();

        List<SelectItem> selectItemList= plainSelect.getSelectItems();

        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
        List<String> tableNameList = tablesNamesFinder.getTableList(selectStatement);

        Map<String,String> list=new HashMap<>();

        list.put("LIST_NAME",listId);
        list.put("DATASOURCE","dataSource");

        List<Map<String, String>> headList=new ArrayList<>();
        List<Map<String, String>> searchList=new ArrayList<>();
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
            Map<String,String> head=new HashMap<>();
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
            Map<String,String> search=new HashMap<>();
            search.put(JsoupUtil.LABEL,alias);
            search.put(JsoupUtil.ITEM_NAME,StringUtils.upperCase(colName));
            search.put(JsoupUtil.NAME,StringUtils.upperCase(colName));
          //  search.put("ITEM_SORT",i*10+"");



            if(colName.equalsIgnoreCase("COMPANY_ID")){
                search.put(JsoupUtil.PLUGIN,"select-search");
                search.put(JsoupUtil.DATA, "SELECT COMPANY_ID K,COMPANY_NAME V FROM T_SYS_COMPANY");
                search.put(JsoupUtil.DATATYPE, "KVSQLCACHE");
            }
            else  if(alias.contains("时间")){
                search.put(JsoupUtil.PLUGIN,"daterange");
                search.put(JsoupUtil.OPER, "between");
            }
            else{
                search.put(JsoupUtil.PLUGIN,"input-text");
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

        list.put("SELECT_EXPRESS", sqlExpress.toString());
       // list.put("DEFAULT_ORDER","ORDER BY ADD_TIME DESC");

        String id=  editService.insertListById(listId,list);
        for (int i = 0; i < headList.size(); i++) {
            try {
                headList.get(i).put("LIST_ID",id);
                JsoupConfigHolder.updateColumnByListId(listId,Utils.trimNull(headList.get(i).get(JsoupUtil.ITEM_NAME)),headList.get(i));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        for (int i = 0; i < searchList.size(); i++) {
            try {
                searchList.get(i).put("LIST_ID",id);
                JsoupConfigHolder.updateSearchByListId(listId,Utils.trimNull(searchList.get(i).get(JsoupUtil.ITEM_NAME)),searchList.get(i));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        //保存表单
        if(Utils.isNotEmpty(tableNameList)&& StringUtils.equals(generateForm,"1") ){
            pureAddForm( listId,tableNameList.get(0),fieldNameList,fieldLabelList);
            extracted("/ezadmin/form/form-"+listId,listId,"新增","PARENT","button-table");
            extracted("/ezadmin/form/form-"+listId,listId,"一段提示信息","PARENT","button-span");
            extractedRow("/ezadmin/form/form-"+listId+"?ID=${ID}",listId,"编辑","MODEL","layui-border-blue");
            extractedRow("/ezadmin/form/doDelete-"+listId+"?ID=${ID}",listId,"删除","CONFIRM_AJAX","layui-border-red");
         }
    }

    private void extracted(String url,String listId,String name,String openType,String plugin) throws Exception {

        List<Map<String, String>> tableBtnList=new ArrayList<>();
        Map<String,String> map=new HashMap<>();
        map.put(JsoupUtil.URL,url);
        map.put(JsoupUtil.OPENTYPE,openType);
        map.put(JsoupUtil.LABEL,name);
        map.put(JsoupUtil.ITEM_NAME,name);
        map.put(JsoupUtil.WINDOW_NAME,name);
        map.put(JsoupUtil.PLUGIN,plugin);
        tableBtnList.add(map);
        JsoupConfigHolder.updateTableButtonByListId(listId,name,map);
    }
    private void extractedRow(String url,String listId,String name,String openType,String CC) throws Exception {
        List<Map<String, String>> tableBtnList=new ArrayList<>();
        Map<String,String> map=new HashMap<>();
        map.put(JsoupUtil.URL,url);
        map.put(JsoupUtil.OPENTYPE,openType);
        map.put(JsoupUtil.LABEL,name);
        map.put(JsoupUtil.ITEM_NAME,name);
        map.put(JsoupUtil.WINDOW_NAME,name);
        map.put(JsoupUtil.PLUGIN,"button-single");
        map.put(JsoupUtil.CLASS,CC);
        tableBtnList.add(map);
        JsoupConfigHolder.updateRowButtonByListId(listId,name,map);
     }

    public void pureAddForm(String formId,String table, List<String> fieldNameList,List<String> fieldLabelList) throws  Exception {


            Map<String, String> form=new HashMap<>();
            form.put("datasource", "dataSource");
            form.put("form_name", formId);
            form.put("FORM_NAME", formId);
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
           // sqlExpress.append("\ncompanyId=$$(\"COMPANY_ID\");");
            sqlExpress.append("\nsql.append(\""+sql.toString()+"\");");
//            sqlExpress.append("\nif(companyId>1){");
//            sqlExpress.append("\n\tsql.append(\" AND COMPANY_ID=\"+companyId);");
//            sqlExpress.append("\n}");
            sqlExpress.append("\nreturn select(sql).get(0);");


            form.put(JsoupUtil.INIT_EXPRESS, sqlExpress.toString());

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
            form.put(JsoupUtil.SUBMIT_EXPRESS, submitEx);
            form.put(JsoupUtil.DELETE_EXPRESS, "\nupdate(\"UPDATE "+table+" set delete_flag=1 where "+idName+"=${ID}\");");

            form.put(JsoupUtil.SUCCESS_URL,"reload");
            JsoupConfigHolder.updateFormCoreByFormId(formId,form);

            for (int i = 0; i < fieldNameList.size(); i++) {
                Map<String,String> item=new HashMap<>();
                String name=fieldNameList.get(i).toUpperCase();
                item.put("item_label", fieldLabelList.get(i));
                item.put(JsoupUtil.LABEL, fieldLabelList.get(i));
                item.put(JsoupUtil.ITEM_NAME, name);
                item.put(JsoupUtil.NAME, name);
                if(fieldLabelList.get(i).equals("ID")||ignorField(name)  ){
                    continue;
                }
                if(fieldLabelList.get(i).endsWith("时间")){
                    item.put("plugin_code", "input-date");
                }else{
                    item.put("plugin_code", "input-text");
                }
                JsoupConfigHolder.updateFormItemByFormIdAndName(formId,name,item);
            }
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
