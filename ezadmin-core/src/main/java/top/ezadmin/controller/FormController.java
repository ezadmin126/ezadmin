package top.ezadmin.controller;

import com.ql.util.express.exception.QLBizException;
import com.ql.util.express.exception.QLCompileException;
import top.ezadmin.common.NotExistException;
import top.ezadmin.common.annotation.EzMapping;
import top.ezadmin.common.constants.SessionConstants;
import top.ezadmin.common.enums.ContentTypeEnum;
import top.ezadmin.common.utils.*;
import top.ezadmin.plugins.parser.MapParser;
import top.ezadmin.plugins.sqlog.EzSqlogDataSource;
import top.ezadmin.EzClientBootstrap;
import top.ezadmin.service.FormService;
import top.ezadmin.common.EzAdminRuntimeException;
import top.ezadmin.common.enums.ExceptionCode;
import top.ezadmin.plugins.express.executor.DefaultExpressExecutor;
import top.ezadmin.web.EzResult;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.sql.DataSource;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@EzMapping("/topezadmin/form/")
public class FormController extends BaseController {

    FormService formService = EzProxy.singleInstance(FormService.class);

    //
    @EzMapping(value = "trace.html", name = "view")
    public String trace(HttpServletRequest request, HttpServletResponse response) throws Exception {
        long start=System.currentTimeMillis();

            String result=form(request,response);
        Utils.addLog("form cost:"+(System.currentTimeMillis()-start));
            return EzClientBootstrap.instance().getAdminStyle()+"/form";
    }
    @EzMapping(value = "form.html", name = "view")
    public String form(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String formId = Utils.trimNull(request.getAttribute("FORM_ID"));
        String ENCRYPT_FORM_ID = Utils.trimNull(request.getAttribute("ENCRYPT_FORM_ID"));

        request.setAttribute("formUrl",request.getContextPath()+"/topezadmin/form/form-"+ ENCRYPT_FORM_ID);
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
        Map<String,Object>  form=JSONUtils.parseObjectMap(formService.selectPublishFormById(ENCRYPT_FORM_ID)) ;

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
            request.setAttribute("formSubmitUrl",request.getContextPath()+"/topezadmin/form/doSubmit-"+ ENCRYPT_FORM_ID);
        }

        String layout = ""+core.getOrDefault("layout",EzClientBootstrap.instance().getLayout());

