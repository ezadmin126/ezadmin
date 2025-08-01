package top.ezadmin.controller;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.exception.ExceptionUtils;
import top.ezadmin.EzClientBootstrap;
import top.ezadmin.common.NotExistException;
import top.ezadmin.common.annotation.EzMapping;
import top.ezadmin.common.constants.RequestParamConstants;
import top.ezadmin.common.constants.SessionConstants;
import top.ezadmin.common.utils.*;
import top.ezadmin.dao.model.Info;
import top.ezadmin.dao.model.InitVO;
import top.ezadmin.plugins.express.executor.DefaultExpressExecutor;
import top.ezadmin.service.ListService;
import top.ezadmin.web.EzResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 * 列表控制器
 */
@EzMapping("/topezadmin/list/")
public class ListController extends BaseController {

    private ListService listService = EzProxy.singleInstance(ListService.class);

    @EzMapping("list.html")
    public String list(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String listUrlCode = Utils.trimNull(request.getAttribute("ENCRYPT_LIST_ID"));
        if(StringUtils.isBlank(listUrlCode)){
            throw new NotExistException();
        }
        //TODO 需要优化，如果列表是ajax请求，则返回ajaxList页面

        return old(request,response);
    }
    


    private String old(HttpServletRequest request, HttpServletResponse response)throws Exception{
        String listUrlCode = Utils.trimNull(request.getAttribute("ENCRYPT_LIST_ID"));
        if(StringUtils.isBlank(listUrlCode)){
            throw new NotExistException();
        }
        Map<String, Object> requestParamMap = requestToMap(request);
        String  customSearch=Utils.trimNull(requestParamMap.get("customSearch") );
        request.setAttribute("customSearch",customSearch);

        requestParamMap.putIfAbsent("perPageInt",request.getParameter("perPageInt"));
        requestParamMap.putIfAbsent("currentPage",request.getParameter("page"));
        requestParamMap.put("vi",request.getAttribute("vi"));

        requestParamMap.put("ContextPath", request.getContextPath());

        Map<String, String> sessionParamMap = sessionToMap(request.getSession());
        Map<String, Object>  list =JSONUtils.parseObjectMap(listService.selectPublishListById(listUrlCode)) ;
        if(!Utils.isNotEmpty(list)){
            logger.warn("找不到列表：{}",listUrlCode);
            throw new NotExistException();
        }

        request.setAttribute(RequestParamConstants._SEARCH_ITEM_DISPLAY, request.getParameter("_SEARCH_ITEM_DISPLAY"));

        request.setAttribute("listUrl", request.getContextPath() + "/topezadmin/list/list-" + listUrlCode);
        request.setAttribute("_EZ_SERVER_NAME", "//" + request.getServerName() + ":" + request.getServerPort());
        request.setAttribute("cacheFlag",EzClientBootstrap.instance().isSqlCache());
        request.setAttribute("customSearchOpen",EzClientBootstrap.instance().isCustomSearchOpen());

        listService.fillListById(list, requestParamMap, sessionParamMap);
        request.setAttribute("data", list);
        Map<String, Object> coreMap = (Map<String, Object>) list.get("core");
        String pagetotaltype = ""+coreMap.getOrDefault("pagesync","0");
        String layout = ""+coreMap.getOrDefault("layout",EzClientBootstrap.instance().getLayout());

        request.setAttribute("layout",layout);

        if(StringUtils.equalsIgnoreCase(pagetotaltype,"1")){
            listService.fillCountById(list, requestParamMap, sessionParamMap);
        }
        String username = Utils.trimNull(request.getSession().getAttribute(SessionConstants.EZ_SESSION_USER_NAME_KEY));
        request.setAttribute("EZ_SESSION_USER_NAME_KEY",username);
        String adminStyle=Utils.trimNullDefault(coreMap.get(JsoupUtil.ADMINSTYLE),EzClientBootstrap.instance().getAdminStyle());
        String template=Utils.trimNullDefault(coreMap.get(JsoupUtil.TEMPLATE),"list");
        request.setAttribute("prefixUrl",EzClientBootstrap.instance().getPrefixUrl());
        return adminStyle + "/"+template;
    }


