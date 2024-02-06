package top.ezadmin.controller;


import com.ezcloud.EzClientBootstrap;
import com.ezcloud.common.utils.*;
import com.ezcloud.controller.BaseController;
import com.ezcloud.dao.FormDao;
import com.ezcloud.dao.ListDao;
import com.ezcloud.plugins.sqlog.format.FormatStyle;
import com.ezcloud.service.ListService;
import com.ezcloud.web.EzResult;

import top.ezadmin.domain.mapper.EzCloudMapper;
import top.ezadmin.domain.model.EzCloud;
import top.ezadmin.domain.model.EzCloudExample;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.*;

@Controller
@RequestMapping("/ezlist/")
public class EzListEditController extends BaseController {
    ListService listService = EzProxy.singleInstance(ListService.class);

    @Resource
    EzCloudMapper ezCloudMapper;

    @RequestMapping("loadEdit.html")
    public String loadList(Model model, Long cloudId,String ENCRYPT_LIST_ID, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> requestParamMap =requestToMap(request);
        Map<String, String> sessionParamMap = sessionToMap(request.getSession());
        requestParamMap.put("loadDataFlag",0);
        Map<String, Object> list=new HashMap<>();
        if(cloudId!=null){
            EzCloud ezCore=ezCloudMapper.selectByPrimaryKey(cloudId);
            if(ezCore!=null){
                list= listService.selectAllListByHtml(ezCore.getEzConfig())  ;
            }
        }else if(StringUtils.isNotBlank(ENCRYPT_LIST_ID)){
            EzCloudExample example=new EzCloudExample();
            example.createCriteria().andEzCodeEqualTo(ENCRYPT_LIST_ID).andEzTypeEqualTo(1).andIsDelEqualTo(0);
            example.setOrderByClause("  ID desc");
            List<EzCloud>  listDbList=ezCloudMapper.selectByExampleWithBLOBs(example);
           if(!CollectionUtils.isEmpty(listDbList)){
               list= listService.selectAllListByHtml(listDbList.get(0).getEzConfig())  ;
               cloudId=listDbList.get(0).getId();
           }
        }

        if(!Utils.isNotEmpty(list)){
            list=new HashMap();
            list.put("core",new HashMap());
            list.put("search",new ArrayList<>());
            list.put("tab",new ArrayList<>());
            list.put("tablebtn",new ArrayList<>());
            list.put("rowbtn",new ArrayList<>());
            list.put("col",new ArrayList<>());
            Map<String, Object> coreMap = (Map<String, Object>) list.get("core");
            coreMap.put(JsoupUtil.ADMINSTYLE,"layui");
            coreMap.put(JsoupUtil.APPEND_HEAD,"");
            coreMap.put(JsoupUtil.APPEND_FOOT,"");
        }else{
            Map<String, Object> coreMap = (Map<String, Object>) list.get("core");
            coreMap.put(JsoupUtil.ADMINSTYLE,"layui");
            coreMap.putIfAbsent(JsoupUtil.APPEND_HEAD,"");
            coreMap.putIfAbsent(JsoupUtil.APPEND_FOOT,"");
            listService.fillListById(list,requestParamMap,sessionParamMap);
        }
        model.addAttribute("data",list);
        model.addAttribute("vi",System.currentTimeMillis());
        model.addAttribute("cloudId",cloudId);
        model.addAttribute("ENCRYPT_LIST_ID",request.getParameter("ENCRYPT_LIST_ID"));
        return "layui/listedit";
    }
    @RequestMapping("export.html")
    public String export(Model model, Long cloudId,String ezType, HttpServletRequest request, HttpServletResponse response) throws Exception {
        EzCloud ezCore=ezCloudMapper.selectByPrimaryKey(cloudId);
        model.addAttribute("code",ezCore.getEzConfig());
        model.addAttribute("ezCode",ezCore.getEzCode());
        model.addAttribute("cloudId",ezCore.getId());
        model.addAttribute("ezType",StringUtils.isBlank(ezType)?"1":ezType);
        return "export";
    }