        request.setAttribute("layout",layout);
        String username = Utils.trimNull(request.getSession().getAttribute(SessionConstants.EZ_SESSION_USER_NAME_KEY));
        request.setAttribute("EZ_SESSION_USER_NAME_KEY",username);
        String adminStyle=Utils.trimNullDefault(core.get(JsoupUtil.ADMINSTYLE),EzClientBootstrap.instance().getAdminStyle());
        return adminStyle + "/form";
     }

    @EzMapping("upload.html")
    public void upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id= UUID.randomUUID()+(System.nanoTime()+"");

        System.out.println(request.getParameter("width"));
        System.out.println(request.getParameter("height"));
        String path= EzClientBootstrap.instance().getUploadPath();
        if(Files.notExists(Paths.get(path))){
            Files.createDirectories(Paths.get(path));
        }
        Part filePart = request.getPart("file");
        String suff = filePart.getSubmittedFileName().substring(filePart.getSubmittedFileName().lastIndexOf(".")+1);
        id=id+"_"+suff;

        filePart.write( path+ File.separator+id  );

        if(StringUtils.equals(request.getParameter("descFlag"),"1")){
            Map<String, Object> map = new HashMap<>();
            map.put("errno", 0);
            Map<String,String> dataMap=new HashMap<>();
            String url= EzClientBootstrap.instance().getDownloadUrl()+id;
            dataMap.put("url",url);
            dataMap.put("src",url);

            map.put("data", dataMap);
            map.put("location",url);

            response.setHeader("Content-Type", "application/json;charset=UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print(JSONUtils.toJSONString(map));
            response.getWriter().flush();
        }else{
            String url= EzClientBootstrap.instance().getDownloadUrl()+id;
            Map<String,String> map=new HashMap<>();
            map.put("fileId",id);
            map.put("src",url);
            List<Map<String,String>> data=new ArrayList<>();
            data.add(map);
            EzResult.instance().data( data).printJSONUtils(response); ;
        }
    }
    @EzMapping("download.html")
    public void download(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path= EzClientBootstrap.instance().getUploadPath()+request.getParameter("fileId");
        response.setHeader("Access-Control-Allow-Origin","*");
        String headStr = "attachment; filename=\"" + new String((path).getBytes(), "iso8859-1") + "\"";
        response.setContentType(ContentTypeEnum.loadContentTypeByDownloadUrl(path));
        Files.copy(Paths.get(path),response.getOutputStream());
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
        Map<String,Object>  form=JSONUtils.parseObjectMap(formService.selectPublishFormById(ENCRYPT_FORM_ID)) ;

        if(form==null||form.isEmpty()){
            return "404";
        }
        searchParamsValues.put(JsoupUtil.PLUGIN_FOLD,"detail");
        formService.fillFormById(form,searchParamsValues,sessionMap);
        request.setAttribute("ID",ID);
        request.setAttribute("form",form);

        return EzClientBootstrap.instance().getAdminStyle()+"/detail";
    }

    @EzMapping("doSubmitAdd.html")
    public EzResult doSubmitAdd(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return doSubmit(request,response);
    }
    @EzMapping("doSubmitEdit.html")
    public EzResult doSubmitEdit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return doSubmit(request,response);
    }
    @EzMapping("doSubmit.html")
    public EzResult doSubmit(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String ENCRYPT_FORM_ID = Utils.trimNull(request.getAttribute("ENCRYPT_FORM_ID"));
        String ID=getIdInForm(request);
        try {
          //  logger.info("ezform doSubmit start {} {} ID={}", ENCRYPT_FORM_ID,ID);
            if ( StringUtils.isBlank(ENCRYPT_FORM_ID)) {
                return EzResult.instance().code("404");
            }
            Map<String,Object>  form=JSONUtils.parseObjectMap(formService.selectPublishFormById(ENCRYPT_FORM_ID)) ;

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
                  //  return r;
                }
                return r;
            }

            //data.data
            String defaultTo=request.getContextPath()+"/topezadmin/form/form-"+ ENCRYPT_FORM_ID+"?ID="+toFormId(rowId,request);
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
            return EzResult.instance().setSuccess(false).code("200").setMessage("表达式配置错误");
        }
        catch (EzAdminRuntimeException   e2){
            logger.error("",e2);
            if(ExceptionCode.QLBIZ.name().equalsIgnoreCase(e2.code())){
                return EzResult.instance().setSuccess(false).code("200").setMessage("表达式执行错误");
            }
            return EzResult.instance().setSuccess(false).code("200").setMessage("表达式配置错误");
        }
        catch (QLBizException BE){
            if(BE.getCause().getCause() instanceof  EzAdminRuntimeException){
                return EzResult.instance().setSuccess(false).code("200").setMessage(BE.getCause().getCause().getMessage());
            }else{
                logger.warn("ezform doSubmit error {}   ID={}", ENCRYPT_FORM_ID,ID,BE);
                return EzResult.instance().setSuccess(false).code("500").setMessage(ExceptionUtils.getFullStackTrace(BE));
            }
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
            Map<String,Object>  form=JSONUtils.parseObjectMap(formService.selectPublishFormById(ENCRYPT_FORM_ID)) ;

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

            //   logger.info("保存表单 {}{}{}",  ENCRYPT_FORM_ID,
            //        ENCRYPT_FORM_ID,JSONUtils.toJSONString(searchParamsValues));
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
            logger.error("保存表单失败{}"  ,ENCRYPT_FORM_ID,e);
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
            Map<String,Object>  form=JSONUtils.parseObjectMap(formService.selectPublishFormById(ENCRYPT_FORM_ID)) ;

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

            logger.info("保存表单 {} {}"   ,
                    ENCRYPT_FORM_ID );
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

}
