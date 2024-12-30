package top.ezadmin.controller;

import net.sf.jsqlparser.statement.select.*;

import top.ezadmin.EzClientBootstrap;
import top.ezadmin.common.NotExistException;
import top.ezadmin.common.constants.RequestParamConstants;
import top.ezadmin.common.utils.*;
import top.ezadmin.dao.FormDao;
import top.ezadmin.dao.ListDao;
import top.ezadmin.plugins.express.InsertSimpleOperator;
import top.ezadmin.plugins.express.OperatorParam;
import top.ezadmin.plugins.express.UpdateSimpleOperator;
import top.ezadmin.plugins.express.jdbc.InsertParam;
import top.ezadmin.plugins.express.jdbc.UpdateParam;
import top.ezadmin.service.ListService;
import top.ezadmin.common.annotation.EzMapping;
import top.ezadmin.plugins.sqlog.format.FormatStyle;
import top.ezadmin.web.EzResult;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.util.TablesNamesFinder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * : ListEditController
 * @author EzAdmin
 */
@EzMapping("/topezadmin/listEdit/")
public class ListEditController extends BaseController {

    ListService listService = EzProxy.singleInstance(ListService.class);
    @EzMapping("list.html")
    public String list(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String listUrlCode = Utils.trimNull(request.getAttribute("ENCRYPT_LIST_ID"));
        if(StringUtils.isBlank(listUrlCode)){
            throw new NotExistException();
        }
        Map<String, Object> requestParamMap = requestToMap(request);

        requestParamMap.putIfAbsent("perPageInt",request.getParameter("perPageInt"));
        requestParamMap.putIfAbsent("currentPage",request.getParameter("page"));

        requestParamMap.put("ContextPath", request.getContextPath());

        Map<String, String> sessionParamMap = sessionToMap(request.getSession());
        Map<String, Object> list = listService.selectConfigEditList(listUrlCode) ;
        if(StringUtils.isNotBlank(request.getParameter("version"))){
            list=listService.selectConfigHistoryList(listUrlCode,request.getParameter("version"));
        }
        if(!Utils.isNotEmpty(list)){
            throw new NotExistException();
        }

        request.setAttribute(RequestParamConstants._SEARCH_ITEM_DISPLAY, request.getParameter("_SEARCH_ITEM_DISPLAY"));

        request.setAttribute("listUrl", request.getContextPath() + "/topezadmin/listEdit/list-" + listUrlCode);
        request.setAttribute("_EZ_SERVER_NAME", "//" + request.getServerName() + ":" + request.getServerPort());
        request.setAttribute("cacheFlag",EzClientBootstrap.instance().isSqlCache());

        listService.fillListById(list, requestParamMap, sessionParamMap);
        request.setAttribute("data", list);
        Map<String, Object> coreMap = (Map<String, Object>) list.get("core");
        String pagetotaltype = ""+coreMap.getOrDefault("pagesync","0");
        String layout = ""+coreMap.getOrDefault("layout",EzClientBootstrap.instance().getLayout());

        request.setAttribute("layout",layout);

        if(StringUtils.equalsIgnoreCase(pagetotaltype,"1")){
            listService.fillCountById(list, requestParamMap, sessionParamMap);
        }
        String adminStyle=Utils.trimNullDefault(coreMap.get(JsoupUtil.ADMINSTYLE),EzClientBootstrap.instance().getAdminStyle());
        String template=Utils.trimNullDefault(coreMap.get(JsoupUtil.TEMPLATE),"list");
        return adminStyle + "/"+template;
    }