    @PostMapping("submitExportEdit.html")
    @ResponseBody
    public EzResult submitExportEdit(Long cloudId,String ezType,String data,HttpServletRequest request, HttpServletResponse response) throws Exception {
        //所有表单类型的插件
        if(StringUtils.equals(ezType,"1")){
            if(StringUtils.isNotBlank(data)){
                Map<String, Object> list=ListDao.getInstance().selectAllListByHtml(data);
                String html= ListDao.getInstance().updateList(list);
                Map<String,Object> coreMap=(Map<String,Object>)list.get("core");
                String listcode= Utils.trimNull( coreMap.get("listcode"));
                String listname=  Utils.trimNull( coreMap.get("listname"));
                String DATASOURCE=  Utils.trimNull( coreMap.get(JsoupUtil.DATASOURCE));
                EzCloud record=new EzCloud();
                record.setEzCode(listcode);
                record.setUpdateTime(new Date());
                record.setEzName(listname);
                record.setEzConfig(html);
                record.setDatasource(DATASOURCE);
                record.setUpdateName(IpUtils.getRealIp(request));
                if(cloudId!=null){
                    record.setId(cloudId);
                    ezCloudMapper.updateByPrimaryKeySelective(record);
                }else{
                    record.setIsDel(0);
                    record.setEzType(1);
                    record.setAddTime(new Date());
                    ezCloudMapper.insert(record);
                    cloudId=record.getId();
                }
                return EzResult.instance().data("cloudId",cloudId+"");
            }
        }else if(StringUtils.equals(ezType,"2")){
            if(StringUtils.isNotBlank(data)){
                Map<String, Object> list=  FormDao.getInstance().selectAllFormByHtml(data);
                String html=FormDao.getInstance().updateForm(list);
                Map<String,Object> coreMap=(Map<String,Object>)list.get("core");
                String formcode= Utils.trimNull( coreMap.get("formcode"));
                String FORM_NAME=  Utils.trimNull( coreMap.get(JsoupUtil.FORM_NAME));
                String DATASOURCE=  Utils.trimNull( coreMap.get(JsoupUtil.DATASOURCE));
                EzCloud record=new EzCloud();
                record.setEzCode(formcode);
                record.setUpdateTime(new Date());
                record.setEzName(FORM_NAME);
                record.setEzConfig(html);
                record.setDatasource(DATASOURCE);
                if(cloudId!=null){
                    record.setId(cloudId);
                    ezCloudMapper.updateByPrimaryKeySelective(record);
                }else{
                    record.setIsDel(0);
                    record.setEzType(2);
                    record.setAddTime(new Date());
                    ezCloudMapper.insert(record);
                    cloudId=record.getId();
                }
                return EzResult.instance().data("cloudId",cloudId+"");
            }
        }
        return EzResult.instance().fail();
    }

    @PostMapping("submitEdit.html")
    @ResponseBody
    public EzResult edit(Long cloudId,String appName,HttpServletRequest request, HttpServletResponse response) throws Exception {
        //所有表单类型的插件
        Map<String, Object> list=JSONUtils.parseObjectMap(request.getParameter("data"));
        String html= ListDao.getInstance().updateList(list);
        if(StringUtils.isNotBlank(html)){
            Map<String,Object> coreMap=(Map<String,Object>)list.get("core");
            String listcode= Utils.trimNull( coreMap.get("listcode"));
            String listname=  Utils.trimNull( coreMap.get("listname"));
            String DATASOURCE=  Utils.trimNull( coreMap.get(JsoupUtil.DATASOURCE));
            EzCloud record=new EzCloud();
            record.setEzCode(listcode);
            record.setUpdateTime(new Date());
            record.setEzName(listname);
            record.setEzConfig(html);
            record.setDatasource(DATASOURCE);
            record.setUpdateName(IpUtils.getRealIp(request));
            if(cloudId!=null){
                record.setId(cloudId);
                ezCloudMapper.updateByPrimaryKeySelective(record);
            }else{
                record.setIsDel(0);
                record.setEzType(1);
                record.setAppName(appName);
                record.setAddTime(new Date());
             ezCloudMapper.insert(record);
                cloudId=record.getId();
            }
            return EzResult.instance().data("cloudId",cloudId+"");
        }
        return EzResult.instance().fail();
    }
    @RequestMapping("importSql.html")
    public String importSql(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return "import";
    }
    @PostMapping("importlist.html")
    @ResponseBody
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
            return EzResult.instance().fail().message("SQL错误");
        }
    }


//    @RequestMapping("/format.html")
//    @ResponseBody
//    public EzResult format(String source)  {
//        try {
//
//            return EzResult.instance().data("code",ff(source));
//        } catch (FormatterException e) {
//            e.printStackTrace();
//            return EzResult.instance().fail().data("code",source);
//        }
//    }

