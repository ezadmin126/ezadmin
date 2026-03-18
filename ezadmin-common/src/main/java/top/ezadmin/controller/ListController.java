package top.ezadmin.controller;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ezadmin.EzBootstrap;
import top.ezadmin.common.NotExistException;
import top.ezadmin.common.constants.RequestParamConstants;
import top.ezadmin.common.constants.SessionConstants;
import top.ezadmin.common.enums.DefaultParamEnum;
import top.ezadmin.common.enums.JdbcTypeEnum;
import top.ezadmin.common.enums.OrderEnum;
import top.ezadmin.common.enums.ParamNameEnum;
import top.ezadmin.common.utils.*;
import top.ezadmin.dao.Dao;
import top.ezadmin.dao.model.CustomSearchDTO;
import top.ezadmin.dao.model.CustomSearchOrder;
import top.ezadmin.dao.model.TreeConfig;
import top.ezadmin.plugins.export.EzExportResult;
import top.ezadmin.plugins.express.executor.ListExpressExecutor;
import top.ezadmin.service.ListService;
import top.ezadmin.web.EzResult;
import top.ezadmin.web.RequestContext;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
        templateParam.put("ENCRYPT_LIST_ID",listUrlCode);
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
        // 支持一维和二维数组格式
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

        // 支持一维和二维数组格式

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


    public EzResult page(RequestContext requestContext, String method, String id) throws Exception {
        Map<String, Object> templateParam=new HashMap<>();
        // 使用统一加载器（文件优先，数据库降级）
        top.ezadmin.dao.dto.DslConfig dslConfig = DslLoader.loadDsl(id, "list");
        if (dslConfig == null) {
           return render(EzBootstrap.config().getAdminStyle() + "/404", templateParam);
        }
        Map<String, Object> list = dslConfig.getConfig();
        initSearch(requestContext,list);
        initMenu(requestContext,list);
        initTree(requestContext,list);
        Collection<String> tdtemplates=initTd(requestContext,list);
        if(list.get("initApi") == null){
            list.put("initApi", EzBootstrap.config().getPrefixUrl()+"/list/data-" + id);
        }
        if(list.get("countApi") == null){
            list.put("countApi", EzBootstrap.config().getPrefixUrl()+"/list/countpage-" + id);
        }
        //默认你不隐藏头部
        if(list.get("hideSearch") == null){
            list.put("hideSearch", false);
        }

        initRowBtn(list);
        templateParam.put("list", list);
        templateParam.put("perPageInt", list.get("body") != null ? ((Map<String, Object>)list.get("body")).get("limit") : 10);
        templateParam.put("ENCRYPT_LIST_ID", id);
        templateParam.put("cacheFlag",EzBootstrap.config().isSqlCache());
        templateParam.put("tdTemplates", tdtemplates);
        templateParam.put("requestContext",requestContext);
        templateParam.put("downloadUrl",EzBootstrap.config().getDownloadUrl());
        templateParam.putAll(EzBootstrap.config().getConfig());

        templateParam.put("EZ_SESSION_USER_NAME_KEY", Utils.trimNull(requestContext.getSessionParams().get(SessionConstants.EZ_SESSION_USER_NAME_KEY)));
        templateParam.put("EZ_SESSION_USER_ID_KEY", Utils.trimNull(requestContext.getSessionParams().get(SessionConstants.EZ_SESSION_USER_ID_KEY)));

        return render("layui/dsl/listTemplate",templateParam);
    }

    private void initMenu(RequestContext requestContext, Map<String, Object> list) {
        if(list.get("menu") != null){
            listService.initComponentData(requestContext, (Map<String, Object>) list.get("menu"));
        }
    }
    private void initTree(RequestContext requestContext, Map<String, Object> list) {
        if(list.get("tree") != null){
            listService.initComponentData(requestContext, (Map<String, Object>) list.get("tree"));
        }
    }



    public EzResult data(RequestContext requestContext, String method, String id) throws Exception {

        // 使用统一加载器（文件优先，数据库降级）
        top.ezadmin.dao.dto.DslConfig dslConfig = DslLoader.loadDsl(id, "list");
        if (dslConfig == null) {
            return EzResult.instance().code("JSON").data(EzResult.instance().code("404") );
        }
        Map<String, Object> list = dslConfig.getConfig();
        String select_express = Utils.expressToString( ((Map<String, Object>)list.get("express")).get("main"));
        String orderBy = Utils.expressToString(((Map<String, Object>)list.get("express")).get("orderBy"));
        String groupBy = Utils.expressToString(((Map<String, Object>)list.get("express")).get("groupBy"));
        String count_express = Utils.expressToString(((Map<String, Object>)list.get("express")).get("count"));
        //兼容老设计
        list.put("orderby_express",orderBy);
        list.put("count_express",count_express);
        list.put("col", list.get("column"));
        list.put("core",list.get("body"));


        List<Map<String, Object>> columnList = (List<Map<String, Object>>) list.get("column");
        columnList.forEach(item->{
            //兼容老设计
            item.put("type",item.get("component"));
        });

        // 支持一维和二维数组格式
        List<List<Map<String, Object>>> searchList = getFlattenedSearchList(list.get("search"));
        fillinitRequestValue(searchList,requestContext.getRequestParams());

      //  Page page = new Page(requestContext.getRequestParams());
        int perPageInt = list.get("body") != null ? Utils.toInt(Utils.trimNull(((Map<String, Object>)list.get("body")).get("limit"))) : 10;
        if( !requestContext.getRequestParams().containsKey("perPageInt")){
            requestContext.getRequestParams().put("perPageInt", perPageInt);
        }
        if(list.get("tree")!=null
                &&Utils.isNotEmpty((Map<String, Object>)list.get("tree"))
        ){
            //tree最多支持10000条数据
            requestContext.getRequestParams().put("perPageInt", 10000);
        }

       Page page = loadingPage(list, requestContext.getRequestParams());

        ListExpressExecutor listExpressExecutor = ListExpressExecutor.createInstance();
        listExpressExecutor.datasource(EzBootstrap.getInstance().getDataSourceByKey(list.get("dataSource")))
                .express(select_express)
                .page(page);
        //兼容老设计
        listExpressExecutor.getOperatorParam().setListDto(list);

        //计算group by
        String group = excuteGroup(groupBy, requestContext.getRequestParams(), requestContext.getSessionParams());

        listExpressExecutor.addParam("_CHECKD_IDS", Utils.getStringByObject(requestContext.getRequestParams(), "_CHECKD_IDS"));
        listExpressExecutor.addParam("EZ_SUM_FLAG", Utils.getStringByObject(requestContext.getRequestParams(), "EZ_SUM_FLAG"));
        listExpressExecutor.addParam("GROUP_BY", group);
        page.setGroupBy(group);
        listExpressExecutor.addSessionParam(requestContext.getSessionParams());
        listExpressExecutor.addRequestParam(requestContext.getRequestParams());
        List<Map<String, Object>> dataList = (List<Map<String, Object>>) listExpressExecutor.execute();
        //查询总数
        long count =page.getPerPageInt();
        if(list.get("tree")!=null
                &&Utils.isNotEmpty((Map<String, Object>)list.get("tree"))
        ){
           // 从请求参数中获取树搜索关键词
            Map<String, Object> customNameMap = (Map<String, Object>) ((Map<?, ?>) list.get("tree")) .get("customName");
            TreeConfig config=new TreeConfig();
            if(customNameMap != null){
                config.setTreeId(Utils.trimNull(customNameMap.get("id")));
                config.setTreePid(Utils.trimNull(customNameMap.get("pid")));
                config.setTreeLabel(Utils.trimNull(customNameMap.get("name")));
                config.setTreeChildren(Utils.trimNull(customNameMap.get("children")));
                config.setTreeIsParent(Utils.trimNull(customNameMap.get("isParent")));
            }
            //取data配置里面的rootPid
            Map<String, Object> dataMap = (Map<String, Object>) ((Map<?, ?>) list.get("tree")) .get("data");
            String rootPid ="";
            if(dataMap != null){
                config.setRootPid(Utils.trimNull(dataMap.get("rootPid")));
            }
           String treeSearchKeyword = Utils.trimNull(requestContext.getParameter(config.getTreeLabel()));
           List<Map<String, Object>> rr=Utils.flatLabelValueTree(dataList,"","",
                   treeSearchKeyword,
                   config);
            return EzResult.instance().code("JSON").count(count).data(EzResult.instance().count(rr.size()).data(rr));
        }else{
            if(Utils.isTrue(list.get("pagesync"))){
                count= getDataCountByListId(EzBootstrap.getInstance().getDataSourceByKey(list.get("dataSource"))
                        , list,  requestContext.getRequestParams(), requestContext.getSessionParams());
            }
        }
        return EzResult.instance().code("JSON").count(count).data(EzResult.instance().count(count).data(dataList));
    }

    public EzResult countpage(RequestContext requestContext, String method, String id) throws Exception {

        // 使用统一加载器（文件优先，数据库降级）
        top.ezadmin.dao.dto.DslConfig dslConfig = DslLoader.loadDsl(id, "list");
        if (dslConfig == null) {
            return EzResult.instance().code("JSON").data(EzResult.instance().code("404") );
        }
        Map<String, Object> list = dslConfig.getConfig();
        String select_express = Utils.expressToString( ((Map<String, Object>)list.get("express")).get("main"));
        String orderBy = Utils.expressToString(((Map<String, Object>)list.get("express")).get("orderBy"));
        String groupBy = Utils.expressToString(((Map<String, Object>)list.get("express")).get("groupBy"));
        String count_express = Utils.expressToString(((Map<String, Object>)list.get("express")).get("count"));
        //兼容老设计
        list.put("orderby_express",orderBy);
        list.put("count_express",count_express);
        list.put("col", list.get("column"));
        list.put("core",list.get("body"));


        List<Map<String, Object>> columnList = (List<Map<String, Object>>) list.get("column");
        columnList.forEach(item->{
            //兼容老设计
            item.put("type",item.get("component"));
        });

        // 支持一维和二维数组格式
        List<List<Map<String, Object>>> searchList = getFlattenedSearchList(list.get("search"));
        fillinitRequestValue(searchList,requestContext.getRequestParams());

        //  Page page = new Page(requestContext.getRequestParams());
        int perPageInt = list.get("body") != null ? Utils.toInt(Utils.trimNull(((Map<String, Object>)list.get("body")).get("limit"))) : 10;
        if( !requestContext.getRequestParams().containsKey("perPageInt")){
            requestContext.getRequestParams().put("perPageInt", perPageInt);
        }
        if(list.get("tree")!=null
                &&Utils.isNotEmpty((Map<String, Object>)list.get("tree"))
        ){
            //tree最多支持10000条数据
            requestContext.getRequestParams().put("perPageInt", 10000);
        }

        Page page = loadingPage(list, requestContext.getRequestParams());

        ListExpressExecutor listExpressExecutor = ListExpressExecutor.createInstance();
        listExpressExecutor.datasource(EzBootstrap.getInstance().getDataSourceByKey(list.get("dataSource")))
                .express(select_express)
                .page(page);
        //兼容老设计
        listExpressExecutor.getOperatorParam().setListDto(list);

        //计算group by
        String group = excuteGroup(groupBy, requestContext.getRequestParams(), requestContext.getSessionParams());

        listExpressExecutor.addParam("_CHECKD_IDS", Utils.getStringByObject(requestContext.getRequestParams(), "_CHECKD_IDS"));
        listExpressExecutor.addParam("EZ_SUM_FLAG", Utils.getStringByObject(requestContext.getRequestParams(), "EZ_SUM_FLAG"));
        listExpressExecutor.addParam("GROUP_BY", group);
        page.setGroupBy(group);
        listExpressExecutor.addSessionParam(requestContext.getSessionParams());
        listExpressExecutor.addRequestParam(requestContext.getRequestParams());
        List<Map<String, Object>> dataList =new ArrayList<>();
        //查询总数
        long count =0;
        if(list.get("tree")!=null
                &&Utils.isNotEmpty((Map<String, Object>)list.get("tree"))
        ){
            // 从请求参数中获取树搜索关键词
            Map<String, Object> customNameMap = (Map<String, Object>) ((Map<?, ?>) list.get("tree")) .get("customName");
            TreeConfig config=new TreeConfig();
            if(customNameMap != null){
                config.setTreeId(Utils.trimNull(customNameMap.get("id")));
                config.setTreePid(Utils.trimNull(customNameMap.get("pid")));
                config.setTreeLabel(Utils.trimNull(customNameMap.get("name")));
                config.setTreeChildren(Utils.trimNull(customNameMap.get("children")));
                config.setTreeIsParent(Utils.trimNull(customNameMap.get("isParent")));
            }
            //取data配置里面的rootPid
            Map<String, Object> dataMap = (Map<String, Object>) ((Map<?, ?>) list.get("tree")) .get("data");
            String rootPid ="";
            if(dataMap != null){
                config.setRootPid(Utils.trimNull(dataMap.get("rootPid")));
            }
            String treeSearchKeyword = Utils.trimNull(requestContext.getParameter(config.getTreeLabel()));
            List<Map<String, Object>> rr=Utils.flatLabelValueTree(dataList,"","",
                    treeSearchKeyword,
                    config);
            return EzResult.instance().code("JSON").count(count).data(EzResult.instance().count(rr.size()).data(rr));
        }else{
            count= getDataCountByListId(EzBootstrap.getInstance().getDataSourceByKey(list.get("dataSource"))
                    , list,  requestContext.getRequestParams(), requestContext.getSessionParams());
        }
        return EzResult.instance().code("JSON").count(count).data(EzResult.instance().count(count).data(dataList));
    }

    //简单并发拦截
    private final Map<String, String> existList = new ConcurrentHashMap<String, String>();
    public EzResult exportpage(RequestContext requestContext, String method, String listUrlCode) throws Exception {
        String ENCRYPT_LIST_ID =listUrlCode;
        String _BLANK_PARAM_COLUMN = Utils.trimNull(requestContext.getParameter("_BLANK_PARAM_COLUMN"));
        String EZ_PER_PAGE_SIZE = Utils.trimEmptyDefault(requestContext.getParameter("EZ_PER_PAGE_SIZE"), "1000");
//        String orderedColumn[] = null;
//        if (StringUtils.isNotBlank(_BLANK_PARAM_COLUMN)) {
//            orderedColumn = _BLANK_PARAM_COLUMN.split(",");
//        }
        String key = ENCRYPT_LIST_ID + "_" + ENCRYPT_LIST_ID;
        try {
            String sessionUserId = Utils.trimNull(requestContext.getSessionParams().get(SessionConstants.EZ_SESSION_USER_ID_KEY));
            String username = Utils.trimNull(requestContext.getSessionParams().get(SessionConstants.EZ_SESSION_USER_NAME_KEY));

            if (existList.containsKey(key)) {
                return EzResult.instance().fail().message(key + "导出失败：当前已有人员" + existList.get(key) + "正在导出此列表，请等待对方先导出完成。");
            } else {
                existList.put(key, sessionUserId + username );
            }
            logger.info("start export html list_id= {}" , ENCRYPT_LIST_ID);
            Map<String, Object> requestParamMap =requestContext.getRequestParams();
            Map<String, String> sessionParamMap = requestContext.getSessionParams();

            requestParamMap.put("currentPage", "1");
            requestParamMap.put("perPageInt", EZ_PER_PAGE_SIZE);

            // 使用统一加载器（文件优先，数据库降级）
            top.ezadmin.dao.dto.DslConfig dslConfig = DslLoader.loadDsl(ENCRYPT_LIST_ID, "list");
            if (dslConfig == null) {
                return EzResult.instance().fail().message("列表配置不存在: " + ENCRYPT_LIST_ID);
            }
            Map<String, Object> list = dslConfig.getConfig();
            initTd(requestContext,list);
            List<Map<String, Object>> colList = (List<Map<String, Object>>) list.get("column");

            logger.info("start finish load ez   list_id=" + ENCRYPT_LIST_ID);

            List<List<Object>> data = new ArrayList<>();
            //第一页的
            EzResult pageData=((EzResult)data(requestContext, method, ENCRYPT_LIST_ID).getData());

            List<Map<String, Object>> dataList = (List<Map<String, Object>>) pageData.getData();

            fillExcelData(data,dataList, colList);

            int currentPage = 2;
            while (Utils.isNotEmpty(dataList) && dataList.size() >= NumberUtils.toInt(EZ_PER_PAGE_SIZE)) {
                requestParamMap.put("currentPage", currentPage++);
                EzResult pageDataTemp=((EzResult)data(requestContext, method, ENCRYPT_LIST_ID).getData());
                List<Map<String, Object>> dataListTemp = (List<Map<String, Object>>) pageDataTemp.getData();
                fillExcelData(data, dataListTemp, colList);
                dataList = dataListTemp;
            }
            List<List<String>> head = new ArrayList<List<String>>();
            List<String> head0 = new ArrayList<String>();
            head0.add("序号");
            head0.add("");
            head0.add("");
            head.add(head0);
            for (int i = 0; i < colList.size(); i++) {
                Map<String, Object> col = (Map<String, Object>) colList.get(i);
                if (StringUtils.equalsIgnoreCase(col.get(JsoupUtil.SPECIALCOL) + "", "1")
                ) {
                    continue;
                }
                List<String> head1 = new ArrayList<String>();
                //文案
                head1.add(Utils.getStringByObject(col, JsoupUtil.LABEL));
                //宽度
                head1.add(Utils.getStringByObject(col, JsoupUtil.WIDTH));
                //字段类型
                head1.add(Utils.getStringByObject(col, JsoupUtil.JDBCTYPE));
                head.add(head1);
            }
            //  log.info("ezadmin start export {} {} {} {}",sessionUserId,ip, Utils.getStringByObject(coreMap,"listname"));
            String fileName = Utils.trimNull(requestParamMap.get("EXPORT_FILE_NAME"));

            EzExportResult bb= EzBootstrap.config().getEzExport().export(Utils.trimEmptyDefault(fileName,Utils.getStringByObject(list, "name")), head, data);
            if (StringUtils.isBlank(fileName)) {
                fileName = bb.getFileName();
            }
            if (StringUtils.isBlank(fileName)) {
                fileName = Utils.getStringByObject(list, "name");
            }
            return EzResult.instance().success().dataMap("html",bb.getFile())
                    .dataMap("contentType",bb.getContentType())
                    .dataMap("fileName",fileName).code("EXPORT");
        } catch (Exception e) {
            logger.error("", e);
            return EzResult.instance().fail().message("导出失败：" + e.getMessage());
        } finally {
            existList.remove(key);
        }
    }

    private void fillExcelData(List<List<Object>> data, List<Map<String, Object>> dataList, List<Map<String, Object>> colList){
        if (Utils.isEmpty(dataList)) {
            return;
        }
        for (int i = 0; i < dataList.size(); i++) {
            List<Object> row = new ArrayList<>();
            row.add(data.size() + 1);
            //当前行的数据
            Map<String, Object> rowData = dataList.get(i);
            //需要转换成 展示的数据
            List<String> rowCol=new ArrayList<>();
            colList.forEach(col -> {
                String item_name = (String) col.get("item_name");
                String component = (String) col.get("component");
                String rowValue=Utils.trimNull(rowData.get(item_name));

                List<Map<String, Object>> configData = (List<Map<String, Object>>) col.get("data");//value,label 格式

                if (StringUtils.equalsIgnoreCase(component, "tdSelect")
                    ||StringUtils.equalsIgnoreCase(component, "tdSelectMultiple")
                ) {
                    List<String> values = Arrays.asList(rowValue.split(","));
                    //找到 configData里面 value 为 rowValue 的项 ，提取所有label
                    List<String> rowLabels = configData.stream().filter(item ->
                                            values.contains(Utils.trimNull(item.get("value")))
                                   ).map(item -> (String) item.get("label"))
                                   .collect(Collectors.toList());
                    rowCol.add(StringUtils.join(rowLabels,","));
                } else if(StringUtils.equalsIgnoreCase(component, "tdPic")){
                    rowCol.add(EzBootstrap.config().getDownloadUrl()+rowValue);
                } else {
                    rowCol.add(rowValue);
                }
            });
            //添加一行
            row.addAll(rowCol);
            data.add(row);
        }
    }




    public void initRowBtn(Map<String, Object> list) {
        List<Map<String, Object>> columnList = (List<Map<String, Object>>) list.get("rowButton");
        List<Map<String, Object>> normal = new ArrayList<>();
        List<Map<String, Object>> bread = new ArrayList<>();
        List<Map<String, Object>> dropdown = new ArrayList<>();
        columnList.forEach(item->{
            if(item.get("component")==null||StringUtils.isBlank((String) item.get("component"))){
                normal.add(item);
                return;
            }
            if(item.get("component").equals("button-bread")){
                bread.add(item);
            }else if(item.get("component").equals("button-dropdown")){
                dropdown.add(item);
            }else{
                normal.add(item);
            }
        });
        list.put("rowButtonNormal", normal);
        list.put("rowButtonBread", bread);
        list.put("rowButtonDropdown", dropdown);
    }

    public Collection<String> initTd(RequestContext requestContext,Map<String, Object> list  ) {
        List<Map<String, Object>> columnList = (List<Map<String, Object>>) list.get("column");
        Map<String,Object> body=(Map<String,Object>) list.get("body");
        Map<String,String> tdTemplates=new HashMap<>();
        columnList.forEach(item->{
            Map<String,Object> props=(Map<String,Object>) item.get("props");
            if(props==null){
                props=new HashMap<>();;
            }
            String emptyShow = (String) props.get("emptyShow");
            props.put("emptyShow", Utils.trimEmptyDefault(emptyShow,Utils.trimNull(body.get("emptyShow"))));
            item.put("propsJson", JSONUtils.toJSONString(item.get("props")));
            //props不为空
            item.put("props", props);
            Map<String, Object> initData = (Map<String, Object>) item.get("initData");
            listService.initComponentData(requestContext, item);

            try {
                String comp=Utils.trimEmptyDefault(item.get("component"), "tdText");
                // component 不为空
                item.put("component", comp);
                if(!tdTemplates.containsKey(comp)){
                    String temp=Resources.getResourceAsString("topezadmin/config/layui/dsl/component/"+comp+".html");
                    tdTemplates.put(comp,temp);
                }
            } catch (IOException e) {
                logger.warn("加载组件失败 {} {}", item.get("component"),"topezadmin/config/layui/dsl/component/"+item.get("component")+".html");
            }
        });
        return tdTemplates.values();
    }

    public void initSearch(RequestContext requestContext,Map<String, Object> list) {
        Object searchObj = list.get("search");
        // 支持一维和二维数组格式
        List<List<Map<String, Object>>> searchList = getFlattenedSearchList(searchObj);
        if (searchList == null) {
            return;
        }
        searchList.forEach(row->{
            row.forEach(search->{
                if (search != null) {
                    listService.initComponentData(requestContext, search);
                    // 默认 classAppend：每行4列
                    if(search.get("classAppend") == null){
                        search.put("classAppend", "layui-col-sm6 layui-col-md4 layui-col-lg4 layui-col-xl3");
                    }
                }
            });
        });
    }

    /**
     * 获取扁平化的搜索字段列表（支持新的对象数组格式）
     * @param searchObj search 对象
     * @return 扁平化的搜索字段列表
     */
    private List<List<Map<String, Object>>> getFlattenedSearchList(Object searchObj) {
        try {
            if (!(searchObj instanceof List)) {
                return null;
            }
            List<Object> list = (List<Object>) searchObj;
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

            logger.error("Search配置错误，应该使用对象数组格式: [{\"row\":[...]}, {\"row\":[...]}]");
            return null;
        } catch (Exception e) {
            logger.error("Search配置错误，请检查格式{}", searchObj, e);
        }
        return null;
    }

    private String excuteGroup(String group, Map<String, Object> request, Map<String, String> session) {
        try {
            String groupByExpress =group;
            //兼容老的 group by
            if (StringUtils.startWithTrimAndLower(groupByExpress, "group ")) {
                return Utils.trimNull(groupByExpress);
            }
            ListExpressExecutor groupExe = ListExpressExecutor.createInstance();
            groupExe.getOperatorParam().setListDto(null);
            groupExe.express(groupByExpress);
            groupExe.addSessionParam(session);
            groupExe.addRequestParam(request);
            Object obj = groupExe.executeCount();
            return Utils.trimNull(obj);
        } catch (Exception e) {
            logger.error("EZADMIN   group by 配置错误   ,", e);
            return " ";
        }
    }
    private void fillinitRequestValue(List<List<Map<String, Object>>> searchList, Map<String, Object> requestParamMap) {
        if (Utils.isEmpty(searchList)) {
            return;
        }
        for (int j = 0; j < searchList.size(); j++) {
            List<Map<String, Object>> searchListRow = searchList.get(j);
            for (int i = 0; i < searchListRow.size(); i++) {
                Map<String, Object> search = searchListRow.get(i);
                Map<String, Object> props =search.get("props")==null?new HashMap<String, Object>(): (Map<String, Object>) search.get("props");
                String currentItemname = Utils.trimNull(search.get(JsoupUtil.ITEM_NAME));
                String component = Utils.trimEmptyDefault(search.get("component"), "input");
                String orgValue = Utils.trimNull(requestParamMap.get(currentItemname));
                //兼容
                search.put(JsoupUtil.OPER, Utils.trimNull(search.get("operator")));
                search.put(JsoupUtil.TYPE, component);
                String newJdbcType=Utils.trimEmptyDefault(search.get("jdbcType"), JdbcTypeEnum.VARCHAR.getName());
                search.putIfAbsent(JsoupUtil.JDBCTYPE, newJdbcType);
                search.put(ParamNameEnum.itemParamValue.getName(), orgValue);
                search.put(ParamNameEnum.itemParamValueStart.getName(), Utils.trimNull(requestParamMap.get(currentItemname + "_START")));
                search.put(ParamNameEnum.itemParamValueEnd.getName(), Utils.trimNull(requestParamMap.get(currentItemname + "_END")));
                //联动日期区间  -
                if (component.equalsIgnoreCase("date")
                        && Utils.isTrue(props.get("range"))
                        && StringUtils.isNotBlank(orgValue)) {
                    //默认datetime
                    search.put(JsoupUtil.JDBCTYPE, JdbcTypeEnum.DATETIME.getName());
                    orgValue = DefaultParamEnum.getValue(orgValue);
                    String[] valueSplit = orgValue.split(" - ");
                    search.put(currentItemname, orgValue);
                    search.put(ParamNameEnum.itemParamValue.getName(), orgValue);
                    if (valueSplit.length == 2) {
                        search.put(ParamNameEnum.itemParamValueStart.getName(), valueSplit[0]);
                        search.put(ParamNameEnum.itemParamValueEnd.getName(), valueSplit[1]);

                    }
                }

                search.put(ParamNameEnum.itemParamOrderValue.getName(), Utils.trimNull(requestParamMap.get(currentItemname + "_ORDER")));

                search.put(ParamNameEnum.itemSearchKey.getName(), Utils.getStringByObject(requestParamMap, "itemSearchKey"));
                search.put(ParamNameEnum.itemSearchValue.getName(), Utils.getStringByObject(requestParamMap, "itemSearchValue"));
                search.put(ParamNameEnum.itemSearchConcatValue.getName(), Utils.getStringByObject(requestParamMap, "itemSearchConcatValue"));
                search.put(ParamNameEnum.itemSearchDateKey.getName(), Utils.getStringByObject(requestParamMap, "itemSearchDateKey"));


                String start = DefaultParamEnum.getValue(Utils.trimNull(Utils.getStringByObject(requestParamMap, "itemSearchDateValueStart")));
                String end = DefaultParamEnum.getValue(Utils.trimNull(Utils.getStringByObject(requestParamMap, "itemSearchDateValueEnd")));
                search.put(ParamNameEnum.itemSearchDateValueStart.getName(), start);
                search.put(ParamNameEnum.itemSearchDateValueEnd.getName(), end);

                //联动日期区间  -
                String itemSearchDateValue = Utils.trimNull(requestParamMap.get("itemSearchDateValue"));
                if (StringUtils.isNotBlank(itemSearchDateValue)) {
                    itemSearchDateValue = DefaultParamEnum.getValue(itemSearchDateValue);
                    search.put("itemSearchDateValue", itemSearchDateValue);
                    String[] valueSplit = itemSearchDateValue.split(" - ");
                    search.put(currentItemname, itemSearchDateValue);
                    search.put(ParamNameEnum.itemParamValue.getName(), orgValue);
                    search.put(ParamNameEnum.itemSearchDateValueStart.getName(), valueSplit[0]);
                    search.put(ParamNameEnum.itemSearchDateValueEnd.getName(), valueSplit[1]);
                }
            }

            for (int i = 0; i < searchListRow.size(); i++) {
                Map<String, Object> search = searchListRow.get(i);
                String currentItemname = Utils.trimNull(search.get(JsoupUtil.ITEM_NAME));
                List<Map<String, Object>> childsearchList = chilrenByName(currentItemname, searchListRow);
                //用于生成sql
                search.put("children", childsearchList);
            }
        }
    }

    private List<Map<String, Object>> chilrenByName(String item_name, List<Map<String, Object>> searchList) {
        if (!StringUtils.contains(item_name, ","))//多字段逗号分隔{
        {
            return new ArrayList<Map<String, Object>>();
        }
        List<Map<String, Object>> itemNameList = new ArrayList<Map<String, Object>>();
        for (int j = 0; j < searchList.size(); j++) {
            String itemName = Utils.trimNull(searchList.get(j).get(JsoupUtil.ITEM_NAME));
            if (StringUtils.contains(itemName, ",")) {
                //不可以嵌套联合
                continue;
            }
            if (StringUtils.contains(item_name, itemName)) {
                itemNameList.add(searchList.get(j));
            }
        }
        // context.put("itemNameList", itemNameList);
        return itemNameList;
    }

    private long getDataCountByListId(DataSource dataSource, Map<String, Object> list, Map<String, Object> request, Map<String, String> session) throws Exception {
        Object select_expressObj = ((Map<String, Object>)list.get("express")).get("main");
        String select_express = Utils.expressToString(select_expressObj);

        String orderBy = Utils.expressToString(((Map<String, Object>)list.get("express")).get("orderBy"));
        String groupBy = Utils.expressToString(((Map<String, Object>)list.get("express")).get("groupBy"));
        String count_express = Utils.expressToString(((Map<String, Object>)list.get("express")).get("count"));

        ListExpressExecutor listExpressExecutor = ListExpressExecutor.createInstance();
        String countQl = "";
        if (StringUtils.isBlank(count_express) ||
                StringUtils.equalsIgnoreCase("count(1)", count_express.toLowerCase().trim())) {
            countQl = transSqlCountToQl(select_express);
        } else {
            countQl = transSqlCountToQl(count_express);
        }
        listExpressExecutor.datasource(dataSource).express(countQl);
        //计算group by
        String group = excuteGroup(groupBy, request, session);
        listExpressExecutor.listDTOAndSearchParam(list);
        listExpressExecutor.addParam("_CHECKD_IDS", Utils.getStringByObject(request, "_CHECKD_IDS"));
        listExpressExecutor.addParam("EZ_SUM_FLAG", Utils.getStringByObject(request, "EZ_SUM_FLAG"));
        listExpressExecutor.addParam("GROUP_BY", group);
        listExpressExecutor.addSessionParam(session);
        listExpressExecutor.addRequestParam(request);
        Object obj = listExpressExecutor.executeCount();
        return NumberUtils.toLong(obj + "");
    }

    private String transSqlCountToQl(String ql) {
        if (StringUtils.trimEmpty(ql).toLowerCase(Locale.ROOT).startsWith("select")) {
            return "c=search(\"" + ql + "\");return c;";
        }
        return ql;
    }

    private Page loadingPage(Map<String, Object> list, Map<String, Object> requestParamMap) {
        Page pagination = new Page(requestParamMap);
        List<Map<String, Object>> colList = (List<Map<String, Object>>) list.get("column");
        // 支持一维和二维数组格式，扁平化处理
        List<List<Map<String, Object>>> searchListRows = getFlattenedSearchList(list.get("search"));
        if (Utils.isEmpty(colList)) {
            return pagination;
        }
        final String orderByName=Utils.trimNull(requestParamMap.get("orderByName"));
        final String orderByType=Utils.trimNull(requestParamMap.get("orderByType"));
        //从search里面找到对应字段的alias
        StringBuilder newOrderBy=new StringBuilder();
        if (searchListRows != null) {
            for (List<Map<String, Object>> row : searchListRows) {
                for (Map<String, Object> item : row) {
                    if (StringUtils.equalsIgnoreCase(orderByName, Utils.trimNull(item.get(JsoupUtil.ITEM_NAME)))) {
                        String fieldName = SqlUtils.alias(Utils.trimNull(item.get(JsoupUtil.ALIAS)), Utils.trimNull(item.get(JsoupUtil.ITEM_NAME)));
                        newOrderBy.append(" order by "+fieldName +" "+orderByType);
                        break;
                    }
                }
                if (newOrderBy.length() > 0) {
                    break;
                }
            }
        }

        if(StringUtils.isBlank(newOrderBy.toString())){
            String orderBy = Utils.trimNull(list.get("orderby_express"));
            newOrderBy.append(orderBy);
        }
        pagination.setOrderByClause(newOrderBy.toString());
        try {
            String customJson = Utils.trimNull(requestParamMap.get("customSearch"));
            CustomSearchDTO customSearchDTO = JSONUtils.parseObject(customJson, CustomSearchDTO.class);
            if (customSearchDTO != null) {
                String customOrder = customOrder(customSearchDTO.getO());
                if (StringUtils.isNotBlank(customOrder)) {
                    pagination.setOrderByClause(customOrder);
                }
            }
        } catch (Exception e) {
            logger.warn("",e);
        }
        return pagination;
    }
    private String customOrder(List<CustomSearchOrder> o) {
        if (o == null || o.isEmpty()) {
            return "";
        }
        StringBuilder order = new StringBuilder();
        for (int i = 0; i < o.size(); i++) {
            if (StringUtils.isNotBlank(o.get(i).getF()) && StringUtils.isNotBlank(o.get(i).getO())) {
                order.append(", ")
                        .append(StringUtils.safeDb(o.get(i).getF()))
                        .append(" ")
                        .append(StringUtils.safeDb(o.get(i).getO()));
            }
        }
        if (order.length() > 1) {
            return " order by " + order.substring(1);
        }
        return "";
    }
}