    @EzMapping("loadEdit.html")
    public String loadList( HttpServletRequest request, HttpServletResponse response) throws Exception {

        String ENCRYPT_LIST_ID = Utils.trimNull(request.getAttribute("ENCRYPT_LIST_ID"));
         Map<String, Object> requestParamMap =requestToMap(request);
        Map<String, String> sessionParamMap = sessionToMap(request.getSession());
        requestParamMap.put("loadDataFlag",0);
        Map<String, Object> list= listService.selectConfigEditList(ENCRYPT_LIST_ID) ;
        if(StringUtils.isNotBlank(request.getParameter("version"))){
            list=listService.selectConfigHistoryList(ENCRYPT_LIST_ID,request.getParameter("version"));
        }
        if(Utils.isEmpty(list)){
            list=new HashMap();
            list.put("core",new HashMap());
            list.put("search",new ArrayList<>());
            list.put("tab",new ArrayList<>());
            list.put("tablebtn",new ArrayList<>());
            list.put("rowbtn",new ArrayList<>());
            list.put("col",new ArrayList<>());
            Map<String, Object> coreMap = (Map<String, Object>) list.get("core");
            coreMap.put(JsoupUtil.ADMINSTYLE,"layui"); //编辑状态使用layui
            coreMap.put(JsoupUtil.TEMPLATE,"list");
            coreMap.put(JsoupUtil.APPEND_HEAD,"");
            coreMap.put(JsoupUtil.APPEND_FOOT,"");
        }else{
            Map<String, Object> coreMap = (Map<String, Object>) list.get("core");
            coreMap.put(JsoupUtil.ADMINSTYLE,"layui");
            coreMap.putIfAbsent(JsoupUtil.TEMPLATE,"list");
            coreMap.putIfAbsent(JsoupUtil.APPEND_HEAD,"");
            coreMap.putIfAbsent(JsoupUtil.APPEND_FOOT,"");
        }
        listService.fillListById(list,requestParamMap,sessionParamMap);
        request.setAttribute("data",list);
        request.setAttribute("vi",System.currentTimeMillis());
         request.setAttribute("ENCRYPT_LIST_ID",request.getParameter("ENCRYPT_LIST_ID"));
        return "layui/edit/listedit";
    }