    /**
     * 异步加载列表总数
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @EzMapping("count.html")
    public EzResult count(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String listUrlCode = Utils.trimNull(request.getAttribute("ENCRYPT_LIST_ID"));
        try {
            if (Utils.getLog() != null) {
                Utils.addLog("start  id=" + listUrlCode);
            }
            Map<String, Object> requestParamMap = requestToMap(request);
            Map<String, String> sessionParamMap = sessionToMap(request.getSession());

            Map<String, Object>  list =JSONUtils.parseObjectMap(listService.selectPublishListById(listUrlCode)) ;
            if(Utils.isEmpty(list)){
                logger.warn("找不到列表：{}",listUrlCode);
                return EzResult.instance().code("500").setMessage("NotExistException");
            }

         long count=   listService.fillCountById(list, requestParamMap, sessionParamMap);
            request.setAttribute("listUrl", request.getContextPath() + "/topezadmin/list/list-" + listUrlCode);
            request.setAttribute("_EZ_SERVER_NAME", "//" + request.getServerName() + ":" + request.getServerPort());
             Map<String, Object>  list2=new HashMap<>();
            list2.put("page",list.get("page"));
            return EzResult.instance().data(list2).count(count);
        } catch (Exception e) {
            logger.error("count：{}",listUrlCode,e);
            return EzResult.instance().code("500").setMessage(ExceptionUtils.getFullStackTrace(e));
        }
    }

    /**
     * 树形列表
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @EzMapping("tree.html")
    public String tree(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String ENCRYPT_LIST_ID = Utils.trimNull(request.getAttribute("ENCRYPT_LIST_ID"));

            Map<String, Object> requestParamMap = requestToMap(request);
            if (NumberUtils.toInt("" + requestParamMap.get("perPageInt")) > 5000) {
                requestParamMap.put("perPageInt", 5000);
            }
            requestParamMap.put("ContextPath", request.getContextPath());
            //是否加载数据，由于是异步加载，所以无需加载数据：：treedata.html
            requestParamMap.put("loadDataFlag", 0);
        requestParamMap.put("vi",request.getAttribute("vi"));
            Map<String, String> sessionParamMap = sessionToMap(request.getSession());

        Map<String, Object>  list =JSONUtils.parseObjectMap(listService.selectPublishListById(ENCRYPT_LIST_ID)) ;
        if(Utils.isEmpty(list)){
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
            request.setAttribute("data", list);
            request.setAttribute("coldata", JSONUtils.toJSONString(col));

            String layout = ""+core.getOrDefault("layout",EzClientBootstrap.instance().getLayout());

            request.setAttribute("layout",layout);
            request.setAttribute(RequestParamConstants._SEARCH_ITEM_DISPLAY, request.getParameter("_SEARCH_ITEM_DISPLAY"));
            request.setAttribute("listUrl", request.getContextPath() + "/topezadmin/list/tree-" + ENCRYPT_LIST_ID);

            request.setAttribute("_EZ_SERVER_NAME", "//" + request.getServerName() + ":" + request.getServerPort());

        String adminStyle=Utils.trimNullDefault(core.get(JsoupUtil.ADMINSTYLE),EzClientBootstrap.instance().getAdminStyle());
        return adminStyle + "/listtree";
    }
    /**
     * 异步加载树形列表数据
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @EzMapping("treedata.html")
    public EzResult treedata(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String listId = Utils.trimNull(request.getAttribute("LIST_ID"));
        String ENCRYPT_LIST_ID = Utils.trimNull(request.getAttribute("ENCRYPT_LIST_ID"));

        if (Utils.getLog() != null) {
            Utils.addLog("开始执行列表 list_id=" + listId);
        }
        Map<String, Object> requestParamMap = requestToMap(request);
        Map<String, String> sessionParamMap = sessionToMap(request.getSession());

        String sessionUserId = Utils.trimNull(request.getSession().getAttribute(SessionConstants.EZ_SESSION_USER_ID_KEY));
        sessionParamMap.put(SessionConstants.EZ_SESSION_USER_ID_KEY, sessionUserId);
        Map<String, Object>  list =JSONUtils.parseObjectMap(listService.selectPublishListById(ENCRYPT_LIST_ID)) ;
        if(Utils.isEmpty(list)){
            throw new NotExistException();
        }
        requestParamMap.put("perPageInt",10000);
        listService.fillTreeById(list, requestParamMap, sessionParamMap);
        Map<String, Object> core = (Map<String, Object>) list.get("core");
        return EzResult.instance().data(core.get("dataList"));
    }
    /**
     * 打印sql
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @EzMapping("trace.html")
    public String trace(HttpServletRequest request, HttpServletResponse response) throws Exception {
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


        Map<String, Object> list = new HashMap<>();
        if (StringUtils.isNotBlank(ENCRYPT_LIST_ID)) {
            try {
                  list =JSONUtils.parseObjectMap(listService.selectPublishListById(ENCRYPT_LIST_ID)) ;
                if(Utils.isEmpty(list)){
                    throw new NotExistException();
                }
            }catch (Exception e){
                if (Utils.getLog() != null) {
                    Utils.addLog("加载列表异常",e);
                }
            }
        }
        try {
            Utils.addLog("打印countSQL "  );
            listService.fillCountById(list, requestParamMap, sessionParamMap);
        }catch (Exception e){
            if (Utils.getLog() != null) {
                Utils.addLog("加载总数异常",e);
            }
        }
        try{
            Utils.addLog("打印tree SQL "  );
        listService.fillTreeById(list, requestParamMap, sessionParamMap);
        }catch (Exception e){
            if (Utils.getLog() != null) {
                Utils.addLog("查询树数据异常",e);
            }
        }
        String result= list(request,response);

        request.setAttribute("data", list);
        request.setAttribute("_SEARCH_ITEM_DISPLAY", request.getParameter("_SEARCH_ITEM_DISPLAY"));

        request.setAttribute("listUrl", request.getContextPath() + "/topezadmin/list/trace-" + ENCRYPT_LIST_ID);
        if (Utils.getLog() != null) {
            Utils.addLog("end list_id=" + listId + ",总共耗时：" + (System.currentTimeMillis() - start) + "ms");
        }
        request.setAttribute("cacheFlag",false);
        return result;
    }
    /**
     * 列表api 接口数据
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @EzMapping("api.html")
    public EzResult api(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String listId = Utils.trimNull(request.getAttribute("LIST_ID"));
        String ENCRYPT_LIST_ID = Utils.trimNull(request.getAttribute("ENCRYPT_LIST_ID"));
        long start = System.currentTimeMillis();
        if (Utils.getLog() != null) {
            Utils.addLog("开始执行列表 list_id=" + listId);
        }
        Map<String, Object> requestParamMap = requestToMap(request);

        Map<String, String> sessionParamMap = sessionToMap(request.getSession());

        Map<String, Object>  list =JSONUtils.parseObjectMap(listService.selectPublishListById(ENCRYPT_LIST_ID)) ;
        if(Utils.isEmpty(list)){
            throw new NotExistException();
        }
        listService.fillListById(list, requestParamMap, sessionParamMap);

        Map<String, Object> coreMap = (Map<String, Object>) list.get("core");
        List<Map<String, Object>> dataList = (List<Map<String, Object>>) coreMap.get("dataList");
        if(dataList!=null){
            dataList.forEach(item->{
                item.remove("tds");
                item.remove("rowjson");
            });
        }else{
            dataList=new ArrayList<>();
        }
        request.setAttribute("data", list);
        request.setAttribute("_SEARCH_ITEM_DISPLAY", request.getParameter("_SEARCH_ITEM_DISPLAY"));

        request.setAttribute("listUrl", request.getContextPath() + "/topezadmin/list/list-" + ENCRYPT_LIST_ID);

        if (Utils.getLog() != null) {
            Utils.addLog("结束执行列表 list_id=" + listId + ",总共耗时：" + (System.currentTimeMillis() - start) + "ms");
        }
        return EzResult.instance().data(dataList).count(dataList.size());
    }
    /**
     * 选择列
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @EzMapping("selectCols.html")
    public String selectCols(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String ENCRYPT_LIST_ID = Utils.trimNull(request.getAttribute("ENCRYPT_LIST_ID"));
        Map<String, Object> requestParamMap = requestToMap(request);

        requestParamMap.put("default_empty", "1");
        Map<String, Object>  list =JSONUtils.parseObjectMap(listService.selectPublishListById(ENCRYPT_LIST_ID)) ;
        if(Utils.isEmpty(list)){
            throw new NotExistException();
        }
        List<Map<String, Object>> searchList = (List<Map<String, Object>>) list.get("search");
        List<Map<String, Object>> colList = (List<Map<String, Object>>) list.get("col");
        request.setAttribute("fromSearchField", searchList);
        request.setAttribute("fromColField", colList);
        request.setAttribute("IS_DEBUG", request.getParameter("IS_DEBUG"));
        request.setAttribute("_EZ_SERVER_NAME", "//" + request.getServerName() + ":" + request.getServerPort());
        return EzClientBootstrap.instance().getAdminStyle() + "/custom_cols_cache";
    }
    /**
     * 自定义搜索
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @EzMapping("customSearch.html")
    public String custom_search(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String ENCRYPT_LIST_ID = Utils.trimNull(request.getAttribute("ENCRYPT_LIST_ID"));
        String config = Utils.trimNull(request.getParameter("config"));
        Map<String, Object> requestParamMap = requestToMap(request);
        requestParamMap.put("default_empty", "1");
        Map<String, String> sessionParamMap = sessionToMap(request.getSession());

        Map<String, Object>  list =JSONUtils.parseObjectMap(listService.selectPublishListById(ENCRYPT_LIST_ID)) ;
        if(Utils.isEmpty(list)){
            throw new NotExistException();
        }
        listService.fillListById(list, requestParamMap, sessionParamMap);

        List<Map<String, Object>> searchList = (List<Map<String, Object>>) list.get("search");
        List<Map<String, Object>> colList = (List<Map<String, Object>>) list.get("col");
        List<Map<String, Object>> searchListR=searchList.stream().filter(s ->
                        s.get("type").equals("numberrange")
                                ||s.get("type").equals("input-text")
                             || s.get("type").equals("select")
                             || s.get("type").equals("select-search")
                              || s.get("type").equals("xmselect")
                             || s.get("type").equals("daterange")
                ) // 筛选条件：性别为男性
                .collect(Collectors.toList());

        List<Map<String, Object>> colListR=colList.stream().filter(s ->
                        s.get("order").equals("1")
                ) // 筛选条件：性别为男性
                .collect(Collectors.toList());
        request.setAttribute("fromSearchField", searchListR);
        request.setAttribute("fromColField", colListR);
        request.setAttribute("data", list);
        request.setAttribute("config", config);
        request.setAttribute("IS_DEBUG", request.getParameter("IS_DEBUG"));
        request.setAttribute("_EZ_SERVER_NAME", "//" + request.getServerName() + ":" + request.getServerPort());
        return EzClientBootstrap.instance().getAdminStyle() + "/custom_search";
    }

    /**
     * 用于admin的默认导航
     * @param request
     * @param response
     * @throws Exception
     */
    @EzMapping("navs.html")
    public void navs(HttpServletRequest request, HttpServletResponse response) throws Exception {
        InitVO vo = new InitVO();
        List<Info> toproot = new ArrayList<>();
        List<Info> root = new ArrayList<>();
        Info listN = new Info();
        listN.setTitle("新增列表");
        listN.setId("1");
        listN.setPid("0");
        listN.setHref("/topezadmin/list/loadEdit-");
        root.add(listN);

        Info listF = new Info();
        listF.setTitle("新增表单");
        listF.setId("2");
        listF.setPid("0");
        listF.setHref("/topezadmin/form/loadEdit-");
        root.add(listF);

        Info TOP = new Info();
        TOP.setTitle("系统管理");
        TOP.setChild(root);
        toproot.add(TOP);
        vo.homeInfo(EzClientBootstrap.instance().getSystemName(), "").
                logoInfo("", "/topezadmin/index.html", EzClientBootstrap.instance().getConfig().get("logo") + "")
                .setMenuInfo(toproot);
        EzResult.instance().msg("0", "ok")
                .data(vo).printJSONUtils(response);
    }

