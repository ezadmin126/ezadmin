package com.ezadmin.biz.form.controller;

import com.ezadmin.biz.dao.Dao;
import com.ezadmin.common.annotation.EzMapping;
import com.ezadmin.common.enums.ContentTypeEnum;
import com.ezadmin.common.utils.*;
import com.ezadmin.plugins.parser.MapParser;
import com.ezadmin.plugins.sqlog.EzSqlogDataSource;
import com.ezadmin.EzBootstrap;
import com.ezadmin.biz.base.controller.BaseController;
import com.ezadmin.biz.emmber.form.DefalutEzFormBuilder;
import com.ezadmin.biz.emmber.form.EzFormBuilder;
import com.ezadmin.biz.form.service.FormService;
import com.ezadmin.common.EzAdminRuntimeException;
import com.ezadmin.common.enums.ExceptionCode;
import com.ezadmin.plugins.express.executor.DefaultExpressExecutor;
import com.ezadmin.web.EzResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.sql.DataSource;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@EzMapping("/ezadmin/form/")
public class FormController extends BaseController {

    FormService formService = EzProxy.singleInstance(FormService.class);

    //
    @EzMapping(value = "trace.html", name = "view")
    public String trace(HttpServletRequest request, HttpServletResponse response) throws Exception {
            String formId = Utils.trimNull(request.getAttribute("FORM_ID"));
            String ENCRYPT_FORM_ID = Utils.trimNull(request.getAttribute("ENCRYPT_FORM_ID"));
            request.setAttribute("formSubmitUrl", request.getContextPath()+"/ezadmin/form/traceSubmit-" + ENCRYPT_FORM_ID);
            request.setAttribute("formUrl", request.getContextPath()+"/ezadmin/form/trace-" + ENCRYPT_FORM_ID);

            String ID = getIdInForm(request);

            if (StringUtils.isBlank(formId)) {
                notFound(false, request, response);
                return "404";
            }
            Map<String, Object> searchParamsValues = requestToMap(request);
            Map<String, String> sessionMap = sessionToMap(request.getSession());

            searchParamsValues.put("FORM_ID", formId);
            searchParamsValues.put("ID", ID);
            EzFormBuilder form = new DefalutEzFormBuilder(EzBootstrap.instance().getOriginDataSource(), searchParamsValues,sessionMap);

            if (form == null) {
                notFound(false, request, response);
                return "404";
            }
            //form.load();
            form.renderHtml();
            request.setAttribute("data", form.getData());
            return "form/form";
    }
    @EzMapping(value = "form.html", name = "view")
    public String form(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String formId = Utils.trimNull(request.getAttribute("FORM_ID"));
        String ENCRYPT_FORM_ID = Utils.trimNull(request.getAttribute("ENCRYPT_FORM_ID"));
        request.setAttribute("formSubmitUrl",request.getContextPath()+"/ezadmin/form/doSubmit-"+ ENCRYPT_FORM_ID);
        request.setAttribute("formUrl",request.getContextPath()+"/ezadmin/form/form-"+ ENCRYPT_FORM_ID);
        //自定义ID
        String ID= getIdInForm(request);

        if (StringUtils.isBlank(formId)&&StringUtils.isBlank(ENCRYPT_FORM_ID)) {
           // notFound(false,request,response);
            return "404";
        }
        Map<String,Object> searchParamsValues=requestToMap(request );
        Map<String, String> sessionMap = sessionToMap(request.getSession());
        searchParamsValues.put("ContextPath", request.getContextPath());
        searchParamsValues.put("FORM_ID",formId);
        searchParamsValues.put("ENCRYPT_FORM_ID",ENCRYPT_FORM_ID);
        searchParamsValues.put("ID",ID);
        Map<String, Object> form=new HashMap<>();
        if(StringUtils.isNotBlank(ENCRYPT_FORM_ID)){
            form=   JSONUtils.parseObjectMap(formService.selectAllFormById(ENCRYPT_FORM_ID))  ;
        }
        formService.fillFormById(form,searchParamsValues,sessionMap);
        request.setAttribute("form",form);
        return "layui/form/form";
     }

    @EzMapping("upload.html")
    public void upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id= UUID.randomUUID()+(System.nanoTime()+"");

        System.out.println(request.getParameter("width"));
        System.out.println(request.getParameter("height"));
        String path=EzBootstrap.instance().getUploadPath();
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
            String url=EzBootstrap.instance().getDownloadUrl()+id;
            dataMap.put("url",url);
            dataMap.put("src",url);

            map.put("data", dataMap);
            map.put("location",url);