    @EzMapping("submitEdit.html")
    public EzResult edit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> list= JSONUtils.parseObjectMap(request.getParameter("data"));
        String html= ListDao.getInstance().updateList(list);
        if(StringUtils.isNotBlank(html)){
            Map<String,Object> coreMap=(Map<String,Object>)list.get("core");
            String listcode= Utils.trimNull( coreMap.get("id"));
            String listname=  Utils.trimNull( coreMap.get("listname"));
            String DATASOURCE=  Utils.trimNull( coreMap.get(JsoupUtil.DATASOURCE));
            saveOrUpdate(listcode,listname,html,DATASOURCE);
            return EzResult.instance().data("EZ_CODE",listcode+"");
        }
        return EzResult.instance().fail();
    }


    @EzMapping("publish.html")
    public EzResult publish(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String ENCRYPT_LIST_ID = Utils.trimNull(request.getAttribute("ENCRYPT_LIST_ID"));
        Map<String,Object>  c=JSONUtils.parseObjectMap(listService.selectDbPublishListById(ENCRYPT_LIST_ID));
        //生成如果有数据 //1.把生产的复制到历史表
        if(Utils.isNotEmpty(c)){
            String config=c.get("EZ_CONFIG")+"";
            String DATASOURCE=c.get("DATASOURCE")+"";
            String name=c.get("EZ_NAME")+"";
            String code=ENCRYPT_LIST_ID;
            Map<String, Object> requestParamMap = requestToMap(request);
            requestParamMap.put("EZ_CONFIG",config);
            requestParamMap.put("DATASOURCE",DATASOURCE);
            requestParamMap.put("EZ_NAME",name);
            requestParamMap.put("EZ_CODE",code);
            requestParamMap.put("EZ_TYPE",1);
            OperatorParam op=new OperatorParam();
            op.setParams(requestParamMap);
            op.setDs(EzClientBootstrap.instance().getEzDataSource());
            Utils.addParam(op);
            InsertSimpleOperator o=new InsertSimpleOperator();
            InsertParam param=new InsertParam();
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
        Map<String,Object> edit=listService.selectConfigEditList(ENCRYPT_LIST_ID);
        String config=edit.get("EZ_CONFIG")+"";
        String DATASOURCE=edit.get("DATASOURCE")+"";
        String name=edit.get("EZ_NAME")+"";
        String code=ENCRYPT_LIST_ID;
        if(Utils.isEmpty(c)){
            Map<String, Object> requestParamMap = requestToMap(request);
            requestParamMap.put("EZ_CONFIG",config);
            requestParamMap.put("DATASOURCE",DATASOURCE);
            requestParamMap.put("EZ_NAME",name);
            requestParamMap.put("EZ_CODE",code);
            requestParamMap.put("EZ_TYPE",1);
            OperatorParam op=new OperatorParam();
            op.setParams(requestParamMap);
            op.setDs(EzClientBootstrap.instance().getEzDataSource());
            Utils.addParam(op);
            InsertSimpleOperator o=new InsertSimpleOperator();
            InsertParam param=new InsertParam();
            param.table("T_EZADMIN_PUBLISH");
            param.add("#{EZ_CODE}");
            param.add("#{DATASOURCE}");
            param.add("#{EZ_NAME}");
            param.add("#{EZ_TYPE,value=1}");
            param.add("#{EZ_CONFIG}");
            param.add("#{ADD_TIME,value=NOW()}");
            param.add("#{IS_DEL,value=0}");
            o.executeInner(new Object[]{param});
        }else{
            Map<String, Object> requestParamMap = requestToMap(request);
            requestParamMap.put("EZ_CONFIG",config);
            requestParamMap.put("DATASOURCE",DATASOURCE);
            requestParamMap.put("EZ_NAME",name);
            requestParamMap.put("EZ_CODE",code);
            requestParamMap.put("EZ_TYPE",1);
            OperatorParam op=new OperatorParam();
            op.setParams(requestParamMap);
            op.setDs(EzClientBootstrap.instance().getEzDataSource());
            Utils.addParam(op);
            UpdateSimpleOperator o=new UpdateSimpleOperator();
            UpdateParam param=new UpdateParam();
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
        EzClientBootstrap.instance().getEzCache().clear();
        request.setAttribute("EZ_TYPE",request.getParameter("EZ_TYPE"));
        return  EzResult.instance();
    }

    private String saveOrUpdate(String code,String name,String html,String datasource) throws Exception {
        Map<String, Object> requestParamMap = new HashMap<>();
        requestParamMap.put("EZ_CONFIG",html);
        requestParamMap.put("DATASOURCE",datasource);
        requestParamMap.put("EZ_NAME",name);
        requestParamMap.put("EZ_CODE",code);
        requestParamMap.put("EZ_TYPE",1);
        Map<String,Object> c=listService.selectConfigEditList(code);
        OperatorParam op=new OperatorParam();
        op.setParams(requestParamMap);
        op.setDs(EzClientBootstrap.instance().getEzDataSource());
        Utils.addParam(op);
        if(Utils.isEmpty(c)){
            InsertSimpleOperator o=new InsertSimpleOperator();
            InsertParam param=new InsertParam();
            param.table("T_EZADMIN_EDIT");
            param.add("#{EZ_CODE}");
            param.add("#{DATASOURCE}");
            param.add("#{EZ_NAME}");
            param.add("#{EZ_TYPE,value=1}");
            param.add("#{EZ_CONFIG}");
            param.add("#{ADD_TIME,value=NOW()}");
            param.add("#{IS_DEL,value=0}");
            o.executeInner(new Object[]{param});
        }else{
            UpdateSimpleOperator o=new UpdateSimpleOperator();
            UpdateParam param=new UpdateParam();
            param.table("T_EZADMIN_EDIT");
            param.add("#{EZ_CODE}");
            param.add("#{DATASOURCE}");
            param.add("#{EZ_NAME}");
            param.add("#{EZ_CONFIG}");
            param.add("#{UPDATE_TIME,value=NOW()}");
            param.where(" where EZ_CODE=#{EZ_CODE} AND EZ_TYPE=1");
            o.executeInner(new Object[]{param});
        }
        return code;
    }
    @EzMapping("submitSourceEdit.html")
    public EzResult submitExportEdit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String ezType=request.getParameter("EZ_TYPE");
        String data=request.getParameter("data");
        if(StringUtils.isNotBlank(data)){
            Map<String, Object> list=ListDao.getInstance().selectAllListByHtml(data);
            String html= ListDao.getInstance().updateList(list);
            Map<String,Object> coreMap=(Map<String,Object>)list.get("core");
            String listcode= Utils.trimNull( coreMap.get("id"));
            String listname=  Utils.trimNull( coreMap.get("listname"));
            String DATASOURCE=  Utils.trimNull( coreMap.get(JsoupUtil.DATASOURCE));
            saveOrUpdate(listcode,listname,html,DATASOURCE);
            return EzResult.instance().data("EZ_CODE",listcode+"");
        }
        return EzResult.instance().fail();
    }

    @EzMapping("sourceEdit.html")
    public String export(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String ENCRYPT_LIST_ID = Utils.trimNull(request.getAttribute("ENCRYPT_LIST_ID"));
        Map<String,Object>  c=listService.selectConfigEditList(ENCRYPT_LIST_ID);
        if(StringUtils.isNotBlank(request.getParameter("version"))){
            c=listService.selectConfigHistoryList(ENCRYPT_LIST_ID,request.getParameter("version"));
        }
        request.setAttribute("EZ_CONFIG",c.get("EZ_CONFIG")+"");
        request.setAttribute("ENCRYPT_LIST_ID",ENCRYPT_LIST_ID);
        request.setAttribute("EZ_TYPE",request.getParameter("EZ_TYPE"));
        requestToMap(request).forEach((k,v)->{
            request.setAttribute(k,v);
        });
        return  "layui/edit/export";
    }
    @EzMapping("trace.html")
    public void trace(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String listId = Utils.trimNull(request.getAttribute("LIST_ID"));
        String ENCRYPT_LIST_ID = Utils.trimNull(request.getAttribute("ENCRYPT_LIST_ID"));
        long start = System.currentTimeMillis();
        if (Utils.getLog() != null) {
            Utils.addLog("start ID=" + ENCRYPT_LIST_ID);
        }

        Map<String, Object> requestParamMap = requestToMap(request);


        Map<String, String> sessionParamMap = sessionToMap(request.getSession());

        if (Utils.getLog() != null) {
            Utils.addLog("requestParamMap " + requestParamMap);
        }
        if (Utils.getLog() != null) {
            Utils.addLog("sessionParamMap " + JSONUtils.toJSONString(sessionParamMap));
        }

        Map<String, Object> list =  listService.selectConfigEditList(ENCRYPT_LIST_ID);
        if(Utils.isEmpty(list)){
            throw new NotExistException();
        }
        try {
            listService.fillCountById(list, requestParamMap, sessionParamMap);
        }catch (Exception e){
            if (Utils.getLog() != null) {
                Utils.addLog("加载总数异常",e);
            }
        }
        try{
            listService.fillListById(list, requestParamMap, sessionParamMap);
        }catch (Exception e){
            if (Utils.getLog() != null) {
                Utils.addLog("查询数据异常",e);
            }
        }
        try{
            listService.fillTreeById(list, requestParamMap, sessionParamMap);
        }catch (Exception e){
            if (Utils.getLog() != null) {
                Utils.addLog("查询树数据异常",e);
            }
        }
        request.setAttribute("data", list);
        request.setAttribute("_SEARCH_ITEM_DISPLAY", request.getParameter("_SEARCH_ITEM_DISPLAY"));

        request.setAttribute("listUrl", request.getContextPath() + "/topezadmin/listEdit/list-" + ENCRYPT_LIST_ID);
        if (Utils.getLog() != null) {
            Utils.addLog("end list_id=" + listId + ",总共耗时：" + (System.currentTimeMillis() - start) + "ms");
        }
    }



    @EzMapping("importSql.html")
    public String importSql( HttpServletRequest request, HttpServletResponse response) throws Exception {
        return "layui/edit/import";
    }
    @EzMapping("importlist.html")
    public EzResult importlist(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String listcode = request.getParameter("listcode");
            String formcode = request.getParameter("formcode");
            String fasttext = request.getParameter("listexpress");
            String datasource = request.getParameter("datasource");

            EzResult result= sqlToList2(fasttext, listcode, formcode,datasource);
            EzClientBootstrap.instance().getCache().clear();
            return result;
        }
        catch (Exception e){
            logger.error("e",e);
            return EzResult.instance().fail().message("SQL错误");
        }
    }

    private   EzResult sqlToList2(String sql, String listId, String formcode,String datasource) throws Exception {
        // String sql="select A.username as 用户名,password as 密码 ,from_unixtime(A.add_time/1000,'%y-%m')    from  T_USER A WHERE USER_ID=1 ";
        Statement statement = CCJSqlParserUtil.parse(sql);
        Select selectStatement = (Select) statement;

        PlainSelect plainSelect=(PlainSelect)selectStatement.getSelectBody();

        List<SelectItem> selectItemList= plainSelect.getSelectItems();
        if(selectItemList.size()==1&&selectItemList.get(0) instanceof AllColumns){
            return EzResult.instance().fail().message("请指定字段名及字段中文别名");
        }

        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
        List<String> tableNameList = tablesNamesFinder.getTableList(selectStatement);

        Map<String,Object> core=new HashMap<>();

        core.put("id",listId);
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
            head.put(JsoupUtil.HEAD_PLUGIN_CODE, "th");
            if("ID".equalsIgnoreCase(alias)) {
                head.put(JsoupUtil.ITEM_NAME,"ID");

                head.put(JsoupUtil.HEAD_PLUGIN_CODE,"th-numbers");
                head.put(JsoupUtil.BODY_PLUGIN_CODE,"th-numbers");
                head.put("fixed","left");
                head.put(JsoupUtil.WIDTH,"60");

            }else{
                head.put(JsoupUtil.ITEM_NAME, StringUtils.upperCase(colName));
            }

            if(alias.contains("是否")){
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
           // search.put(JsoupUtil.NAME,StringUtils.upperCase(colName));
            //  search.put("ITEM_SORT",i*10+"");



            if(alias.contains("时间")){
                search.put(JsoupUtil.TYPE,"daterange");
                search.put(JsoupUtil.OPER, "between");
            }
            else{
                search.put(JsoupUtil.TYPE,"input-text");
            }
            search.put(JsoupUtil.ALIAS,tableName);
            searchList.add(search);
        }
        StringBuilder sqlExpress=new StringBuilder();
        sqlExpress.append("\r\nStringBuilder sql=new StringBuilder();");
        sqlExpress.append("\r\nsql.append(\""+ FormatStyle.BASIC.getFormatter().format(selectStatement.toString())+"\");");
        sqlExpress.append("\r\nreturn search(sql);");
        core.put("select_express","\n<![CDATA[ \n"+ sqlExpress.toString() +"\n]]>\n");
        if(StringUtils.isNotBlank(formcode)){
            tableBtnList.add(extracted2("/topezadmin/form/form-"+formcode, "新增","PARENT","button-table"));
            rowBtnList.add( extractedRow2("/topezadmin/form/form-"+formcode+"?ID=${ID}", "编辑","MODEL","layui-border-blue"));
            rowBtnList.add(extractedRow2("/topezadmin/form/doDelete-"+formcode+"?ID=${ID}", "删除","CONFIRM_AJAX","layui-border-red"));
            Map<String,Object> form= pureAddForm2( formcode,tableNameList.get(0),fieldNameList,fieldLabelList);
            String html= FormDao.getInstance().updateForm(form);
            if(StringUtils.isNotBlank(html)){
                Map<String,Object> coreMap=(Map<String,Object>)form.get("core");
                String formCode= Utils.trimNull( coreMap.get("formcode"));
                String formName=  Utils.trimNull( coreMap.get(JsoupUtil.FORM_NAME.toLowerCase()));
                String datasourceO=Utils.trimNullDefault( coreMap.get(JsoupUtil.DATASOURCE),datasource);
                new FormEditController().saveOrUpdate(formCode,formName,html,datasourceO);
            }
            if(StringUtils.isBlank(listId)){
                return EzResult.instance();
            }
        }
        Map<String,Object> list=new HashMap();
        list.put("core",core);
        list.put("search",searchList);
        list.put("tablebtn",tableBtnList);
        list.put("rowbtn",rowBtnList);
        list.put("col",headList);
        String listhtml= ListDao.getInstance().updateList(list);
        if(StringUtils.isNotBlank(listhtml)){
            Map<String,Object> coreMap=(Map<String,Object>)list.get("core");
            String listcode= Utils.trimNull( coreMap.get("id"));
            String listname=  Utils.trimNull( coreMap.get("listname"));
            String DATASOURCE=  Utils.trimNull( coreMap.get(JsoupUtil.DATASOURCE));
            saveOrUpdate(listcode,listname,listhtml,DATASOURCE);
        }
        return EzResult.instance();
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
        sqlExpress.append("if(isBlank(\"ID\")){\n return new HashMap();\n}                 		\n");

        sqlExpress.append("StringBuilder sql=new StringBuilder();");
        sqlExpress.append("\nsql.append(\""+sql.toString()+"\");");
        sqlExpress.append("\nreturn selectOne(sql);");


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

        form.put(JsoupUtil.DELETE_EXPRESS, "" +
                "companyId=$$(\"COMPANY_ID\");                  		\n" +
                "\nupdate(\"UPDATE "+table+" set delete_flag=1,UPDATE_TIME=NOW(),UPDATE_ID=\"+sessionUserId+\" where "+idName+"=${ID} and COMPANY_ID=\"+companyId);");

        form.put(JsoupUtil.STATUS_EXPRESS,"reload");

        List<Map<String,Object>> cardsList=new ArrayList<>();
        Map<String,Object> card=new HashMap<>();
        cardsList.add(card);
        List<Map<String,Object>> items=new ArrayList<>();
        card.put("items",items);

        for (int i = 0; i < fieldNameList.size(); i++) {
            Map<String,Object> item=new HashMap<>();
            String name=fieldNameList.get(i).toUpperCase();

            item.put(JsoupUtil.LABEL, fieldLabelList.get(i));
            item.put(JsoupUtil.ITEM_NAME, name);
           // item.put(JsoupUtil.NAME, name);
            if(fieldLabelList.get(i).equals("ID")||ignorField(name)  ){
                continue;
            }
            if(fieldLabelList.get(i).endsWith("时间")){
                item.put(JsoupUtil.TYPE, "input-date");
            }else{
                item.put(JsoupUtil.TYPE, "input-text");
            }
            items.add(item);
        }
        result.put("cards",cardsList);
        return result;
    }


    private boolean ignorField(String name){
        return name.equalsIgnoreCase("ADD_TIME")
                || name.equalsIgnoreCase("UPDATE_TIME")
                || name.equalsIgnoreCase("COMPANY_ID")
                || name.equalsIgnoreCase("ADD_ID")
                || name.equalsIgnoreCase("ADD_NAME");
    }

    private String generateFormExpress(String table,String idName,List<String> fieldNameList){
        StringBuilder ex=new StringBuilder();
        ex.append("\nimport top.ezadmin.plugins.express.jdbc.UpdateParam;\n");
        ex.append("import top.ezadmin.plugins.express.jdbc.InsertParam;                  		\n");
        ex.append("import top.ezadmin.web.EzResult;                  		\n");

        ex.append("ID=$(\"ID\");                  		\n");
        ex.append("companyId=$$(\"COMPANY_ID\");                  		\n");
        ex.append("sessionUserId=$$(\"EZ_SESSION_USER_ID_KEY\");                  		\n");
        ex.append("sessionUserName=$$(\"EZ_SESSION_USER_NAME_KEY\");                  		\n");

        ex.append("if(!isNotBlank(\"ID\")){                  		\n");
        ex.append("    param=new InsertParam();                		\n");
        ex.append("    param.table(\""+table+"\");               		\n");

        for (int i=0;i<fieldNameList.size();i++){
            if(fieldNameList.get(i).equalsIgnoreCase("ID")){
                continue;
            }
            if(fieldNameList.get(i).equalsIgnoreCase("ADD_TIME")){
                StringBuilder pa=new StringBuilder();
                pa.append("#{");
                pa.append(fieldNameList.get(i));
                pa.append("}");
                ex.append("   param.add(\"#{ADD_TIME,value=NOW()}\");               		\n");
                continue;
            }
            if(fieldNameList.get(i).equalsIgnoreCase("ADD_ID")){
                StringBuilder pa=new StringBuilder();
                pa.append("#{");
                pa.append(fieldNameList.get(i));
                pa.append("}");
                ex.append("  param.add(\"#{ADD_ID,value='\"+sessionUserId+\"'}\");            		\n");
                continue;
            }
            if(fieldNameList.get(i).equalsIgnoreCase("UPDATE_TIME")){
                StringBuilder pa=new StringBuilder();
                pa.append("#{");
                pa.append(fieldNameList.get(i));
                pa.append("}");
                ex.append("   param.add(\"#{UPDATE_TIME,value=NOW()}\");               		\n");
                continue;
            }
            if(fieldNameList.get(i).equalsIgnoreCase("UPDATE_ID")){
                StringBuilder pa=new StringBuilder();
                pa.append("#{");
                pa.append(fieldNameList.get(i));
                pa.append("}");
                ex.append("  param.add(\"#{UPDATE_ID,value='\"+sessionUserId+\"'}\");            		\n");
                continue;
            }
            if(fieldNameList.get(i).equalsIgnoreCase("DELETE_FLAG")){
                StringBuilder pa=new StringBuilder();
                pa.append("#{");
                pa.append(fieldNameList.get(i));
                pa.append("}");
                ex.append("  param.add(\"#{DELETE_FLAG,value=0}\");            		\n");
                continue;
            }
            if(fieldNameList.get(i).equalsIgnoreCase("STATUS")){
                StringBuilder pa=new StringBuilder();
                pa.append("#{");
                pa.append(fieldNameList.get(i));
                pa.append("}");
                ex.append("  param.add(\"#{STATUS,value=1}\");            		\n");
                continue;
            }
            if(fieldNameList.get(i).equalsIgnoreCase("COMPANY_ID")){
                StringBuilder pa=new StringBuilder();
                pa.append("#{");
                pa.append(fieldNameList.get(i));
                pa.append("}");
                ex.append("     param.add(\"#{COMPANY_ID,value=\"+companyId+\"}\");\n          		\n");
                continue;
            }
            StringBuilder pa=new StringBuilder();
            pa.append("#{");
            pa.append(fieldNameList.get(i));
            pa.append("}");
            ex.append("    param.add(\""+pa.toString()+"\");               		\n");
        }


        ex.append("id=insertSimple(param);\n");
        ex.append("return id; \n");

        ex.append("}else{                 								\n");

        ex.append("    param=new UpdateParam();\n");
        ex.append("    param.table(\""+table+"\");               		\n");

        for (int i=0;i<fieldNameList.size();i++){
            if(fieldNameList.get(i).equalsIgnoreCase("ID")
                    ||fieldNameList.get(i).equalsIgnoreCase("ADD_TIME")
                    ||fieldNameList.get(i).equalsIgnoreCase("ADD_ID")
            ){
                continue;
            }
            if(fieldNameList.get(i).equalsIgnoreCase("UPDATE_TIME")){
                StringBuilder pa=new StringBuilder();
                pa.append("#{");
                pa.append(fieldNameList.get(i));
                pa.append("}");
                ex.append("   param.add(\"#{UPDATE_TIME,value=NOW()}\");               		\n");
                continue;
            }
            if(fieldNameList.get(i).equalsIgnoreCase("UPDATE_ID")){
                StringBuilder pa=new StringBuilder();
                pa.append("#{");
                pa.append(fieldNameList.get(i));
                pa.append("}");
                ex.append("  param.add(\"#{UPDATE_ID,value='\"+sessionUserId+\"'}\");            		\n");
                continue;
            }



            StringBuilder pa=new StringBuilder();
            pa.append("#{");
            pa.append(fieldNameList.get(i));
            pa.append("}");
            ex.append("    param.add(\""+pa.toString()+"\");               		\n");
        }


        ex.append("  StringBuilder updateSql=new StringBuilder();\n");
        ex.append("  updateSql.append(\" where "+idName+"=#{ID} \");\n");
        ex.append("   updateSql.append(\" and COMPANY_ID= \"+companyId);\n");

        ex.append(" param.where(updateSql.toString());\n");
        ex.append(" updateSimple(param);\n");
        ex.append(" return ID;\n");

        ex.append("}");
        return ex.toString();
    }

}