    /**
     * 列表输入排序
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @EzMapping("doOrder.html")
    public EzResult doOrder(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String formId =  Utils.trimNull(request.getAttribute("FORM_ID"));
        String ENCRYPT_LIST_ID = Utils.trimNull(request.getAttribute("ENCRYPT_LIST_ID"));

        try {
            if (StringUtils.isBlank(formId)&&StringUtils.isBlank(ENCRYPT_LIST_ID)) {
                return EzResult.instance().code("404");
            }
            Map<String, Object>  listDb =JSONUtils.parseObjectMap(listService.selectPublishListById(ENCRYPT_LIST_ID)) ;
            Map<String, Object> core = (Map<String, Object>) listDb.get("core");
            String orderExpress=Utils.trimNull(core.get(JsoupUtil.DISPLAYORDER_EXPRESS));
            Map<String,Object> searchParamsValues=requestToMap(request );
            Map<String, String> sessionParamMap = sessionToMap(request.getSession());
            DataSource dataSource= EzClientBootstrap.instance().getDataSourceByKey(StringUtils.lowerCase(Utils.trimNull(core.get(JsoupUtil.DATASOURCE))) );

            //计算初始化表单的参数值
            Object result = DefaultExpressExecutor.createInstance().datasource(dataSource)
                    .express(orderExpress)
                    .addParam(searchParamsValues)
                    .addRequestParam(searchParamsValues)
                    .addSessionParam(sessionParamMap)
                    .execute();

            return EzResult.instance() ;
        }
        catch (Exception e) {
            logger.error("ezform doOrder error {} {}  ",formId,ENCRYPT_LIST_ID ,e);
            return EzResult.instance().setSuccess(false).code("500").setMessage("服务器异常");
        }
    }

}
