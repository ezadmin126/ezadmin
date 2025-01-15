package top.ezadmin.controller;

import com.ql.util.express.exception.QLCompileException;
import org.apache.commons.lang.exception.ExceptionUtils;
import top.ezadmin.EzClientBootstrap;
import top.ezadmin.common.EzAdminRuntimeException;
import top.ezadmin.common.NotExistException;
import top.ezadmin.common.annotation.EzMapping;
import top.ezadmin.common.enums.ExceptionCode;
import top.ezadmin.common.utils.*;
import top.ezadmin.dao.FormDao;
import top.ezadmin.dao.PluginsDao;
import top.ezadmin.plugins.express.InsertSimpleOperator;
import top.ezadmin.plugins.express.OperatorParam;
import top.ezadmin.plugins.express.UpdateSimpleOperator;
import top.ezadmin.plugins.express.executor.DefaultExpressExecutor;
import top.ezadmin.plugins.express.jdbc.InsertParam;
import top.ezadmin.plugins.express.jdbc.UpdateParam;
import top.ezadmin.plugins.parser.MapParser;
import top.ezadmin.plugins.sqlog.EzSqlogDataSource;
import top.ezadmin.service.FormService;
import top.ezadmin.web.EzResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * : FormEditController
 * @author EzAdmin
 */
@EzMapping("/topezadmin/formEdit/")
public class FormEditController extends BaseController {

    FormService formService = EzProxy.singleInstance(FormService.class);


    @EzMapping(value = "trace.html", name = "view")
    public String trace(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String result=form(request,response);
        return EzClientBootstrap.instance().getAdminStyle()+"/form";
    }

    @EzMapping("submitEdit.html")
    public EzResult edit( HttpServletRequest request, HttpServletResponse response) throws Exception {
        //所有表单类型的插件
       // System.out.println(request.getParameter("data"));
        Map<String, Object> form= JSONUtils.parseObjectMap(request.getParameter("data"));
        String html=  FormDao.getInstance().updateForm(form);
        if(org.apache.commons.lang.StringUtils.isNotBlank(html)){
            Map<String,Object> coreMap=(Map<String,Object>)form.get("core");
            String formCode= Utils.trimNull( coreMap.get("formcode"));
            String formName=  Utils.trimNull( coreMap.get(JsoupUtil.FORM_NAME.toLowerCase()));
            String datasource=Utils.trimNull( coreMap.get(JsoupUtil.DATASOURCE));
            saveOrUpdate(  formCode,  formName,  html,  datasource);
            return EzResult.instance().data("EZ_CODE",formCode+"");
        }
        return EzResult.instance().fail();
    }

    @EzMapping("loadEdit.html")
    public String loadEdit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String ENCRYPT_FORM_ID = Utils.trimNull(request.getAttribute("ENCRYPT_FORM_ID"));

        request.setAttribute("vi",System.currentTimeMillis());

        Map<String,Object> searchParamsValues=requestToMap(request);

        Map<String, String> sessionMap = sessionToMap(request.getSession());
        Map<String, Object> form= formService.selectConfigEditForm(ENCRYPT_FORM_ID);
        if(form==null||form.isEmpty()){
            form=new HashMap();
            form.put("core",new HashMap());
        }
        List<Map<String, Object>> plugins= PluginsDao.getInstance().allFormPlugin();
        request.setAttribute("plugins",plugins);
        searchParamsValues.put(JsoupUtil.FORM_EDIT_FLAG,"1");