            response.setHeader("Content-Type", "application/json;charset=UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print(JSONUtils.toJSONString(map));
            response.getWriter().flush();
        }else{
            String url=EzBootstrap.instance().getDownloadUrl()+id;
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
        String path=EzBootstrap.instance().getUploadPath()+request.getParameter("fileId");
        response.setHeader("Access-Control-Allow-Origin","*");
        String headStr = "attachment; filename=\"" + new String((path).getBytes(), "iso8859-1") + "\"";
        response.setContentType(ContentTypeEnum.loadContentTypeByDownloadUrl(path));
        Files.copy(Paths.get(path),response.getOutputStream());
    }


    @EzMapping(value = "detail.html", name = "view")
    public String detail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String formId = Utils.trimNull(request.getAttribute("FORM_ID"));
        String ENCRYPT_FORM_ID = Utils.trimNull(request.getAttribute("ENCRYPT_FORM_ID"));
        request.setAttribute("formSubmitUrl",request.getContextPath()+"/ezadmin/form/doSubmit-"+ ENCRYPT_FORM_ID);
        request.setAttribute("formUrl",request.getContextPath()+"/ezadmin/form/form-"+ ENCRYPT_FORM_ID);

        String ID= getIdInForm(request);

        if (StringUtils.isBlank(formId)&&StringUtils.isBlank(ENCRYPT_FORM_ID)) {
            return "404";
        }
        Map<String,Object> searchParamsValues=requestToMap(request );
        Map<String, String> sessionMap = sessionToMap(request.getSession());
        searchParamsValues.put("ContextPath", request.getContextPath());
        searchParamsValues.put("FORM_ID",formId);
        searchParamsValues.put("ENCRYPT_FORM_ID",ENCRYPT_FORM_ID);
        searchParamsValues.put("ID",ID);

        searchParamsValues.put(JsoupUtil.PLUGIN_FOLD,"detail");

        EzFormBuilder form=new DefalutEzFormBuilder(EzBootstrap.instance().getOriginDataSource(), searchParamsValues,sessionMap);

        if (form == null) {
            return "404";
        }
        searchParamsValues.put("FORM_ID",form.getData().getForm().getFormId());
        form.renderHtml();
        request.setAttribute("data",form.getData());
        return "layui/form/detail";
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

            logger.info("ezform doSubmit start {} {} ID={}", ENCRYPT_FORM_ID,ID);
            if ( StringUtils.isBlank(ENCRYPT_FORM_ID)) {
                return EzResult.instance().code("404");
            }
            Map<String, Object>   form=    formService.selectAllFormMapById(ENCRYPT_FORM_ID)  ;

            if (form==null||form.isEmpty() ) {
                return EzResult.instance().code("404");
            }

            DataSource formDs=null;
            Map<String, Object> core=(Map<String, Object>)form.get("core");
            if(core!=null){
                formDs= EzBootstrap.instance().getDataSourceByKey(core.get(JsoupUtil.DATASOURCE));
            }else{
                form.put("core",new HashMap<>());
            }
            String successurl=Utils.trimNull( core.get("successurl"));
            String express=   Utils.trimNull( core.get("subcode"));
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
            logger.info("ezform doSubmit execute   {} ID={} param={}",  ENCRYPT_FORM_ID,ID, JSONUtils.toJSONString(searchParamsValues));
            //计算初始化表单的参数值
            Object result = DefaultExpressExecutor.createInstance().datasource(formDs)
                    .express(express)
                    .addParam(paras)
                    .addRequestParam(requestToMap(request))
                    .addSessionParam(sessionParamMap)
                    .execute();
            Object rowId=result;
            if(rowId instanceof  EzResult){
                EzResult  r= (EzResult)result;
                rowId= r.getData();
                if(!r.isSuccess()){
                    return r;
                }
            }

