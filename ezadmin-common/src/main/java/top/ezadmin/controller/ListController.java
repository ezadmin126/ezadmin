package top.ezadmin.controller;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ezadmin.EzBootstrap;
import top.ezadmin.common.NotExistException;
import top.ezadmin.common.constants.RequestParamConstants;
import top.ezadmin.common.constants.SessionConstants;
import top.ezadmin.common.utils.*;
import top.ezadmin.service.ListService;
import top.ezadmin.web.EzResult;
import top.ezadmin.web.RequestContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class ListController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(ListController.class);

    private ListService listService = EzProxy.singleInstance(ListService.class);
    /**
     * 列表页面
    * @param requestContext 请求上下文
    * @param method 请求方法
    * @param listUrlCode 列表URL编码
    * @return EzResult 渲染结果
    * @throws Exception 异常
    */
    public EzResult list(RequestContext requestContext, String method, String listUrlCode) throws Exception {
        if (StringUtils.isBlank(listUrlCode)) {
            throw new NotExistException();
        }
        Map<String, Object> requestParamMap = requestContext.getRequestParams();
        String customSearch = Utils.trimNull(requestParamMap.get("customSearch"));

        Map<String, Object> templateParam=new HashMap<>();
        templateParam.put("customSearch", customSearch);

        requestParamMap.putIfAbsent("perPageInt", requestParamMap.get("perPageInt"));
        requestParamMap.putIfAbsent("currentPage", requestParamMap.get("page"));
        requestParamMap.put("vi", requestParamMap.get("vi"));

        requestParamMap.put("ContextPath", requestContext.getContextPath());
        Map<String, String> sessionParamMap = requestContext.getSessionParams();

        Map<String, Object> list = JSONUtils.parseObjectMap(listService.selectPublishListById(listUrlCode));
        if (!Utils.isNotEmpty(list)) {
            logger.warn("找不到列表：{}", listUrlCode);
            throw new NotExistException();
        }
        templateParam.put(RequestParamConstants._SEARCH_ITEM_DISPLAY, requestParamMap.get("_SEARCH_ITEM_DISPLAY"));

        templateParam.put("listUrl", requestContext.getContextPath() + "/topezadmin/list/list-" + listUrlCode);
        templateParam.put("_EZ_SERVER_NAME", "//" + requestContext.getServerName() + ":" + requestContext.getServerPort());
        templateParam.put("cacheFlag", EzBootstrap.config().isSqlCache());
        templateParam.put("customSearchOpen", EzBootstrap.config().isCustomSearchOpen());

        listService.fillListById(list, requestParamMap, sessionParamMap);
        templateParam.put("data", list);
        Map<String, Object> coreMap = (Map<String, Object>) list.get("core");
        String pagetotaltype = "" + coreMap.getOrDefault("pagesync", "0");
        String layout = "" + coreMap.getOrDefault("layout", EzBootstrap.config().getLayout());
        templateParam.put("layout", layout);
        if (StringUtils.equalsIgnoreCase(pagetotaltype, "1")) {
            listService.fillCountById(list, requestParamMap, sessionParamMap);
        }
        String username = Utils.trimNull(sessionParamMap.get(SessionConstants.EZ_SESSION_USER_NAME_KEY));
        templateParam.put("EZ_SESSION_USER_NAME_KEY", username);
        templateParam.putAll(sessionParamMap);
        String adminStyle = Utils.trimNullDefault(coreMap.get(JsoupUtil.ADMINSTYLE), EzBootstrap.config().getAdminStyle());
        String template = Utils.trimNullDefault(coreMap.get(JsoupUtil.TEMPLATE), "list");
        templateParam.put("prefixUrl",EzBootstrap.config().getPrefixUrl());
        String s= adminStyle + "/" + template;
        return render(s,templateParam);
    }

    /**
     * 异步加载列表总数
     * @param requestContext 请求上下文
     * @param method 请求方法
     * @param listUrlCode 列表URL编码
     * @return EzResult 渲染结果
     * @throws Exception
     */
    public EzResult count(RequestContext requestContext, String method, String listUrlCode) throws Exception {
        try {
            if (Utils.getLog() != null) {
                Utils.addLog("start  id=" + listUrlCode);
            }
            Map<String, Object> requestParamMap = requestContext.getRequestParams();
            Map<String, String> sessionParamMap = requestContext.getSessionParams();

            Map<String, Object> list = JSONUtils.parseObjectMap(listService.selectPublishListById(listUrlCode));
            if (Utils.isEmpty(list)) {
                logger.warn("找不到列表：{}", listUrlCode);
                return EzResult.instance().code("500").setMessage("NotExistException");
            }

            long count = listService.fillCountById(list, requestParamMap, sessionParamMap);
            // 这些属性设置可能不再需要，因为使用RequestContext
            Map<String, Object> list2 = new HashMap<>();
            list2.put("page", list.get("page"));


            return EzResult.instance().code("JSON").data( EzResult.instance().data(list2).count(count));
        } catch (Exception e) {
            logger.error("count：{}", listUrlCode, e);
            return EzResult.instance().code("500").setMessage(ExceptionUtils.getFullStackTrace(e));
        }
    }

    /**
     * 树形列表
     *
     * @param requestContext 请求上下文
     * @param method 请求方法
     * @param listUrlCode 列表URL编码
     * @return EzResult 渲染结果
     * @throws Exception
     */
    public EzResult tree(RequestContext requestContext, String method, String listUrlCode) throws Exception {
        String ENCRYPT_LIST_ID = listUrlCode;

        Map<String, Object> requestParamMap = requestContext.getRequestParams();
        if (NumberUtils.toInt("" + requestParamMap.get("perPageInt")) > 5000) {
            requestParamMap.put("perPageInt", 5000);
        }
        requestParamMap.put("ContextPath", requestContext.getContextPath());
        //是否加载数据，由于是异步加载，所以无需加载数据：：treedata.html
        requestParamMap.put("loadDataFlag", 0);
        requestParamMap.put("vi", requestContext.getParameter("vi"));
        Map<String, String> sessionParamMap = requestContext.getSessionParams();

        Map<String, Object> list = JSONUtils.parseObjectMap(listService.selectPublishListById(ENCRYPT_LIST_ID));
        if (Utils.isEmpty(list)) {
            throw new NotExistException();
        }

        listService.fillListById(list, requestParamMap, sessionParamMap);
        Map<String, Object> core = (Map<String, Object>) list.get("core");
        //组装col
        List col = new ArrayList();
        col.add(JSONUtils.parseObjectMap("{\"type\": \"checkbox\" }"));
        List<Map<String, Object>> colList = (List<Map<String, Object>>) list.get("col");
        for (int i = 0; i < colList.size(); i++) {
            String laydata = Utils.getStringByObject(colList.get(i), JsoupUtil.LAYDATA);
            col.add(JSONUtils.parseObjectMap(laydata));
        }
        String btnlaydata = Utils.trimNull(core.get(JsoupUtil.LAYDATA));
        Map m = JSONUtils.parseObjectMap(btnlaydata);
        m.put("title", "操作");
        col.add(m);
        Map<String, Object> templateParam=new HashMap<>();

        templateParam.put("data", list);
        templateParam.put("coldata", JSONUtils.toJSONString(col));

        String layout = "" + core.getOrDefault("layout", EzBootstrap.config().getLayout());
        templateParam.put("cacheFlag", EzBootstrap.config().isSqlCache());
        templateParam.put("layout", layout);
        templateParam.put(RequestParamConstants._SEARCH_ITEM_DISPLAY, requestContext.getParameter("_SEARCH_ITEM_DISPLAY"));
        templateParam.put("listUrl", requestContext.getContextPath() + "/topezadmin/list/tree-" + ENCRYPT_LIST_ID);
        templateParam.putAll(sessionParamMap);
        templateParam.put("_EZ_SERVER_NAME", "//" + requestContext.getServerName() + ":" + requestContext.getServerPort());
        templateParam.put("prefixUrl",EzBootstrap.config().getPrefixUrl());
        String adminStyle = Utils.trimNullDefault(core.get(JsoupUtil.ADMINSTYLE), EzBootstrap.config().getAdminStyle());
        return render(adminStyle + "/listtree",templateParam);
    }

    /**
     * 异步加载树形列表数据
     *
     * @param requestContext 请求上下文
     * @param method 请求方法
     * @param listUrlCode 列表URL编码
     * @return EzResult 渲染结果
     * @throws Exception
     */
    public EzResult treedata(RequestContext requestContext, String method, String listUrlCode) throws Exception {
        String listId = listUrlCode;
        String ENCRYPT_LIST_ID = listUrlCode;

        if (Utils.getLog() != null) {
            Utils.addLog("开始执行列表 list_id=" + listId);
        }
        Map<String, Object> requestParamMap = requestContext.getRequestParams();
        Map<String, String> sessionParamMap = requestContext.getSessionParams();

        String sessionUserId = Utils.trimNull(sessionParamMap.get(SessionConstants.EZ_SESSION_USER_ID_KEY));
        sessionParamMap.put(SessionConstants.EZ_SESSION_USER_ID_KEY, sessionUserId);
        Map<String, Object> list = JSONUtils.parseObjectMap(listService.selectPublishListById(ENCRYPT_LIST_ID));
        if (Utils.isEmpty(list)) {
            throw new NotExistException();
        }
        requestParamMap.put("perPageInt", 10000);
        listService.fillTreeById(list, requestParamMap, sessionParamMap);
        Map<String, Object> core = (Map<String, Object>) list.get("core");
        return EzResult.instance().code("JSON").data(EzResult.instance().data(core.get("dataList")));
    }

    /**
     * 打印sql
     *
     * @param requestContext 请求上下文
     * @param method 请求方法
     * @param listUrlCode 列表URL编码
     * @return EzResult 渲染结果
     * @throws Exception
     */
    public EzResult trace(RequestContext requestContext, String method, String listUrlCode) throws Exception {
        Utils.initLog();
        try {
            String listId = listUrlCode;
            String ENCRYPT_LIST_ID = listUrlCode;
            long start = System.currentTimeMillis();
            if (Utils.getLog() != null) {
                Utils.addLog("start ID=" + ENCRYPT_LIST_ID);
            }

            Map<String, Object> requestParamMap = requestContext.getRequestParams();


            Map<String, String> sessionParamMap = requestContext.getSessionParams();

            if (Utils.getLog() != null) {
                Utils.addLog("requestParamMap " + requestParamMap);
            }
            if (Utils.getLog() != null) {
                Utils.addLog("sessionParamMap " + JSONUtils.toJSONString(sessionParamMap));
            }


            Map<String, Object> list = new HashMap<>();
            if (StringUtils.isNotBlank(ENCRYPT_LIST_ID)) {
                try {
                    list = JSONUtils.parseObjectMap(listService.selectPublishListById(ENCRYPT_LIST_ID));
                    if (Utils.isEmpty(list)) {
                        throw new NotExistException();
                    }
                } catch (Exception e) {
                    if (Utils.getLog() != null) {
                        Utils.addLog("加载列表异常", e);
                    }
                }
            }
            try {
                Utils.addLog("打印countSQL ");
                listService.fillCountById(list, requestParamMap, sessionParamMap);
            } catch (Exception e) {
                if (Utils.getLog() != null) {
                    Utils.addLog("加载总数异常", e);
                }
            }
            try {
                Utils.addLog("打印tree SQL ");
                listService.fillTreeById(list, requestParamMap, sessionParamMap);
            } catch (Exception e) {
                if (Utils.getLog() != null) {
                    Utils.addLog("查询树数据异常", e);
                }
            }
            EzResult result = list(requestContext, "list", listUrlCode);
            result.data("log", JSONUtils.toJSONString(Utils.getLog()));
            return result;
        }finally {
            Utils.clearLog();
        }
    }

    /**
     * 列表api 接口数据
     *
     * @param requestContext 请求上下文
     * @param method 请求方法
     * @param listUrlCode 列表URL编码
     * @return EzResult 渲染结果
     * @throws Exception
     */
    public EzResult api(RequestContext requestContext, String method, String listUrlCode) throws Exception {
        String ENCRYPT_LIST_ID = listUrlCode;
        Map<String, Object> requestParamMap = requestContext.getRequestParams();
        Map<String, String> sessionParamMap = requestContext.getSessionParams();
        Map<String, Object> list = JSONUtils.parseObjectMap(listService.selectPublishListById(ENCRYPT_LIST_ID));
        if (Utils.isEmpty(list)) {
            throw new NotExistException();
        }
        listService.fillListById(list, requestParamMap, sessionParamMap);
        Map<String, Object> coreMap = (Map<String, Object>) list.get("core");
        List<Map<String, Object>> dataList = (List<Map<String, Object>>) coreMap.get("dataList");
        if (dataList != null) {
            dataList.forEach(item -> {
                item.remove("tds");
                item.remove("rowjson");
            });
        } else {
            dataList = new ArrayList<>();
        }
        Map<String, Object> result=new HashMap<>();
        result.put("body",dataList);
        result.put("head",list.get("col"));
        return EzResult.instance().code("JSON").data(EzResult.instance().data(result).count(dataList.size()));
    }

    /**
     * 选择列
     * @param requestContext 请求上下文
     * @param method 请求方法
     * @param listUrlCode 列表URL编码
     * @return EzResult 渲染结果
     * @throws Exception
     */
    public EzResult selectCols(RequestContext requestContext, String method, String listUrlCode) throws Exception {
        String ENCRYPT_LIST_ID = listUrlCode;
        Map<String, Object> requestParamMap = requestContext.getRequestParams();

        requestParamMap.put("default_empty", "1");
        Map<String, Object> list = JSONUtils.parseObjectMap(listService.selectPublishListById(ENCRYPT_LIST_ID));
        if (Utils.isEmpty(list)) {
            throw new NotExistException();
        }
        List<Map<String, Object>> searchList = (List<Map<String, Object>>) list.get("search");
        List<Map<String, Object>> colList = (List<Map<String, Object>>) list.get("col");
        Map<String, Object> templateParam=new HashMap<>();

        templateParam.put("fromSearchField", searchList);
        templateParam.put("fromColField", colList);
        templateParam.put("IS_DEBUG", requestContext.getParameter("IS_DEBUG"));
        templateParam.put("_EZ_SERVER_NAME", "//" + requestContext.getServerName() + ":" + requestContext.getServerPort());
        return render(EzBootstrap.config().getAdminStyle() + "/custom_cols_cache", templateParam);
     }

    /**
     * 自定义搜索
     * @param requestContext 请求上下文
     * @param method 请求方法
     * @param listUrlCode 列表URL编码
     * @return EzResult 渲染结果
     * @throws Exception
     */
    public EzResult customSearch(RequestContext requestContext, String method, String listUrlCode) throws Exception {
        String ENCRYPT_LIST_ID = listUrlCode;
        String config = Utils.trimNull(requestContext.getParameter("config"));
        Map<String, Object> requestParamMap = requestContext.getRequestParams();
        requestParamMap.put("default_empty", "1");
        Map<String, String> sessionParamMap = requestContext.getSessionParams();

        Map<String, Object> list = JSONUtils.parseObjectMap(listService.selectPublishListById(ENCRYPT_LIST_ID));
        if (Utils.isEmpty(list)) {
            throw new NotExistException();
        }
        listService.fillListById(list, requestParamMap, sessionParamMap);

        List<Map<String, Object>> searchList = (List<Map<String, Object>>) list.get("search");
        List<Map<String, Object>> colList = (List<Map<String, Object>>) list.get("col");
        List<Map<String, Object>> searchListR = searchList.stream().filter(s ->
                        s.get("type").equals("numberrange")
                                || s.get("type").equals("input-text")
                                || s.get("type").equals("select")
                                || s.get("type").equals("select-search")
                                || s.get("type").equals("xmselect")
                                || s.get("type").equals("daterange")
                ) // 筛选条件：性别为男性
                .collect(Collectors.toList());

        List<Map<String, Object>> colListR = colList.stream().filter(s ->
                        s.get("order").equals("1")
                ) // 筛选条件：性别为男性
                .collect(Collectors.toList());
                Map<String, Object> templateParam=new HashMap<>();

        templateParam.put("fromSearchField", searchListR);
        templateParam.put("fromColField", colListR);
        templateParam.put("data", list);
        templateParam.put("config", config);
        templateParam.put("IS_DEBUG", requestContext.getParameter("IS_DEBUG"));
        templateParam.put("_EZ_SERVER_NAME", "//" + requestContext.getServerName() + ":" + requestContext.getServerPort());
        return render(EzBootstrap.config().getAdminStyle() + "/custom_search", templateParam);
    }
 

}