        Map<String,Object> core=(Map<String,Object>)form.get("core");
        core.put(JsoupUtil.ADMINSTYLE,"layui");
        formService.fillFormById(form,searchParamsValues,sessionMap);
        request.setAttribute("form",form);
        return "layui/edit/formedit";
    }

    @EzMapping("submitSourceEdit.html")
    public EzResult submitSourceEdit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String data=request.getParameter("data");
        //所有表单类型的插件
        if(StringUtils.isNotBlank(data)){
            Map<String, Object> list=  FormDao.getInstance().selectAllFormByHtml(data);
            String html=FormDao.getInstance().updateForm(list);
            Map<String,Object> coreMap=(Map<String,Object>)list.get("core");
            String formcode= Utils.trimNull( coreMap.get("formcode"));
            String FORM_NAME=  Utils.trimNull( coreMap.get(JsoupUtil.FORM_NAME));
            String DATASOURCE=  Utils.trimNull( coreMap.get(JsoupUtil.DATASOURCE));
            saveOrUpdate(  formcode,  FORM_NAME,  html,  DATASOURCE);
            return EzResult.instance().data("EZ_CODE",formcode+"");
        }
        return EzResult.instance().fail();
    }

    @EzMapping("sourceEdit.html")
    public String export(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String ENCRYPT_FORM_ID = Utils.trimNull(request.getAttribute("ENCRYPT_FORM_ID"));
        request.setAttribute(JsoupUtil.FORM_EDIT_FLAG,"1");
        Map<String,Object>
            c=formService.selectConfigEditForm(ENCRYPT_FORM_ID);
        request.setAttribute("EZ_CONFIG",c.get("EZ_CONFIG")+"");
        request.setAttribute("ENCRYPT_FORM_ID",ENCRYPT_FORM_ID);
        request.setAttribute("EZ_CODE",ENCRYPT_FORM_ID);
        request.setAttribute("EZ_TYPE",request.getParameter("EZ_TYPE"));
        return  "layui/edit/exportform";
    }
    public  void saveOrUpdate(String formcode,String FORM_NAME,String html,String DATASOURCE) throws Exception {

        Map<String, Object> requestParamMap = new HashMap<>();
        requestParamMap.put("EZ_CONFIG",html);
        requestParamMap.put("DATASOURCE",DATASOURCE);
        requestParamMap.put("EZ_NAME",FORM_NAME);
        requestParamMap.put("EZ_CODE",formcode);
        requestParamMap.put("EZ_TYPE",2);
        Map<String,Object> c=formService.selectConfigEditForm(formcode);
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
            param.add("#{EZ_TYPE}");
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
            param.where(" where EZ_CODE=#{EZ_CODE} AND EZ_TYPE=2");
            o.executeInner(new Object[]{param});
        }

    }

    @EzMapping("importSql.html")
    public String importSql( HttpServletRequest request, HttpServletResponse response) throws Exception {
        return "layui/edit/import";
    }

    @EzMapping("publish.html")
    public EzResult publish(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String ENCRYPT_LIST_ID = Utils.trimNull(request.getAttribute("ENCRYPT_FORM_ID"));
        Map<String,Object>  c=JSONUtils.parseObjectMap(formService.selectPublishFormById(ENCRYPT_LIST_ID)) ;
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
            requestParamMap.put("EZ_TYPE",2);
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
            param.add("#{EZ_TYPE,value=2}");
            param.add("#{EZ_CONFIG}");
            param.add("#{ADD_TIME,value=NOW()}");
            param.add("#{UPDATE_TIME,value=NOW()}");
            param.add("#{IS_DEL,value=0}");
            o.executeInner(new Object[]{param});
        }

        //2.把edit复制到生产
        Map<String,Object> edit=formService.selectConfigEditForm(ENCRYPT_LIST_ID);
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
            requestParamMap.put("EZ_TYPE",2);
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
            param.add("#{EZ_TYPE,value=2}");
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
            requestParamMap.put("EZ_TYPE",2);
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
            param.where(" where EZ_CODE=#{EZ_CODE} and EZ_TYPE=2");
            o.executeInner(new Object[]{param});
        }
        //3.刷新缓存
        EzClientBootstrap.instance().getEzCache().clear();
        request.setAttribute("EZ_TYPE",request.getParameter("EZ_TYPE"));
        return  EzResult.instance();
    }


    @EzMapping(value = "form.html", name = "view")
    public String form(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String formId = Utils.trimNull(request.getAttribute("FORM_ID"));
        String ENCRYPT_FORM_ID = Utils.trimNull(request.getAttribute("ENCRYPT_FORM_ID"));

        request.setAttribute("formUrl",request.getContextPath()+"/topezadmin/formEdit/form-"+ ENCRYPT_FORM_ID);
        //自定义ID
        String ID= getIdInForm(request);
        if (StringUtils.isBlank(formId)&&StringUtils.isBlank(ENCRYPT_FORM_ID)) {
            // notFound(false,request,response);
            throw new NotExistException();
        }
        Map<String,Object> searchParamsValues=requestToMap(request );
        Map<String, String> sessionMap = sessionToMap(request.getSession());
        searchParamsValues.put("ContextPath", request.getContextPath());
        searchParamsValues.put("FORM_ID",formId);
        searchParamsValues.put("ENCRYPT_FORM_ID",ENCRYPT_FORM_ID);
        searchParamsValues.put("ID",Utils.trimNull(ID));
        searchParamsValues.put("vi",request.getAttribute("vi"));
        Map<String,Object>  form= formService.selectConfigEditForm(ENCRYPT_FORM_ID)  ;
             //   new HashMap<>();
        if(Utils.isEmpty(form)){
            return EzClientBootstrap.instance().getAdminStyle()+"/404";
        }
        formService.fillFormById(form,searchParamsValues,sessionMap);
        request.setAttribute("ID",ID);
        request.setAttribute("form",form);
        Map<String, Object>  core= (Map<String, Object>) form.get("core");
        if(StringUtils.isNotBlank(Utils.trimNull(core.get("formSubmitUrl")))){
            request.setAttribute("formSubmitUrl",core.get("formSubmitUrl"));
        }else{
            request.setAttribute("formSubmitUrl",request.getContextPath()+"/topezadmin/formEdit/doSubmit-"+ ENCRYPT_FORM_ID);
        }

        String layout = ""+core.getOrDefault("layout",EzClientBootstrap.instance().getLayout());

        request.setAttribute("layout",layout);


        return core.get(JsoupUtil.ADMINSTYLE)+"/form";
    }

    @EzMapping("doSubmit.html")
    public EzResult doSubmit(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String ENCRYPT_FORM_ID = Utils.trimNull(request.getAttribute("ENCRYPT_FORM_ID"));
        String ID=getIdInForm(request);
        try {

            logger.info("ezform doSubmit start {} {} ID={}", ENCRYPT_FORM_ID,ID);
            if ( StringUtils.isBlank(ENCRYPT_FORM_ID)) {
                return EzResult.instance().code("404");
            }
            Map<String, Object> form=formService.selectConfigEditForm(ENCRYPT_FORM_ID);
            if (form==null||form.isEmpty() ) {
                return EzResult.instance().code("404");
            }
            DataSource formDs=null;
            Map<String, Object> core=(Map<String, Object>)form.get("core");
            if(core!=null){
                formDs= EzClientBootstrap.instance().getDataSourceByKey(core.get(JsoupUtil.DATASOURCE));
            }else{
                form.put("core",new HashMap<>());
            }
            String successurl=Utils.trimNull( core.get(JsoupUtil.SUCCESS_URL));
            String express=   Utils.trimNull( core.get(JsoupUtil.SUBMIT_EXPRESS));
            Map<String, Object> paras = new HashMap<>();
            paras.put("ID", ID);
            List<Map<String,Object>> cardList=(List<Map<String,Object>>)form.get("cards");
            if(Utils.isNotEmpty(cardList)){
                for (int i = 0; i < cardList.size(); i++) {
                    List<Map<String,Object>> items=(List<Map<String,Object>>)cardList.get(i).get("items");
                    if(Utils.isNotEmpty(items)) {
                        for (int j = 0; j < items.size(); j++) {
                            Map<String, Object> item = items.get(j);
                            String item_name = Utils.trimNull(item.get(JsoupUtil.ITEM_NAME)) ;
                            paras.put(item_name, request.getParameter(item_name));
                        }
                    }
                }
            }

            Map<String,Object> searchParamsValues=requestToMap(request );
            Map<String, String> sessionParamMap = sessionToMap(request.getSession());

            paras.putAll(searchParamsValues);
            logger.info("ezform doSubmit execute   {} ID={} param={}",  ENCRYPT_FORM_ID,ID );
            //计算初始化表单的参数值
            Object result = DefaultExpressExecutor.createInstance().datasource(formDs)
                    .express(express)
                    .addParam(paras)
                    .addRequestParam(searchParamsValues)
                    .addSessionParam(sessionParamMap)
                    .execute();
            Object rowId=result;
            if(rowId instanceof  EzResult){
                EzResult  r= (EzResult)result;
                rowId= r.getData();
                if(!r.isSuccess()){
                    r.code("200");
                    return r;
                }
            }

            //data.data
            String defaultTo=request.getContextPath()+"/topezadmin/formEdit/form-"+ ENCRYPT_FORM_ID+"?ID="+toFormId(rowId,request);
            if(StringUtils.isNotBlank(successurl)){
                if(StringUtils.contains(successurl,"/")){
                    successurl=request.getContextPath()+successurl;
                }
                paras.put("ID",toFormId(rowId,request));
                defaultTo= MapParser.parseDefaultEmpty(successurl, paras).getResult();
            }
            return EzResult.instance().data( defaultTo);
        }catch (QLCompileException ex){
            logger.error("",ex);
            return EzResult.instance().setSuccess(false).code("500").setMessage("表达式配置错误");
        }
        catch (EzAdminRuntimeException e2){
            logger.error("",e2);
            if(ExceptionCode.QLBIZ.name().equalsIgnoreCase(e2.code())){
                return EzResult.instance().setSuccess(false).code("500").setMessage("表达式执行错误");
            }
            return EzResult.instance().setSuccess(false).code("500").setMessage("表达式配置错误");
        }
        catch (Exception e) {
            logger.error("ezform doSubmit error {}   ID={}", ENCRYPT_FORM_ID,ID,e);
            return EzResult.instance().setSuccess(false).code("500").setMessage(ExceptionUtils.getFullStackTrace(e));
        }
    }
    @EzMapping("doDelete.html")
    public EzResult doDelete(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String ENCRYPT_FORM_ID = Utils.trimNull(request.getAttribute("ENCRYPT_FORM_ID"));

        try {
            String ID=getIdInForm(request);
            if (StringUtils.isBlank(ENCRYPT_FORM_ID)) {
                return EzResult.instance().code("404");
            }
            Map<String, Object> form=formService.selectConfigEditForm(ENCRYPT_FORM_ID);

            if (form == null) {
                return EzResult.instance().code("404");
            }
            EzSqlogDataSource formDs=null;
            Map<String, Object> core=(Map<String, Object>)form.get("core");
            if(core!=null){
                formDs= EzClientBootstrap.instance().getDataSourceByKey(core.get(JsoupUtil.DATASOURCE));
            }else{
                form.put("core",new HashMap<>());
            }
            String express=  Utils.trimNull(core.get(JsoupUtil.DELETE_EXPRESS));
            Map<String, Object> paras = new HashMap<>();
            paras.put("ID", ID);
            List<Map<String,Object>> cardList=(List<Map<String,Object>>)form.get("cards");
            if(Utils.isNotEmpty(cardList)){
                for (int i = 0; i < cardList.size(); i++) {
                    List<Map<String,Object>> items=(List<Map<String,Object>>)cardList.get(i).get("items");
                    if(Utils.isNotEmpty(items)) {
                        for (int j = 0; j < items.size(); j++) {
                            Map<String, Object> item = items.get(j);
                            String item_name = Utils.trimNull(item.get(JsoupUtil.ITEM_NAME)) ;
                            paras.put(item_name, request.getParameter(item_name));
                        }
                    }
                }
            }

            Map<String,Object> searchParamsValues=requestToMap(request );
            paras.putAll(searchParamsValues);
            Map<String, String> sessionParamMap = sessionToMap(request.getSession());
            //计算初始化表单的参数值
            Object result = DefaultExpressExecutor.createInstance().datasource(formDs)
                    .express(express)
                    .addParam(paras)
                    .addRequestParam(searchParamsValues)
                    .addSessionParam(sessionParamMap)
                    .execute();

            Object rowId=result;
            if(rowId instanceof  EzResult){
                EzResult  r= (EzResult)result;
                rowId= r.getData();
                if(!r.isSuccess()){
                    r.code("200");
                    return r;
                }
            }
            return EzResult.instance().data( toFormId(rowId,request) );
        } catch (Exception e) {
            logger.info("保存表单失败{}"  ,ENCRYPT_FORM_ID,e);
            return EzResult.instance().setSuccess(false).code("500").setMessage("服务器异常"+ExceptionUtils.getFullStackTrace(e));
        }
    }
    @EzMapping("doStatus.html")
    public EzResult doStatus(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String ENCRYPT_FORM_ID = Utils.trimNull(request.getAttribute("ENCRYPT_FORM_ID"));

        try {
            String ID=getIdInForm(request);
            if (StringUtils.isBlank(ENCRYPT_FORM_ID)) {
                return EzResult.instance().code("404");
            }
            Map<String, Object> form=formService.selectConfigEditForm(ENCRYPT_FORM_ID);

            if (form == null) {
                return EzResult.instance().code("404");
            }
            EzSqlogDataSource formDs=null;
            Map<String, Object> core=(Map<String, Object>)form.get("core");
            if(core!=null){
                formDs= EzClientBootstrap.instance().getDataSourceByKey(core.get(JsoupUtil.DATASOURCE));
            }else{
                form.put("core",new HashMap<>());
            }
            String express=  Utils.trimNull(core.get(JsoupUtil.STATUS_EXPRESS));
            Map<String, Object> paras = new HashMap<>();
            paras.put("ID", ID);
            List<Map<String,Object>> cardList=(List<Map<String,Object>>)form.get("cards");
            if(Utils.isNotEmpty(cardList)){
                for (int i = 0; i < cardList.size(); i++) {
                    List<Map<String,Object>> items=(List<Map<String,Object>>)cardList.get(i).get("items");
                    if(Utils.isNotEmpty(items)) {
                        for (int j = 0; j < items.size(); j++) {
                            Map<String, Object> item = items.get(j);
                            String item_name = Utils.trimNull(item.get(JsoupUtil.ITEM_NAME)) ;
                            paras.put(item_name, request.getParameter(item_name));
                        }
                    }
                }
            }

            Map<String,Object> searchParamsValues=requestToMap(request );
            paras.putAll(searchParamsValues);
            Map<String, String> sessionParamMap = sessionToMap(request.getSession());

            //计算初始化表单的参数值
            Object rowId = DefaultExpressExecutor.createInstance().datasource(formDs)
                    .express(express)
                    .addParam(paras)
                    .addRequestParam(searchParamsValues)
                    .addSessionParam(sessionParamMap)
                    .execute();
            if(rowId instanceof  EzResult){
                EzResult  r= (EzResult)rowId;
                rowId= r.getData();
                if(!r.isSuccess()){
                    r.code("200");
                    return r;
                }
            }

            return EzResult.instance().data( toFormId(rowId,request) );
        } catch (Exception e) {
            logger.info("保存表单失败{}  "  ,   ENCRYPT_FORM_ID,e);
            return EzResult.instance().setSuccess(false).code("500").setMessage("服务器异常");
        }
    }


    @EzMapping(value = "detail.html", name = "view")
    public String detail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String ENCRYPT_FORM_ID = Utils.trimNull(request.getAttribute("ENCRYPT_FORM_ID"));
        //自定义ID
        String ID= getIdInForm(request);
        if ( StringUtils.isBlank(ENCRYPT_FORM_ID)) {
            return "404";
        }
        Map<String,Object> searchParamsValues=requestToMap(request );
        Map<String, String> sessionMap = sessionToMap(request.getSession());
        searchParamsValues.put("ContextPath", request.getContextPath());
        searchParamsValues.put("ENCRYPT_FORM_ID",ENCRYPT_FORM_ID);
        searchParamsValues.put("ID",ID);
        Map<String, Object> form=formService.selectConfigEditForm(ENCRYPT_FORM_ID);

        if(form==null||form.isEmpty()){
            return "404";
        }
        searchParamsValues.put(JsoupUtil.PLUGIN_FOLD,"detail");
        formService.fillFormById(form,searchParamsValues,sessionMap);
        request.setAttribute("ID",ID);
        request.setAttribute("form",form);

        return EzClientBootstrap.instance().getAdminStyle()+"/detail";
    }


}