            //data.data
            String defaultTo=request.getContextPath()+"/ezadmin/form/form-"+ ENCRYPT_FORM_ID+"?ID="+toFormId(rowId,request);
            if(form.containsKey(JsoupUtil.SUCCESS_URL) && StringUtils.isNotBlank(successurl)){
                 if(StringUtils.contains(successurl,"/")){
                    successurl=request.getContextPath()+successurl;
                }
                defaultTo= MapParser.parseDefaultEmpty(successurl, paras).getResult();
            }
            return EzResult.instance().data( defaultTo);
        }catch (EzAdminRuntimeException e2){
            if(ExceptionCode.QLBIZ.name().equalsIgnoreCase(e2.code())){
                return EzResult.instance().setSuccess(false).code("500").setMessage("表达式执行错误");
            }
            return EzResult.instance().setSuccess(false).code("500").setMessage("表达式配置错误");
        }
        catch (Exception e) {
            logger.error("ezform doSubmit error {}   ID={}", ENCRYPT_FORM_ID,ID,e);
            return EzResult.instance().setSuccess(false).code("500").setMessage("服务器异常");
        }
    }
    @EzMapping("doDelete.html")
    public EzResult doDelete(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String formId =  Utils.trimNull(request.getAttribute("FORM_ID"));
        String ENCRYPT_FORM_ID = Utils.trimNull(request.getAttribute("ENCRYPT_FORM_ID"));
        String express="";
        try {
            String ID=getIdInForm(request);
            if (StringUtils.isBlank(formId)&&StringUtils.isBlank(ENCRYPT_FORM_ID)) {
                return EzResult.instance().code("404");
            }
            Map<String, String> form = formService.selectFormById(formId,ENCRYPT_FORM_ID);

            if (form == null) {
                return EzResult.instance().code("404");
            }
            formId=form.get("FORM_ID");
            EzSqlogDataSource formDs = EzBootstrap.instance().getDataSourceByKey(form.get("datasource"));
              express = Utils.trimNull(form.get(JsoupUtil.DELETE_EXPRESS));

            Map<String, Object> paras = new HashMap<>();
            paras.put("ID", ID);
            List<Map<String, String>> itemOriginList = formService.getItemListByFormId(formId,ENCRYPT_FORM_ID);

            for (int i = 0; i < itemOriginList.size(); i++) {
                String itemName = Utils.trimNull(itemOriginList.get(i).get(JsoupUtil.ITEM_NAME));
                paras.put(itemName, request.getParameter(itemName));
            }

            Map<String,Object> searchParamsValues=requestToMap(request );
            paras.putAll(searchParamsValues);
            logger.info("保存表单 {}{}{}",  formId,
                    ENCRYPT_FORM_ID,JSONUtils.toJSONString(searchParamsValues));
            //计算初始化表单的参数值
            Object result = DefaultExpressExecutor.createInstance().datasource(formDs)
                    .express(express)
                    .addParam(paras)
                    .addRequestParam(requestToMap(request))
                    .execute();


            Object rowId=result;
            if(rowId instanceof  EzResult){
                EzResult  r= (EzResult)result;
                rowId= r.getData();
                if(!r.isSuccess()){
                    return r;
                }
            }
            return EzResult.instance().data( toFormId(rowId,request) );
        } catch (Exception e) {
            logger.info("保存表单失败{}{}{}"  , express,formId,ENCRYPT_FORM_ID,e);
            return EzResult.instance().setSuccess(false).code("500").setMessage("服务器异常");
        }
    }
    @EzMapping("doStatus.html")
    public EzResult doStatus(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String formId =  Utils.trimNull(request.getAttribute("FORM_ID"));
        String ENCRYPT_FORM_ID = Utils.trimNull(request.getAttribute("ENCRYPT_FORM_ID"));
        String express="";
        try {
            String ID=getIdInForm(request);
            if (StringUtils.isBlank(formId)&&StringUtils.isBlank(ENCRYPT_FORM_ID)) {
                return EzResult.instance().code("404");
            }
            Map<String, String> form = formService.selectFormById(formId,ENCRYPT_FORM_ID);
            if (form == null) {
                return EzResult.instance().code("404");
            }
            formId=form.get("FORM_ID");
            EzSqlogDataSource formDs = EzBootstrap.instance().getDataSourceByKey(form.get("datasource"));
              express = Utils.trimNull(form.get(JsoupUtil.STATUS_EXPRESS));

            Map<String, Object> paras = new HashMap<>();
            paras.put("ID", ID);
            List<Map<String, String>> itemOriginList = formService.getItemListByFormId(formId,ENCRYPT_FORM_ID);

            for (int i = 0; i < itemOriginList.size(); i++) {
                String itemName = Utils.trimNull(itemOriginList.get(i).get(JsoupUtil.ITEM_NAME));
                paras.put(itemName, request.getParameter(itemName));
            }

            Map<String,Object> searchParamsValues=requestToMap(request );
            paras.putAll(searchParamsValues);
            logger.info("保存表单 {}{}{}",  formId,
                    ENCRYPT_FORM_ID,JSONUtils.toJSONString(searchParamsValues));
            //计算初始化表单的参数值
            Object rowId = DefaultExpressExecutor.createInstance().datasource(formDs)
                    .express(express)
                    .addParam(paras)
                    .addRequestParam(requestToMap(request))
                    .execute();
            return EzResult.instance().data( toFormId(rowId,request) );
        } catch (Exception e) {
            logger.info("保存表单失败{}{}{}"  , express,formId,ENCRYPT_FORM_ID,e);
            return EzResult.instance().setSuccess(false).code("500").setMessage("服务器异常");
        }
    }

}
