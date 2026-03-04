package top.ezadmin.controller;


import com.ql.util.express.exception.QLBizException;
import com.ql.util.express.exception.QLCompileException;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ezadmin.EzBootstrap;
import top.ezadmin.common.EzAdminRuntimeException;
import top.ezadmin.common.NotExistException;
import top.ezadmin.common.constants.SessionConstants;
import top.ezadmin.common.enums.DefaultParamEnum;
import top.ezadmin.common.enums.ExceptionCode;
import top.ezadmin.common.enums.JdbcTypeEnum;
import top.ezadmin.common.enums.ParamNameEnum;
import top.ezadmin.common.utils.*;
import top.ezadmin.dao.Dao;
import top.ezadmin.plugins.express.executor.DefaultExpressExecutor;
import top.ezadmin.plugins.parser.MapParser;
import top.ezadmin.service.FormService;
import top.ezadmin.service.ListService;
import top.ezadmin.web.EzResult;
import top.ezadmin.web.RequestContext;

import javax.sql.DataSource;
import java.util.*;

public class FormController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(FormController.class);

    FormService formService = EzProxy.singleInstance(FormService.class);
    private ListService listService = EzProxy.singleInstance(ListService.class);

    //
    public EzResult trace(RequestContext requestContext, String method, String formUrlCode) throws Exception {
        return form(requestContext, method, formUrlCode);
    }

    public EzResult form(RequestContext requestContext, String method, String formUrlCode) throws Exception {
        String formId = formUrlCode;
        String ENCRYPT_FORM_ID = formUrlCode;
        //自定义ID
        String ID = getIdInForm(requestContext);
        if (StringUtils.isBlank(formId) && StringUtils.isBlank(ENCRYPT_FORM_ID)) {
            throw new NotExistException();
        }
        Map<String, Object> templateParam = new HashMap<>();
        templateParam.put("formUrl", requestContext.getContextPath() + "/topezadmin/form/form-" + ENCRYPT_FORM_ID);
     
        Map<String, Object> searchParamsValues = requestContext.getRequestParams();
        Map<String, String> sessionMap = requestContext.getSessionParams();
        searchParamsValues.put("ContextPath", requestContext.getContextPath());
        searchParamsValues.put("FORM_ID", formId);
        searchParamsValues.put("ENCRYPT_FORM_ID", ENCRYPT_FORM_ID);

        searchParamsValues.put("vi", requestContext.getParameter("vi"));

        Map<String, Object> form = JSONUtils.parseObjectMap(formService.selectPublishFormById(ENCRYPT_FORM_ID));

        if (Utils.isEmpty(form)) {
            return EzResult.instance().code("404");
        }
        formService.fillFormById(form, searchParamsValues, sessionMap);
        templateParam.put("ID", Utils.trimEmptyDefault(ID, Utils.trimNull(searchParamsValues.get("ID"))));
            templateParam.put("form", form);
        Map<String, Object> core = (Map<String, Object>) form.get("core");
        if (core!=null&&StringUtils.isNotBlank(Utils.trimNull(core.get("formSubmitUrl")))) {
            templateParam.put("formSubmitUrl", core.get("formSubmitUrl"));
        } else {
            templateParam.put("formSubmitUrl", requestContext.getContextPath() + "/topezadmin/form/doSubmit-" + ENCRYPT_FORM_ID);
        }
        String layout = "" + core.getOrDefault("layout", EzBootstrap.config().getLayout());
        templateParam.put("layout", layout);
        templateParam.put("cacheFlag", EzBootstrap.config().isSqlCache());
        String username = Utils.trimNull(sessionMap.get(SessionConstants.EZ_SESSION_USER_NAME_KEY));
        templateParam.put("EZ_SESSION_USER_NAME_KEY", username);
        templateParam.putAll(sessionMap);
        String adminStyle = Utils.trimNullDefault(core.get(JsoupUtil.ADMINSTYLE), EzBootstrap.config().getAdminStyle());
        String template = Utils.trimNullDefault(core.get(JsoupUtil.TEMPLATE), "form");
        templateParam.put("prefixUrl", EzBootstrap.config().getPrefixUrl());
        return render(adminStyle + "/" + template,templateParam);
    }


    public EzResult doSubmit(RequestContext requestContext, String method, String formUrlCode) throws Exception {

        String ENCRYPT_FORM_ID =formUrlCode;
        String ID = getIdInForm(requestContext);
        try {
            //  logger.info("ezform doSubmit start {} {} ID={}", ENCRYPT_FORM_ID,ID);
            if (StringUtils.isBlank(ENCRYPT_FORM_ID)) {
                return EzResult.instance().code("404");
            }
            Map<String, Object> form = JSONUtils.parseObjectMap(formService.selectPublishFormById(ENCRYPT_FORM_ID));

            if (form == null || form.isEmpty()) {
                return EzResult.instance().code("404");
            }

            DataSource formDs = null;
            Map<String, Object> core = (Map<String, Object>) form.get("core");
            if (core != null) {
                formDs = EzBootstrap.getInstance().getDataSourceByKey(core.get(JsoupUtil.DATASOURCE));
            } else {
                form.put("core", new HashMap<>());
            }
            String successurl = Utils.trimNull(core.get(JsoupUtil.SUCCESS_URL));
            String express = Utils.trimNull(core.get(JsoupUtil.SUBMIT_EXPRESS));
            Map<String, Object> paras = new HashMap<>();
            paras.put("ID", ID);
            List<Map<String, Object>> cardList = (List<Map<String, Object>>) form.get("cards");
            if (Utils.isNotEmpty(cardList)) {
                for (int i = 0; i < cardList.size(); i++) {
                    List<Map<String, Object>> items = (List<Map<String, Object>>) cardList.get(i).get("items");
                    if (Utils.isNotEmpty(items)) {
                        for (int j = 0; j < items.size(); j++) {
                            Map<String, Object> item = items.get(j);
                            String item_name = Utils.trimNull(item.get(JsoupUtil.ITEM_NAME));
                            paras.put(item_name, requestContext.getParameter(item_name));
                        }
                    }
                }
            }

            Map<String, Object> searchParamsValues = requestContext.getRequestParams();
            Map<String, String> sessionParamMap = requestContext.getSessionParams();

            paras.putAll(searchParamsValues);
            //计算初始化表单的参数值
            Object result = DefaultExpressExecutor.createInstance().datasource(formDs)
                    .express(express)
                    .addParam(paras)
                    .addRequestParam(searchParamsValues)
                    .addSessionParam(sessionParamMap)
                    .execute();
            Object rowId = result;
            if (rowId instanceof EzResult) {

                EzResult r = (EzResult) result;
                rowId = r.getData();
                if (!r.isSuccess()) {
                    r.code("200");
                    //  return r;
                }
                return r;
            }

            //data.data
            String defaultTo = requestContext.getContextPath() + "/topezadmin/form/form-" + ENCRYPT_FORM_ID + "?ID=" +Utils.trimNullDefault(rowId,ID);
            if (StringUtils.isNotBlank(successurl)) {
                if (StringUtils.contains(successurl, "/")) {
                    successurl = requestContext.getContextPath() + successurl;
                }
                paras.put("ID", Utils.trimNullDefault(rowId,ID));
                defaultTo = MapParser.parseDefaultEmpty(successurl, paras).getResult();
            }
            return EzResult.instance().data(defaultTo);
        } catch (QLCompileException ex) {
            logger.error("", ex);
            return EzResult.instance().setSuccess(false).code("200").setMessage("表达式配置错误");
        } catch (EzAdminRuntimeException e2) {
            logger.error("", e2);
            if (ExceptionCode.QLBIZ.name().equalsIgnoreCase(e2.code())) {
                return EzResult.instance().setSuccess(false).code("200").setMessage("表达式执行错误");
            }
            return EzResult.instance().setSuccess(false).code("200").setMessage("表达式配置错误");
        } catch (QLBizException BE) {
            if (BE.getCause().getCause() instanceof EzAdminRuntimeException) {
                return EzResult.instance().setSuccess(false).code("200").setMessage(BE.getCause().getCause().getMessage());
            } else {
                logger.warn("ezform doSubmit error {}   ID={}", ENCRYPT_FORM_ID, ID, BE);
                return EzResult.instance().setSuccess(false).code("500").setMessage("服务器异常");
            }
        } catch (Exception e) {
            logger.error("ezform doSubmit error {}   ID={}", ENCRYPT_FORM_ID, ID, e);
            return EzResult.instance().setSuccess(false).code("500").setMessage("服务器异常");
        }
    }

    public EzResult doDelete(RequestContext requestContext, String method, String formUrlCode) throws Exception {
        String ENCRYPT_FORM_ID = formUrlCode;

        try {
            String ID = getIdInForm(requestContext);
            if (StringUtils.isBlank(ENCRYPT_FORM_ID)) {
                return EzResult.instance().code("404");
            }
            Map<String, Object> form = JSONUtils.parseObjectMap(formService.selectPublishFormById(ENCRYPT_FORM_ID));

            if (form == null) {
                return EzResult.instance().code("404");
            }
             DataSource formDs = null;
            Map<String, Object> core = (Map<String, Object>) form.get("core");
            if (core != null) {
                formDs = EzBootstrap.getInstance().getDataSourceByKey(core.get(JsoupUtil.DATASOURCE));
            } else {
                core=new HashMap<>();
                form.put("core", core);
            }
            String express = Utils.trimNull(core.get(JsoupUtil.DELETE_EXPRESS));
            Map<String, Object> paras = new HashMap<>();
            paras.put("ID", ID);
            List<Map<String, Object>> cardList = (List<Map<String, Object>>) form.get("cards");
            if (Utils.isNotEmpty(cardList)) {
                for (int i = 0; i < cardList.size(); i++) {
                    List<Map<String, Object>> items = (List<Map<String, Object>>) cardList.get(i).get("items");
                    if (Utils.isNotEmpty(items)) {
                        for (int j = 0; j < items.size(); j++) {
                            Map<String, Object> item = items.get(j);
                            String item_name = Utils.trimNull(item.get(JsoupUtil.ITEM_NAME));
                            paras.put(item_name, requestContext.getParameter(item_name));
                        }
                    }
                }
            }

            Map<String, Object> searchParamsValues = requestToMap(requestContext);
            paras.putAll(searchParamsValues);
            Map<String, String> sessionParamMap = sessionToMap(requestContext);

            //   logger.info("保存表单 {}{}{}",  ENCRYPT_FORM_ID,
            //        ENCRYPT_FORM_ID,JSONUtils.toJSONString(searchParamsValues));
            //计算初始化表单的参数值
            Object result = DefaultExpressExecutor.createInstance().datasource(formDs)
                    .express(express)
                    .addParam(paras)
                    .addRequestParam(searchParamsValues)
                    .addSessionParam(sessionParamMap)
                    .execute();

            Object rowId = result;
            if (rowId instanceof EzResult) {
                EzResult r = (EzResult) result;
                rowId = r.getData();
                if (!r.isSuccess()) {
                    r.code("200");
                    return r;
                }
            }
            return EzResult.instance().data(toFormId(rowId, requestContext));
        } catch (EzAdminRuntimeException e2) {
            logger.error("", e2);
            if (ExceptionCode.QLBIZ.name().equalsIgnoreCase(e2.code())) {
                return EzResult.instance().setSuccess(false).code("200").setMessage("表达式执行错误");
            }
            return EzResult.instance().setSuccess(false).code("200").setMessage("表达式配置错误");
        } catch (QLBizException BE) {
            logger.error("保存表单失败{}", ENCRYPT_FORM_ID,BE);
            if (BE.getCause().getCause() instanceof EzAdminRuntimeException) {
                return EzResult.instance().setSuccess(false).code("200").setMessage(BE.getCause().getCause().getMessage());
            } else {
                return EzResult.instance().setSuccess(false).code("500").setMessage(ExceptionUtils.getFullStackTrace(BE));
            }
        } catch (Exception e) {
            logger.error("保存表单失败{}", ENCRYPT_FORM_ID, e);
            return EzResult.instance().setSuccess(false).code("500").setMessage("服务器异常");
        }
    }

    public EzResult doStatus(RequestContext requestContext, String method, String formUrlCode) throws Exception {

        String ENCRYPT_FORM_ID = formUrlCode;

        try {
            String ID = getIdInForm(requestContext);
            if (StringUtils.isBlank(ENCRYPT_FORM_ID)) {
                return EzResult.instance().code("404");
            }
            Map<String, Object> form = JSONUtils.parseObjectMap(formService.selectPublishFormById(ENCRYPT_FORM_ID));

            if (form == null) {
                return EzResult.instance().code("404");
            }
            DataSource formDs = null;
            Map<String, Object> core = (Map<String, Object>) form.get("core");
            if (core != null) {
                formDs = EzBootstrap.getInstance().getDataSourceByKey(core.get(JsoupUtil.DATASOURCE));
            } else {
                core = new HashMap<>();
                form.put("core", core);
            }
            String express = Utils.trimNull(core.get(JsoupUtil.STATUS_EXPRESS));
            Map<String, Object> paras = new HashMap<>();
            paras.put("ID", ID);
            List<Map<String, Object>> cardList = (List<Map<String, Object>>) form.get("cards");
            if (Utils.isNotEmpty(cardList)) {
                for (int i = 0; i < cardList.size(); i++) {
                    List<Map<String, Object>> items = (List<Map<String, Object>>) cardList.get(i).get("items");
                    if (Utils.isNotEmpty(items)) {
                        for (int j = 0; j < items.size(); j++) {
                            Map<String, Object> item = items.get(j);
                            String item_name = Utils.trimNull(item.get(JsoupUtil.ITEM_NAME));
                            paras.put(item_name, requestContext.getParameter(item_name));
                        }
                    }
                }
            }

            Map<String, Object> searchParamsValues = requestToMap(requestContext);
            paras.putAll(searchParamsValues);
            Map<String, String> sessionParamMap = sessionToMap(requestContext);

            logger.info("保存表单 {} {}",
                    ENCRYPT_FORM_ID);
            //计算初始化表单的参数值
            Object rowId = DefaultExpressExecutor.createInstance().datasource(formDs)
                    .express(express)
                    .addParam(paras)
                    .addRequestParam(searchParamsValues)
                    .addSessionParam(sessionParamMap)
                    .execute();
            if (rowId instanceof EzResult) {
                EzResult r = (EzResult) rowId;
                rowId = r.getData();
                if (!r.isSuccess()) {
                    r.code("200");
                    return r;
                }
            }

            return EzResult.instance().data(toFormId(rowId, requestContext));
        } catch (Exception e) {
            logger.info("保存表单失败{}  ", ENCRYPT_FORM_ID, e);
            return EzResult.instance().setSuccess(false).code("500").setMessage("服务器异常");
        }
    }

    /**
     * 表单初始化UI
     * @param requestContext
     * @param method
     * @param formUrlCode
     * @return EzResult
     * @throws Exception
     */
    public EzResult page(RequestContext requestContext, String method, String formUrlCode) throws Exception {
        String formId = formUrlCode;
        Map<String, Object> templateParam=new HashMap<>();
        // 使用统一加载器（文件优先，数据库降级）
        top.ezadmin.dao.dto.DslConfig dslConfig = DslLoader.loadDsl(formId, "form");
        if (dslConfig == null) {
            //跳转到一个全新的 静态页面
            return  EzResult.instance().code("404") ;
        }
        Map<String, Object> form = dslConfig.getConfig();
        if(form!=null&&StringUtils.isBlank((String)form.get("initUrl"))){
            form.put("initUrl", "/topezadmin/form/data-"+formUrlCode);
        }
        iniFormItem(requestContext,form);
        templateParam.put("form", form);
        templateParam.put("requestContext",requestContext);
        templateParam.put("cacheFlag",EzBootstrap.config().isSqlCache());
        templateParam.put("ID",requestContext.getRequestParams().get("ID"));
        templateParam.put("uploadUrl",EzBootstrap.config().getUploadUrl());
        templateParam.put("downloadUrl",EzBootstrap.config().getDownloadUrl());
        templateParam.put("formSubmitUrl", "/topezadmin/form/submit-"+formUrlCode);
        templateParam.put("ENCRYPT_FORM_ID",  formUrlCode);
        templateParam.putAll(EzBootstrap.config().getConfig());
        templateParam.put("EZ_SESSION_USER_NAME_KEY", Utils.trimNull(requestContext.getSessionParams().get(SessionConstants.EZ_SESSION_USER_NAME_KEY)));
        templateParam.put("EZ_SESSION_USER_ID_KEY", Utils.trimNull(requestContext.getSessionParams().get(SessionConstants.EZ_SESSION_USER_ID_KEY)));
        return render("layui/dsl/formTemplate",templateParam);
    }
    public void iniFormItem(RequestContext requestContext,Map<String, Object> list) {
        List<Map<String, Object>> cardList = (List<Map<String, Object>>) list.get("cardList");
        if(Utils.isEmpty(cardList)){
            return;
        }
        cardList.forEach(card->{
            Map<String,Object> componentJson=new HashMap<>();

            componentJson.put("type",card.get("type"));
            componentJson.put("label",card.get("label"));
            componentJson.put("iframe",card.get("iframe"));
            componentJson.put("description",card.get("description"));
            componentJson.put("buttonList",card.get("buttonList"));

            card.put("componentJson",JSONUtils.toJSONString(componentJson));

            Object fieldListObj = card.get("fieldList");
            List<List<Map<String, Object>>> fieldList = getFlattenedFieldList(fieldListObj);
            if(Utils.isNotEmpty(fieldList ) ){
                fieldList.forEach(row->{
                    if(Utils.isNotEmpty(row)){
                        row.forEach(field -> processFormField(requestContext,field));
                    }
                });
            }
            // Don't replace the original fieldList - keep object array format for template
            // card.put("fieldList", fieldList);
        });
    }

    /**
     * 处理单个表单字段的初始化
     * @param item 字段配置
     */
    private void processFormField(RequestContext requestContext, Map<String, Object> item) {
        Map<String, Object> initData = (Map<String, Object>) item.get("initData");
        if (Utils.isNotEmpty(initData)) {
            listService.initComponentData(requestContext, item);
        }
        try {
            Map<String, Object> props = (Map<String, Object>) item.get("props");
            if(props != null&&props.containsKey("lay-verify")){
                try {
                    String layVerify = StringUtils.lowerCase((String) props.get("lay-verify"));
                    if (StringUtils.contains(layVerify, "required")) {
                        props.put("required", true);
                    } else {
                        props.put("required", false);
                    }
                }catch (Exception e){
                    logger.warn("配置错误，lay-verify必须是一个字符串");
                    //ignore
                }
            }
            //props
            if(props != null && item.get("component") != null && item.get("component").equals("input")){
                props.putIfAbsent("lay-affix", "clear");
            }
            if (props != null && props.containsKey("validate")) {
                Map<String, Object> validate = (Map<String, Object>) props.get("validate");
                if (validate != null && validate.containsKey("rule")) {
                    Map<String, Object> rules = (Map<String, Object>) validate.get("rule");
                    if (rules != null && rules.containsKey("required")) {
                        Object required = rules.get("required");
                        if (required instanceof Boolean) {
                            props.put("required", (Boolean) required);
                        } else if (required instanceof String) {
                            props.put("required", StringUtils.equalsIgnoreCase((String) required, "true"));
                        }
                    }
                }
            }

        }catch (Exception e){
            logger.info("props error{}", JSONUtils.toJSONString(item), e);
        }
        if(item.get("classAppend") == null){
            item.put("classAppend", " layui-col-md12");
        }
    }

    /**
     * 获取扁平化的字段列表（支持新的对象数组格式）
     * @param fieldListObj fieldList 对象
     * @return 扁平化的字段列表
     */
    private List<List<Map<String, Object>>> getFlattenedFieldList(Object fieldListObj) {
        try {
            if (!(fieldListObj instanceof List)) {
                return null;
            }
            List<Object> list = (List<Object>) fieldListObj;
            if (list.isEmpty()) {
                return null;
            }

            // 新格式：[{row:[...]}, {row:[...]}]
            if (list.get(0) instanceof Map) {
                Map firstItem = (Map) list.get(0);
                if (firstItem.containsKey("row")) {
                    List<List<Map<String, Object>>> result = new ArrayList<>();
                    for (Object item : list) {
                        Map rowObj = (Map) item;
                        List<Map<String, Object>> row = (List<Map<String, Object>>) rowObj.get("row");
                        if (row != null) {
                            result.add(row);
                        }
                    }
                    return result;
                }
            }

            logger.error("FieldList配置错误，应该使用对象数组格式: [{\"row\":[...]}, {\"row\":[...]}]");
            return null;
        } catch (Exception e) {
            logger.error("FieldList配置错误，请检查格式{}", fieldListObj, e);
        }
        return null;
    }

    /**
     * 表单初始化数据接口
     * @param requestContext
     * @param method
     * @param formUrlCode
     * @return EzResult
     * @throws Exception
     */
    public EzResult data(RequestContext requestContext, String method, String formUrlCode) throws Exception {
        String formId = formUrlCode;
        // 使用统一加载器（文件优先，数据库降级）
        top.ezadmin.dao.dto.DslConfig dslConfig = DslLoader.loadDsl(formId, "form");
        if (dslConfig == null) {
            return  EzResult.instance().code("JSON").data(EzResult.instance().code("404"));
        }
        Map<String, Object> form = dslConfig.getConfig();
        if (form == null) {
            return EzResult.instance().code("JSON").data(EzResult.instance().code("404"));
        }
        iniFormItem(requestContext,form);
        //只返回数据，屏蔽模板
        String ID = Utils.getStringByObject(requestContext.getRequestParams(), "ID");
        Map<String, Object> initItemMap = new HashMap<>();
        Map<String, Object> resultMapFinal = new HashMap<>();
        initItemMap.putAll(requestContext.getRequestParams());
        if (StringUtils.isNotBlank(ID)) {
            try {
                DefaultExpressExecutor expressExecutor = DefaultExpressExecutor.createInstance();
                expressExecutor.datasource(EzBootstrap.getInstance().getDataSourceByKey(form.get("dataSource")));
                String initExpress = Utils.expressToString( form.get("initExpress"));
                expressExecutor.express(initExpress);

                expressExecutor.addParam(requestContext.getRequestParams());
                expressExecutor.addRequestParam(requestContext.getRequestParams());
                expressExecutor.addSessionParam(requestContext.getSessionParams());
                Map<String, Object> resultMap = expressExecutor.executeAndReturnMap();
                for (Map.Entry<String, Object> entry : resultMap.entrySet()) {
                    if (StringUtils.isNotBlank(Utils.trimNull(entry.getValue()))) {
                        initItemMap.put(entry.getKey(), Utils.trimNull(entry.getValue()));
                        resultMapFinal.put(entry.getKey(), Utils.trimNull(entry.getValue()));
                    }
                }
            } catch (Exception e) {
                Utils.addLog(JSONUtils.toJSONString(form.get("core")), e);
            }
        }
        if (initItemMap.containsKey(ID) && StringUtils.isBlank(Utils.trimNull(initItemMap.get(ID)))) {
            initItemMap.put("ID", initItemMap.get("ID"));
            resultMapFinal.put("ID", initItemMap.get("ID"));
        }
        return EzResult.instance().code("JSON") .data(EzResult.instance().data(resultMapFinal));
    }



    public EzResult submit(RequestContext requestContext, String method, String formUrlCode) throws Exception {

        String ENCRYPT_FORM_ID =formUrlCode;
        String ID = getIdInForm(requestContext);
        try {
            //  logger.info("ezform doSubmit start {} {} ID={}", ENCRYPT_FORM_ID,ID);
            if (StringUtils.isBlank(ENCRYPT_FORM_ID)) {
                return EzResult.instance().code("404");
            }
            // 使用统一加载器（文件优先，数据库降级）
            top.ezadmin.dao.dto.DslConfig dslConfig = DslLoader.loadDsl(formUrlCode, "form");
            if (dslConfig == null) {
                return EzResult.instance().code("404");
            }
            Map<String, Object> form = dslConfig.getConfig();

            DataSource formDs =   EzBootstrap.getInstance().getDataSourceByKey(form.get("dataSource"));


            String successurl = Utils.trimNull(form.get("successUrl"));
            String express = Utils.expressToString(form.get("submitExpress"));
            Map<String, Object> paras = new HashMap<>();
            paras.put("ID", ID);
            List<Map<String, Object>> cardList = (List<Map<String, Object>>) form.get("cardList");
            if (Utils.isNotEmpty(cardList)) {
                //把提交参数转换成 ez识别的参数
                for (int i = 0; i < cardList.size(); i++) {
                    // 支持一维和二维数组格式
                    List<List<Map<String, Object>>> items = getFlattenedFieldList(cardList.get(i).get("fieldList"));
                    if (Utils.isNotEmpty(items)) {//一行
                        for (int k = 0; k < items.size(); k++) {
                            List<Map<String, Object>> row = items.get(k);
                            for (int j = 0; j < row.size(); j++) {
                                Map<String, Object> item = row.get(j);
                                String item_name = Utils.trimNull(item.get(JsoupUtil.ITEM_NAME));
                                String component = Utils.trimEmptyDefault(item.get("component"), "input");
                                String orgValue = Utils.trimNull(requestContext.getRequestParams().get(item_name));
                                Map<String, Object> props = new HashMap<>();
                                try {
                                    props.putAll((Map<String, Object>) item.get("props"));
                                }catch (Exception e){
                                    //
                                }

                                //联动日期区间  -
                                if (component.equalsIgnoreCase("date")
                                        && Utils.isTrue(props.get("range"))
                                        && StringUtils.isNotBlank(orgValue)) {
                                    //默认datetime
                                    item.put(JsoupUtil.JDBCTYPE, JdbcTypeEnum.DATETIME.getName());
                                    orgValue = DefaultParamEnum.getValue(orgValue);
                                    String[] valueSplit = orgValue.split(" - ");
                                    item.put(ParamNameEnum.itemParamValue.getName(), orgValue);
                                    if (valueSplit.length == 2) {
                                        requestContext.getRequestParams().put(item_name+"_START", valueSplit[0]);
                                        requestContext.getRequestParams().put(item_name+"_END", valueSplit[1]);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Map<String, Object> searchParamsValues = requestContext.getRequestParams();
            Map<String, String> sessionParamMap = requestContext.getSessionParams();

            paras.putAll(searchParamsValues);
            //计算初始化表单的参数值
            Object result = DefaultExpressExecutor.createInstance().datasource(formDs)
                    .express(express)
                    .addParam(paras)
                    .addRequestParam(searchParamsValues)
                    .addSessionParam(sessionParamMap)
                    .execute();
            Object rowId = result;
            if (rowId instanceof EzResult) {

                EzResult r = (EzResult) result;
                rowId = r.getData();
                if (!r.isSuccess()) {
                    r.code("200");
                    //  return r;
                }
                return r;
            }

            //data.data
            String defaultTo = requestContext.getContextPath() + "/topezadmin/form/page-" + ENCRYPT_FORM_ID + "?ID=" +Utils.trimNullDefault(rowId,ID);
            if (StringUtils.isNotBlank(successurl)) {
                if (StringUtils.contains(successurl, "/")) {
                    successurl = requestContext.getContextPath() + successurl;
                }
                paras.put("ID", Utils.trimNullDefault(rowId,ID));
                defaultTo = MapParser.parseDefaultEmpty(successurl, paras).getResult();
            }
            return EzResult.instance().data(defaultTo);
        } catch (QLCompileException ex) {
            logger.error("", ex);
            return EzResult.instance().setSuccess(false).code("200").setMessage("表达式配置错误");
        } catch (EzAdminRuntimeException e2) {
            logger.error("", e2);
            if (ExceptionCode.QLBIZ.name().equalsIgnoreCase(e2.code())) {
                return EzResult.instance().setSuccess(false).code("200").setMessage("表达式执行错误");
            }
            return EzResult.instance().setSuccess(false).code("200").setMessage("表达式配置错误");
        } catch (QLBizException BE) {
            if (BE.getCause().getCause() instanceof EzAdminRuntimeException) {
                return EzResult.instance().setSuccess(false).code("200").setMessage(BE.getCause().getCause().getMessage());
            } else {
                logger.warn("ezform doSubmit error {}   ID={}", ENCRYPT_FORM_ID, ID, BE);
                return EzResult.instance().setSuccess(false).code("500").setMessage("服务器异常");
            }
        } catch (Exception e) {
            logger.error("ezform doSubmit error {}   ID={}", ENCRYPT_FORM_ID, ID, e);
            return EzResult.instance().setSuccess(false).code("500").setMessage("服务器异常");
        }
    }

    public EzResult delete(RequestContext requestContext, String method, String formUrlCode) throws Exception {
        String ENCRYPT_FORM_ID = formUrlCode;

        try {
            String ID = getIdInForm(requestContext);
            if (StringUtils.isBlank(ENCRYPT_FORM_ID)) {
                return EzResult.instance().code("404");
            }
            if (StringUtils.isBlank(ENCRYPT_FORM_ID)) {
                return EzResult.instance().code("404");
            }
            // 使用统一加载器（文件优先，数据库降级）
            top.ezadmin.dao.dto.DslConfig dslConfig = DslLoader.loadDsl(formUrlCode, "form");
            if (dslConfig == null) {
                return EzResult.instance().code("404");
            }
            Map<String, Object> form = dslConfig.getConfig();

            DataSource formDs =   EzBootstrap.getInstance().getDataSourceByKey(form.get("dataSource"));


            String successurl = Utils.trimNull(form.get("successUrl"));
            String express = Utils.expressToString(form.get("deleteExpress"));

            Map<String, Object> paras = new HashMap<>();
            paras.put("ID", ID);
            List<Map<String, Object>> cardList = (List<Map<String, Object>>) form.get("cardList");
            if (Utils.isNotEmpty(cardList)) {
                for (int i = 0; i < cardList.size(); i++) {
                    // 支持一维和二维数组格式
                    List<List<Map<String, Object>>> items  = getFlattenedFieldList(cardList.get(i).get("fieldList"));
                    if (Utils.isNotEmpty(items)) {

                        for (int k = 0; k < items.size(); k++) {
                            List<Map<String, Object>> row = items.get(k);
                            for (int j = 0; j < row.size(); j++) {
                                Map<String, Object> item = row.get(j);
                                String item_name = Utils.trimNull(item.get(JsoupUtil.ITEM_NAME));
                                paras.put(item_name, requestContext.getParameter(item_name));
                            }
                        }
                    }
                }
            }

            Map<String, Object> searchParamsValues = requestToMap(requestContext);
            paras.putAll(searchParamsValues);
            Map<String, String> sessionParamMap = sessionToMap(requestContext);

            //   logger.info("保存表单 {}{}{}",  ENCRYPT_FORM_ID,
            //        ENCRYPT_FORM_ID,JSONUtils.toJSONString(searchParamsValues));
            //计算初始化表单的参数值
            Object result = DefaultExpressExecutor.createInstance().datasource(formDs)
                    .express(express)
                    .addParam(paras)
                    .addRequestParam(searchParamsValues)
                    .addSessionParam(sessionParamMap)
                    .execute();

            Object rowId = result;
            if (rowId instanceof EzResult) {
                EzResult r = (EzResult) result;
                rowId = r.getData();
                if (!r.isSuccess()) {
                    r.code("200");
                    return r;
                }
            }
            return EzResult.instance().data(toFormId(rowId, requestContext));
        } catch (EzAdminRuntimeException e2) {
            logger.error("", e2);
            if (ExceptionCode.QLBIZ.name().equalsIgnoreCase(e2.code())) {
                return EzResult.instance().setSuccess(false).code("200").setMessage("表达式执行错误");
            }
            return EzResult.instance().setSuccess(false).code("200").setMessage("表达式配置错误");
        } catch (QLBizException BE) {
            logger.error("保存表单失败{}", ENCRYPT_FORM_ID,BE);
            if (BE.getCause().getCause() instanceof EzAdminRuntimeException) {
                return EzResult.instance().setSuccess(false).code("200").setMessage(BE.getCause().getCause().getMessage());
            } else {
                return EzResult.instance().setSuccess(false).code("500").setMessage(ExceptionUtils.getFullStackTrace(BE));
            }
        } catch (Exception e) {
            logger.error("保存表单失败{}", ENCRYPT_FORM_ID, e);
            return EzResult.instance().setSuccess(false).code("500").setMessage("服务器异常");
        }
    }

    public EzResult status(RequestContext requestContext, String method, String formUrlCode) throws Exception {

        String ENCRYPT_FORM_ID = formUrlCode;

        try {
            String ID = getIdInForm(requestContext);
            if (StringUtils.isBlank(ENCRYPT_FORM_ID)) {
                return EzResult.instance().code("404");
            }
            if (StringUtils.isBlank(ENCRYPT_FORM_ID)) {
                return EzResult.instance().code("404");
            }
            // 使用统一加载器（文件优先，数据库降级）
            top.ezadmin.dao.dto.DslConfig dslConfig = DslLoader.loadDsl(formUrlCode, "form");
            if (dslConfig == null) {
                return EzResult.instance().code("404");
            }
            Map<String, Object> form = dslConfig.getConfig();

            DataSource formDs =   EzBootstrap.getInstance().getDataSourceByKey(form.get("dataSource"));


            String express = Utils.expressToString(form.get("statusExpress"));

            Map<String, Object> paras = new HashMap<>();
            paras.put("ID", ID);
            List<Map<String, Object>> cardList = (List<Map<String, Object>>) form.get("cardList");
            if (Utils.isNotEmpty(cardList)) {
                for (int i = 0; i < cardList.size(); i++) {
                    //  二维数组格式
                    List<List<Map<String, Object>>> items = getFlattenedFieldList(cardList.get(i).get("fieldList"));
                    if (Utils.isNotEmpty(items)) {
                        for (int k = 0; k < items.size(); k++) {
                            List<Map<String, Object>> row = items.get(k);
                            for (int j = 0; j < row.size(); j++) {
                                Map<String, Object> item = row.get(j);
                                String item_name = Utils.trimNull(item.get(JsoupUtil.ITEM_NAME));
                                paras.put(item_name, requestContext.getParameter(item_name));
                            }
                        }
                    }
                }
            }

            Map<String, Object> searchParamsValues = requestToMap(requestContext);
            paras.putAll(searchParamsValues);
            Map<String, String> sessionParamMap = sessionToMap(requestContext);

            logger.info("保存表单 {} {}",  ENCRYPT_FORM_ID);
            //计算初始化表单的参数值
            Object rowId = DefaultExpressExecutor.createInstance().datasource(formDs)
                    .express(express)
                    .addParam(paras)
                    .addRequestParam(searchParamsValues)
                    .addSessionParam(sessionParamMap)
                    .execute();
            if (rowId instanceof EzResult) {
                EzResult r = (EzResult) rowId;
                rowId = r.getData();
                if (!r.isSuccess()) {
                    r.code("200");
                    return r;
                }
            }

            return EzResult.instance().data(toFormId(rowId, requestContext));
        } catch (Exception e) {
            logger.info("保存表单失败{}  ", ENCRYPT_FORM_ID, e);
            return EzResult.instance().setSuccess(false).code("500").setMessage("服务器异常");
        }
    }


}
