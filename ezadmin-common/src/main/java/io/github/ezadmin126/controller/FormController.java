package io.github.ezadmin126.controller;

import com.ql.util.express.exception.QLBizException;
import com.ql.util.express.exception.QLCompileException;
import io.github.ezadmin126.common.utils.*;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.ezadmin126.EzBootstrap;
import io.github.ezadmin126.common.EzAdminRuntimeException;
import io.github.ezadmin126.common.NotExistException;
import io.github.ezadmin126.common.constants.SessionConstants;
import io.github.ezadmin126.common.enums.ExceptionCode;
import io.github.ezadmin126.plugins.express.executor.DefaultExpressExecutor;
import io.github.ezadmin126.plugins.parser.MapParser;
import io.github.ezadmin126.service.FormService;
import io.github.ezadmin126.web.EzResult;
import io.github.ezadmin126.web.RequestContext;

import javax.sql.DataSource;
import java.util.*;

public class FormController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(FormController.class);

    FormService formService = EzProxy.singleInstance(FormService.class);

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
        if (StringUtils.isNotBlank(Utils.trimNull(core.get("formSubmitUrl")))) {
            templateParam.put("formSubmitUrl", core.get("formSubmitUrl"));
        } else {
            templateParam.put("formSubmitUrl", requestContext.getContextPath() + "/topezadmin/form/doSubmit-" + ENCRYPT_FORM_ID);
        }
        String layout = "" + core.getOrDefault("layout", EzBootstrap.config().getLayout());
        templateParam.put("layout", layout);
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
                form.put("core", new HashMap<>());
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

        String ENCRYPT_FORM_ID = Utils.trimNull((String)requestContext.getRequestParams().get("ENCRYPT_FORM_ID"));

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
                form.put("core", new HashMap<>());
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

}