//    public static String ff(String source) throws FormatterException {
//        String pre="public class t{ public static void main(String[] args){/**start**/";
//        String end="/**end**/}}";
//        StringBuilder impos=new StringBuilder();
//        StringBuilder body=new StringBuilder();
//        String[] sourceLines=source.split("\n");
//        for (int i = 0; i < sourceLines.length; i++) {
//            if(sourceLines[i].trim().startsWith("import")){
//                impos.append(sourceLines[i]+"\n");
//            }else if(sourceLines[i].trim().contains("//")){
//                body.append(sourceLines[i]+"\n");
//            }else{
//                body.append(sourceLines[i]);
//            }
//        }
//        System.out.println(pre+body.toString()+end);
//        String formattedSource = new com.google.googlejavaformat.java.Formatter().formatSource(pre+body.toString()+end);
//        formattedSource= formattedSource.substring(formattedSource.indexOf("/** start* */")+"/** start* */".length(),formattedSource.indexOf("/** end* */"));
//        return impos+formattedSource;
//    }
//    public static void main(String[] args) throws JSQLParserException, FormatterException {
//        String body="import java.utils.*;\n" +
//                "//这是一个测试环境\n" +
//                "StringBuilder sql=\n" +
//                "new \n" +
//                "StringBuilder();\n" +
//                " sql.append(\"\n" +
//                "    SELECT\n" +
//                "        ID ID,\n" +
//                "        NAME \n" +
//                "    FROM\n" +
//                "        T_USER \n" +
//                "    WHERE\n" +
//                "        1 = 1\");" +
//                "\n" ;
//        System.out.println(ff(body));
//    }
    public   EzResult sqlToList2(String sql, String listId, String formcode,String datasource) throws Exception {
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
            search.put(JsoupUtil.NAME,StringUtils.upperCase(colName));
            //  search.put("ITEM_SORT",i*10+"");



            if(alias.contains("时间")){
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
        sqlExpress.append("\r\nsql.append(\""+ FormatStyle.BASIC.getFormatter().format(selectStatement.toString())+"\");");
        sqlExpress.append("\r\nreturn search(sql);");
        core.put("select_express","\n<![CDATA[ \n"+ sqlExpress.toString() +"\n]]>\n");
        if(StringUtils.isNotBlank(formcode)){
            tableBtnList.add(extracted2("/ezcloud/form/form-"+formcode, "新增","PARENT","button-table"));
            rowBtnList.add( extractedRow2("/ezcloud/form/form-"+formcode+"?ID=${ID}", "编辑","MODEL","layui-border-blue"));
            rowBtnList.add(extractedRow2("/ezcloud/form/doDelete-"+formcode+"?ID=${ID}", "删除","CONFIRM_AJAX","layui-border-red"));
            Map<String,Object> form= pureAddForm2( formcode,tableNameList.get(0),fieldNameList,fieldLabelList);
           String html= FormDao.getInstance().updateForm(form);
            if(StringUtils.isNotBlank(html)){
                Map<String,Object> coreMap=(Map<String,Object>)form.get("core");
                String formCode= Utils.trimNull( coreMap.get("formcode"));
                String formName=  Utils.trimNull( coreMap.get(JsoupUtil.FORM_NAME.toLowerCase()));
                String datasourceO=Utils.trimNullDefault( coreMap.get(JsoupUtil.DATASOURCE),datasource);
                EzCloud record=new EzCloud();
                record.setEzCode(formCode);
                record.setUpdateTime(new Date());
                record.setEzName(formName);
                record.setEzConfig(html);
                record.setDatasource(datasourceO);
                record.setIsDel(0);
                record.setEzType(2);
                record.setAddTime(new Date());
                ezCloudMapper.insert(record);
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
            String listcode= Utils.trimNull( coreMap.get("listcode"));
            String listname=  Utils.trimNull( coreMap.get("listname"));
            String DATASOURCE=  Utils.trimNull( coreMap.get(JsoupUtil.DATASOURCE));
            EzCloud record=new EzCloud();
            record.setEzCode(listcode);
            record.setUpdateTime(new Date());
            record.setEzName(listname);
            record.setEzConfig(listhtml);
            record.setDatasource(DATASOURCE);
            record.setIsDel(0);
            record.setEzType(1);
            record.setAddTime(new Date());
            ezCloudMapper.insert(record);
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

        sqlExpress.append("StringBuilder sql=new StringBuilder();");
        sqlExpress.append("\nsql.append(\""+sql.toString()+"\");");
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

        form.put(JsoupUtil.STATUS_EXPRESS,"reload");

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
        ex.append("\nimport com.ezcloud.plugins.express.jdbc.UpdateParam;\n");
        ex.append("import com.ezcloud.plugins.express.jdbc.InsertParam;                  		\n");
        ex.append("import com.ezcloud.web.EzResult;                  		\n");

        ex.append("ID=$(\"ID\");                  		\n");
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


        ex.append("  StringBuilder updateSql=new StringBuilder();\n");
        ex.append("  updateSql.append(\" where "+idName+"=#{ID} \");\n");

        ex.append(" param.where(updateSql.toString());\n");
        ex.append(" updateSimple(param);\n");
        ex.append(" return ID;\n");

        ex.append("}");
        return ex.toString();
    }

}
