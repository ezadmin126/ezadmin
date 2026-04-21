package top.ezadmin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ezadmin.EzBootstrap;
import top.ezadmin.common.NotExistException;
import top.ezadmin.common.constants.RequestParamConstants;
import top.ezadmin.common.utils.*;
import top.ezadmin.dao.ListDao;
import top.ezadmin.plugins.express.InsertSimpleOperator;
import top.ezadmin.plugins.express.OperatorParam;
import top.ezadmin.plugins.express.UpdateSimpleOperator;
import top.ezadmin.plugins.express.jdbc.InsertParam;
import top.ezadmin.plugins.express.jdbc.UpdateParam;
import top.ezadmin.service.ListService;
import top.ezadmin.web.EzResult;
import top.ezadmin.web.RequestContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * : ListEditController
 *
 * @author EzAdmin
 */
public class ListEditController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(ListEditController.class);

    ListService listService = EzProxy.singleInstance(ListService.class);

    public EzResult preview(RequestContext requestContext, String method, String listUrlCode) throws Exception {

        String encryptListId = listUrlCode;
        if (StringUtils.isBlank(encryptListId)) {
            throw new NotExistException();
        }
        Map<String, Object> requestParamMap = requestContext.getRequestParams();
        Map<String, Object> templateParam = new HashMap<>();
        templateParam.putIfAbsent("perPageInt", requestContext.getParameter("perPageInt"));
        templateParam.putIfAbsent("currentPage", requestContext.getParameter("page"));
        templateParam.put("vi", requestContext.getParameter("vi"));
        templateParam.put("ContextPath", requestContext.getContextPath());

        Map<String, String> sessionParamMap = requestContext.getSessionParams();
        Map<String, Object> list = listService.selectConfigEditList(encryptListId);
        if (StringUtils.isNotBlank((String) requestContext.getParameter("version"))) {
            list = listService.selectConfigHistoryList(encryptListId, (String) requestContext.getParameter("version"));
        }
        if (!Utils.isNotEmpty(list)) {
            throw new NotExistException();
        }

        templateParam.put(RequestParamConstants._SEARCH_ITEM_DISPLAY, requestContext.getParameter("_SEARCH_ITEM_DISPLAY"));

        templateParam.put("listUrl", requestContext.getContextPath() + "/topezadmin/listEdit/list-" + listUrlCode);
        templateParam.put("_EZ_SERVER_NAME", "//" + requestContext.getServerName() + ":" + requestContext.getServerPort());
        templateParam.put("cacheFlag", EzBootstrap.config().isSqlCache());

        listService.fillListById(list, requestParamMap, sessionParamMap);
        templateParam.put("data", list);
        Map<String, Object> coreMap = (Map<String, Object>) list.get("core");
        String pagetotaltype = "" + coreMap.getOrDefault("pagesync", "0");
        String layout = "" + coreMap.getOrDefault("layout", EzBootstrap.config().getLayout());

        templateParam.put("layout", layout);

        if (StringUtils.equalsIgnoreCase(pagetotaltype, "1")) {
            listService.fillCountById(list, requestParamMap, sessionParamMap);
        }
        String adminStyle = Utils.trimNullDefault(coreMap.get(JsoupUtil.ADMINSTYLE), EzBootstrap.config().getAdminStyle());
        String template = Utils.trimNullDefault(coreMap.get(JsoupUtil.TEMPLATE), "list");
        return render(adminStyle + "/" + template, templateParam);
    }

    public EzResult list(RequestContext requestContext, String method, String listUrlCode) throws Exception {
        return sourceEdit(requestContext, method, listUrlCode);
    }

    public EzResult loadList(RequestContext requestContext, String method, String listUrlCode) throws Exception {

        String ENCRYPT_LIST_ID = Utils.trimNull((String) requestContext.getRequestParams().get("ENCRYPT_LIST_ID"));
        Map<String, Object> requestParamMap = requestToMap(requestContext);
        Map<String, String> sessionParamMap = sessionToMap(requestContext);
        requestParamMap.put("loadDataFlag", 0);
        Map<String, Object> list = listService.selectConfigEditList(ENCRYPT_LIST_ID);
        if (StringUtils.isNotBlank((String) requestContext.getParameter("version"))) {
            list = listService.selectConfigHistoryList(ENCRYPT_LIST_ID, (String) requestContext.getParameter("version"));
        }
        if (Utils.isEmpty(list)) {
            list = new HashMap();
            list.put("core", new HashMap());
            list.put("search", new ArrayList<>());
            list.put("tab", new ArrayList<>());
            list.put("tablebtn", new ArrayList<>());
            list.put("rowbtn", new ArrayList<>());
            list.put("col", new ArrayList<>());
            Map<String, Object> coreMap = (Map<String, Object>) list.get("core");
            coreMap.put(JsoupUtil.ADMINSTYLE, "layui"); //编辑状态使用layui
            coreMap.put(JsoupUtil.TEMPLATE, "list");
            coreMap.put(JsoupUtil.APPEND_HEAD, "");
            coreMap.put(JsoupUtil.APPEND_FOOT, "");
        } else {
            Map<String, Object> coreMap = (Map<String, Object>) list.get("core");
            coreMap.put(JsoupUtil.ADMINSTYLE, "layui");
            coreMap.putIfAbsent(JsoupUtil.TEMPLATE, "list");
            coreMap.putIfAbsent(JsoupUtil.APPEND_HEAD, "");
            coreMap.putIfAbsent(JsoupUtil.APPEND_FOOT, "");
        }
        listService.fillListById(list, requestParamMap, sessionParamMap);
        Map<String, Object> templateParam = new HashMap<>();

        templateParam.put("data", list);
        templateParam.put("vi", System.currentTimeMillis());
        templateParam.put("ENCRYPT_LIST_ID", requestContext.getParameter("ENCRYPT_LIST_ID"));
        return render("layui/edit/listedit", templateParam);
    }

    public EzResult submitEdit(RequestContext requestContext, String method, String listUrlCode) throws Exception {
        Map<String, Object> list = JSONUtils.parseObjectMap((String) requestContext.getParameter("data"));
        String html = ListDao.getInstance().transEntityToHtmlConfig(list);
        if (StringUtils.isNotBlank(html)) {
            Map<String, Object> coreMap = (Map<String, Object>) list.get("core");
            String listcode = Utils.trimNull(coreMap.get("id"));
            String listname = Utils.trimNull(coreMap.get("listname"));
            String DATASOURCE = Utils.trimNull(coreMap.get(JsoupUtil.DATASOURCE));
            saveOrUpdate(listcode, listname, html, DATASOURCE);
            return EzResult.instance().data("EZ_CODE", listcode + "");
        }
        return EzResult.instance().fail();
    }


    public EzResult publish(RequestContext requestContext, String method, String listUrlCode) throws Exception {
        String ENCRYPT_LIST_ID = listUrlCode;
        Map<String, Object> c = JSONUtils.parseObjectMap(listService.selectDbPublishListById(ENCRYPT_LIST_ID));
        //生成如果有数据 //1.把生产的复制到历史表
        if (Utils.isNotEmpty(c)) {
            String config = c.get("EZ_CONFIG") + "";
            String DATASOURCE = c.get("DATASOURCE") + "";
            String name = c.get("EZ_NAME") + "";
            String code = ENCRYPT_LIST_ID;
            Map<String, Object> requestParamMap = requestToMap(requestContext);
            requestParamMap.put("EZ_CONFIG", config);
            requestParamMap.put("DATASOURCE", DATASOURCE);
            requestParamMap.put("EZ_NAME", name);
            requestParamMap.put("EZ_CODE", code);
            requestParamMap.put("EZ_TYPE", 1);
            OperatorParam op = new OperatorParam();
            op.setParams(requestParamMap);
            op.setDs(EzBootstrap.getInstance().getEzDataSource());
            Utils.addParam(op);
            InsertSimpleOperator o = new InsertSimpleOperator();
            InsertParam param = new InsertParam();
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
        Map<String, Object> edit = listService.selectConfigEditList(ENCRYPT_LIST_ID);
        String config = edit.get("EZ_CONFIG") + "";
        String DATASOURCE = edit.get("DATASOURCE") + "";
        String name = edit.get("EZ_NAME") + "";
        String code = ENCRYPT_LIST_ID;
        if (Utils.isEmpty(c)) {
            Map<String, Object> requestParamMap = requestToMap(requestContext);
            requestParamMap.put("EZ_CONFIG", config);
            requestParamMap.put("DATASOURCE", DATASOURCE);
            requestParamMap.put("EZ_NAME", name);
            requestParamMap.put("EZ_CODE", code);
            requestParamMap.put("EZ_TYPE", 1);
            OperatorParam op = new OperatorParam();
            op.setParams(requestParamMap);
            op.setDs(EzBootstrap.getInstance().getEzDataSource());
            Utils.addParam(op);
            InsertSimpleOperator o = new InsertSimpleOperator();
            InsertParam param = new InsertParam();
            param.table("T_EZADMIN_PUBLISH");
            param.add("#{EZ_CODE}");
            param.add("#{DATASOURCE}");
            param.add("#{EZ_NAME}");
            param.add("#{EZ_TYPE,value=1}");
            param.add("#{EZ_CONFIG}");
            param.add("#{ADD_TIME,value=NOW()}");
            param.add("#{IS_DEL,value=0}");
            o.executeInner(new Object[]{param});
        } else {
            Map<String, Object> requestParamMap = requestToMap(requestContext);
            requestParamMap.put("EZ_CONFIG", config);
            requestParamMap.put("DATASOURCE", DATASOURCE);
            requestParamMap.put("EZ_NAME", name);
            requestParamMap.put("EZ_CODE", code);
            requestParamMap.put("EZ_TYPE", 1);
            OperatorParam op = new OperatorParam();
            op.setParams(requestParamMap);
            op.setDs(EzBootstrap.getInstance().getEzDataSource());
            Utils.addParam(op);
            UpdateSimpleOperator o = new UpdateSimpleOperator();
            UpdateParam param = new UpdateParam();
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
        EzBootstrap.clear();
        requestContext.getRequestParams().put("EZ_TYPE", requestContext.getParameter("EZ_TYPE"));
        return EzResult.instance();
    }

    private String saveOrUpdate(String code, String name, String html, String datasource) throws Exception {
        Map<String, Object> requestParamMap = new HashMap<>();
        requestParamMap.put("EZ_CONFIG", html);
        requestParamMap.put("DATASOURCE", datasource);
        requestParamMap.put("EZ_NAME", name);
        requestParamMap.put("EZ_CODE", code);
        requestParamMap.put("EZ_TYPE", 1);
        Map<String, Object> c = listService.selectConfigEditList(code);
        OperatorParam op = new OperatorParam();
        op.setParams(requestParamMap);
        op.setDs(EzBootstrap.getInstance().getEzDataSource());
        Utils.addParam(op);
        if (Utils.isEmpty(c)) {
            InsertSimpleOperator o = new InsertSimpleOperator();
            InsertParam param = new InsertParam();
            param.table("T_EZADMIN_EDIT");
            param.add("#{EZ_CODE}");
            param.add("#{DATASOURCE}");
            param.add("#{EZ_NAME}");
            param.add("#{EZ_TYPE,value=1}");
            param.add("#{EZ_CONFIG}");
            param.add("#{ADD_TIME,value=NOW()}");
            param.add("#{IS_DEL,value=0}");
            o.executeInner(new Object[]{param});
        } else {
            UpdateSimpleOperator o = new UpdateSimpleOperator();
            UpdateParam param = new UpdateParam();
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

    public EzResult submitSourceEdit(RequestContext requestContext, String method, String listUrlCode) throws Exception {
        String ezType = (String) requestContext.getParameter("EZ_TYPE");
        String data = (String) requestContext.getParameter("data");
        if (StringUtils.isNotBlank(data)) {
            Map<String, Object> list = ListDao.getInstance().selectAllListByHtml(data);
            String html = ListDao.getInstance().transEntityToHtmlConfig(list);
            Map<String, Object> coreMap = (Map<String, Object>) list.get("core");
            String listcode = Utils.trimNull(coreMap.get("id"));
            String listname = Utils.trimNull(coreMap.get("listname"));
            String DATASOURCE = Utils.trimNull(coreMap.get(JsoupUtil.DATASOURCE));
            saveOrUpdate(listcode, listname, html, DATASOURCE);
            return EzResult.instance().data("EZ_CODE", listcode + "");
        }
        return EzResult.instance().fail();
    }

    public EzResult sourceEdit(RequestContext requestContext, String method, String listUrlCode) throws Exception {
        String ENCRYPT_LIST_ID = listUrlCode;
        Map<String, Object> templateParam = new HashMap<>();

        Map<String, Object> c = listService.selectConfigEditList(ENCRYPT_LIST_ID);
        if (StringUtils.isNotBlank((String) requestContext.getParameter("version"))) {
            c = listService.selectConfigHistoryList(ENCRYPT_LIST_ID, (String) requestContext.getParameter("version"));
        }
        templateParam.put("EZ_CONFIG", c.get("EZ_CONFIG") + "");
        if (c != null && c.containsKey("core")) {
            templateParam.put("treeFlag", ((Map) c.get("core")).containsKey("expandalldefault"));
        }
        templateParam.put("ENCRYPT_LIST_ID", ENCRYPT_LIST_ID);
        templateParam.put("EZ_TYPE", requestContext.getParameter("EZ_TYPE"));
//        requestToMap(requestContext).forEach((k, v) -> {
//            requestContext.getRequestParams().put(k, v);
//        });
        return render("layui/edit/export", templateParam);
    }

    public void trace(RequestContext requestContext, String method, String listUrlCode) throws Exception {
        String listId = listUrlCode;
        String ENCRYPT_LIST_ID = Utils.trimNull(requestContext.getRequestParams().get("ENCRYPT_LIST_ID"));
        long start = System.currentTimeMillis();
        if (Utils.getLog() != null) {
            Utils.addLog("start ID=" + ENCRYPT_LIST_ID);
        }

        Map<String, Object> requestParamMap = requestToMap(requestContext);


        Map<String, String> sessionParamMap = sessionToMap(requestContext);

        if (Utils.getLog() != null) {
            Utils.addLog("requestParamMap " + requestParamMap);
        }
        if (Utils.getLog() != null) {
            Utils.addLog("sessionParamMap " + JSONUtils.toJSONString(sessionParamMap));
        }

        Map<String, Object> list = listService.selectConfigEditList(ENCRYPT_LIST_ID);
        if (Utils.isEmpty(list)) {
            throw new NotExistException();
        }
        try {
            listService.fillCountById(list, requestParamMap, sessionParamMap);
        } catch (Exception e) {
            if (Utils.getLog() != null) {
                Utils.addLog("加载总数异常", e);
            }
        }
        try {
            listService.fillListById(list, requestParamMap, sessionParamMap);
        } catch (Exception e) {
            if (Utils.getLog() != null) {
                Utils.addLog("查询数据异常", e);
            }
        }
        try {
            listService.fillTreeById(list, requestParamMap, sessionParamMap);
        } catch (Exception e) {
            if (Utils.getLog() != null) {
                Utils.addLog("查询树数据异常", e);
            }
        }
        requestContext.getRequestParams().put("data", list);
        requestContext.getRequestParams().put("_SEARCH_ITEM_DISPLAY", requestContext.getParameter("_SEARCH_ITEM_DISPLAY"));

        requestContext.getRequestParams().put("listUrl", requestContext.getContextPath() + "/topezadmin/listEdit/list-" + ENCRYPT_LIST_ID);
        if (Utils.getLog() != null) {
            Utils.addLog("end list_id=" + listId + ",总共耗时：" + (System.currentTimeMillis() - start) + "ms");
        }
    }


    public EzResult importSql(RequestContext requestContext, String method, String listUrlCode) throws Exception {
        return render("layui/edit/import", new HashMap<>());
    }

    public EzResult importlist(RequestContext requestContext, String method, String listUrlCode) throws Exception {
        try {
            String listcode = Utils.trimNull(requestContext.getParameter("listcode"));
            String formcode = Utils.trimNull(requestContext.getParameter("formcode"));
            String fasttext = requestContext.getParameter("listexpress");
            String datasource = requestContext.getParameter("datasource");
            String html = EzBootstrap.config().getEzSqlParser().sqlToList(fasttext, listcode, datasource);
            saveOrUpdate(listcode, listcode, html, datasource);
            if (StringUtils.isNotBlank(formcode)) {
                String htmlform = EzBootstrap.config().getEzSqlParser().sqlToForm(fasttext, formcode, datasource);
                new FormEditController().saveOrUpdate(formcode, formcode, htmlform, datasource);
            }
            EzBootstrap.config().getEzCache().clear();
            return EzResult.instance();
        } catch (Exception e) {
            logger.error("e", e);
            return EzResult.instance().fail().message("SQL错误");
        }
    }
}
