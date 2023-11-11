package com.ezadmin.controller;

import com.ezadmin.dao.Dao;
import com.ezadmin.dao.model.Info;
import com.ezadmin.dao.model.InitVO;
import com.ezadmin.dao.ListDao;
import com.ezadmin.plugins.express.executor.DefaultExpressExecutor;
import com.ezadmin.service.ListService;
import com.ezadmin.common.annotation.EzMapping;
import com.ezadmin.common.constants.RequestParamConstants;
import com.ezadmin.common.constants.SessionConstants;
import com.ezadmin.common.utils.*;
import com.ezadmin.web.EzResult;
import com.ezadmin.EzBootstrap;


import org.apache.commons.lang.exception.ExceptionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.util.*;

@EzMapping("/ezadmin/list/")
public class ListController extends BaseController {

    private ListService listService = EzProxy.singleInstance(ListService.class);

    @EzMapping("list.html")
    public String list(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String listUrlCode = Utils.trimNull(request.getAttribute("ENCRYPT_LIST_ID"));
        try {
            long start = System.currentTimeMillis();
            if (logger.isInfoEnabled()) {
                logger.info("ezadmin listUrlCode={} start", listUrlCode);
            }
            Map<String, Object> requestParamMap = requestToMap(request);

            requestParamMap.putIfAbsent("perPageInt",request.getParameter("perPageInt"));
            requestParamMap.putIfAbsent("currentPage",request.getParameter("page"));

            requestParamMap.put("ContextPath", request.getContextPath());

            Map<String, String> sessionParamMap = sessionToMap(request.getSession());

            Map<String, Object> list = new HashMap<>();
            if (StringUtils.isNotBlank(listUrlCode)) {
                list = JSONUtils.parseObjectMap(listService.selectAllListById(listUrlCode));
            }
            if (list == null || list.isEmpty()) {
                EzResult.instance().code("404").setMessage("没有找到配置文件" + listUrlCode)
                        .setSuccess(false)
                        .printJSONUtils(response);
                return EzBootstrap.instance().getAdminStyle() + "/404";
            } else {
                request.setAttribute(RequestParamConstants._SEARCH_ITEM_DISPLAY, request.getParameter("_SEARCH_ITEM_DISPLAY"));
                request.setAttribute("_EZ_MOBILE_FLAG", request.getParameter("_EZ_MOBILE_FLAG"));
                request.setAttribute("listUrl", request.getContextPath() + "/ezadmin/list/list-" + listUrlCode);
                request.setAttribute("_EZ_SERVER_NAME", "//" + request.getServerName() + ":" + request.getServerPort());
                if (logger.isDebugEnabled()) {
                    logger.debug("EZADMIN LIST={} 结束执行列表  ,总共耗时：{} ms", listUrlCode, (System.currentTimeMillis() - start));
                }
                listService.fillListById(list, requestParamMap, sessionParamMap);
                request.setAttribute("data", list);
                Map<String, Object> coreMap = (Map<String, Object>) list.get("core");
                String pagetotaltype = ""+coreMap.getOrDefault("pagesync","0");
                if(StringUtils.equalsIgnoreCase(pagetotaltype,"1")){
                    listService.fillCountById(list, requestParamMap, sessionParamMap);
                }
                return EzBootstrap.instance().getAdminStyle() + "/list";
            }
        } catch (Exception e) {
            EzResult.instance().code("500").setMessage("服务器异常" + ExceptionUtils.getFullStackTrace(e))
                    .setSuccess(false)
                    .printJSONUtils(response);
            logger.error("EZADMIN LIST={} 服务器异常 ", listUrlCode, e);
            return EzBootstrap.instance().getAdminStyle() + "/500";
        }
    }

    @EzMapping("count.html")
    public EzResult count(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String ENCRYPT_LIST_ID = Utils.trimNull(request.getAttribute("ENCRYPT_LIST_ID"));
            if (Utils.getLog() != null) {
                Utils.addLog("开始执行列表 list_id=" + ENCRYPT_LIST_ID);
            }
            Map<String, Object> requestParamMap = requestToMap(request);
            Map<String, String> sessionParamMap = sessionToMap(request.getSession());

            Map<String, Object> list = new HashMap<>();
            if (StringUtils.isNotBlank(ENCRYPT_LIST_ID)) {
                list = JSONUtils.parseObjectMap(listService.selectAllListById(ENCRYPT_LIST_ID));
            }

            listService.fillCountById(list, requestParamMap, sessionParamMap);

            request.setAttribute("_EZ_MOBILE_FLAG", request.getParameter("_EZ_MOBILE_FLAG"));
            request.setAttribute("listUrl", request.getContextPath() + "/ezadmin/list/list-" + ENCRYPT_LIST_ID);
            request.setAttribute("_EZ_SERVER_NAME", "//" + request.getServerName() + ":" + request.getServerPort());

            return EzResult.instance().data(list).count(1000);
        } catch (Exception e) {
            Utils.addLog("结束执行列表  异常：", e);
            return EzResult.instance().code("500").setMessage(ExceptionUtils.getFullStackTrace(e));
        }
    }


    @EzMapping("tree.html")
    public String tree(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String ENCRYPT_LIST_ID = Utils.trimNull(request.getAttribute("ENCRYPT_LIST_ID"));
        try {
            logger.info("ezadmin LIST={} START ", ENCRYPT_LIST_ID);
            Map<String, Object> requestParamMap = requestToMap(request);
            if (NumberUtils.toInt("" + requestParamMap.get("perPageInt")) > 5000) {
                requestParamMap.put("perPageInt", 5000);
            }
            requestParamMap.put("ContextPath", request.getContextPath());
            //是否加载数据，由于是异步加载，所以无需加载数据：：treedata.html
            requestParamMap.put("loadDataFlag", 0);
            Map<String, String> sessionParamMap = sessionToMap(request.getSession());

            Map<String, Object> list = new HashMap<>();
            if (StringUtils.isNotBlank(ENCRYPT_LIST_ID)) {
                list = JSONUtils.parseObjectMap(listService.selectAllListById(ENCRYPT_LIST_ID));
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
            String layout = Utils.trimNull(core.get(JsoupUtil.LAYDATA));
            Map m = JSONUtils.parseObjectMap(layout);
            m.put("title", "操作");
            m.put("toolbar", "#TPL-treeTable-demo-tools");
            col.add(m);
            request.setAttribute("data", list);
            request.setAttribute("coldata", JSONUtils.toJSONString(col));
            request.setAttribute(RequestParamConstants._SEARCH_ITEM_DISPLAY, request.getParameter("_SEARCH_ITEM_DISPLAY"));
            request.setAttribute("_EZ_MOBILE_FLAG", request.getParameter("_EZ_MOBILE_FLAG"));
            request.setAttribute("listUrl", request.getContextPath() + "/ezadmin/list/tree-" + ENCRYPT_LIST_ID);

            request.setAttribute("_EZ_SERVER_NAME", "//" + request.getServerName() + ":" + request.getServerPort());

            return EzBootstrap.instance().getAdminStyle() + "/treelist";

        } catch (Exception e) {
            EzResult.instance().code("500").setMessage("服务器异常" + ExceptionUtils.getFullStackTrace(e))
                    .setSuccess(false)
                    .printJSONUtils(response);
            logger.error("EZADMIN LIST={} 服务器异常 ", ENCRYPT_LIST_ID, e);
            return EzBootstrap.instance().getAdminStyle() + "/500";
        }
    }

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
        Map<String, Object> list = new HashMap<>();
        if (StringUtils.isNotBlank(ENCRYPT_LIST_ID)) {
            list = JSONUtils.parseObjectMap(listService.selectAllListById(ENCRYPT_LIST_ID));
        }
        listService.fillTreeById(list, requestParamMap, sessionParamMap);
        Map<String, Object> core = (Map<String, Object>) list.get("core");
        return EzResult.instance().data(core.get("dataList"));
    }

    //
    @EzMapping("trace.html")
    public void trace(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String listId = Utils.trimNull(request.getAttribute("LIST_ID"));
        String ENCRYPT_LIST_ID = Utils.trimNull(request.getAttribute("ENCRYPT_LIST_ID"));
        long start = System.currentTimeMillis();
        if (Utils.getLog() != null) {
            Utils.addLog("开始执行列表 ID=" + ENCRYPT_LIST_ID);
        }

        Map<String, Object> requestParamMap = requestToMap(request);


        Map<String, String> sessionParamMap = sessionToMap(request.getSession());
        Map<String, Object> list = new HashMap<>();
        if (StringUtils.isNotBlank(ENCRYPT_LIST_ID)) {
            list = JSONUtils.parseObjectMap(listService.selectAllListById(ENCRYPT_LIST_ID));
        }
        listService.fillCountById(list, requestParamMap, sessionParamMap);
        listService.fillListById(list, requestParamMap, sessionParamMap);
        listService.fillTreeById(list, requestParamMap, sessionParamMap);

        if (Utils.getLog() != null) {
            Utils.addLog("requestParamMap " + requestParamMap);

        }
        if (Utils.getLog() != null) {
            Utils.addLog("sessionParamMap " + JSONUtils.toJSONString(sessionParamMap));
        }

        request.setAttribute("data", list);
        request.setAttribute("_SEARCH_ITEM_DISPLAY", request.getParameter("_SEARCH_ITEM_DISPLAY"));

        request.setAttribute("listUrl", request.getContextPath() + "/ezadmin/list/list-" + ENCRYPT_LIST_ID);
        if (Utils.getLog() != null) {
            Utils.addLog("结束执行列表 list_id=" + listId + ",总共耗时：" + (System.currentTimeMillis() - start) + "ms");
        }
    }

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

        Map<String, Object> list = new HashMap<>();
        if (StringUtils.isNotBlank(ENCRYPT_LIST_ID)) {
            list = JSONUtils.parseObjectMap(listService.selectAllListById(ENCRYPT_LIST_ID));
        }
        listService.fillListById(list, requestParamMap, sessionParamMap);

        Map<String, Object> coreMap = (Map<String, Object>) list.get("core");
        List<Map<String, Object>> dataList = (List<Map<String, Object>>) coreMap.get("dataList");

        dataList.forEach(item->{
            item.remove("tds");
            item.remove("rowjson");
        });
        request.setAttribute("data", list);
        request.setAttribute("_SEARCH_ITEM_DISPLAY", request.getParameter("_SEARCH_ITEM_DISPLAY"));

        request.setAttribute("listUrl", request.getContextPath() + "/ezadmin/list/list-" + ENCRYPT_LIST_ID);

        if (Utils.getLog() != null) {
            Utils.addLog("结束执行列表 list_id=" + listId + ",总共耗时：" + (System.currentTimeMillis() - start) + "ms");
        }
        return EzResult.instance().data(dataList).count(dataList.size());
    }

    @EzMapping("selectCols.html")
    public String selectCols(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String ENCRYPT_LIST_ID = Utils.trimNull(request.getAttribute("ENCRYPT_LIST_ID"));
        Map<String, Object> requestParamMap = requestToMap(request);
        requestParamMap.put("default_empty", "1");
        Map<String, Object> list = new HashMap<>();
        if (StringUtils.isNotBlank(ENCRYPT_LIST_ID)) {
            list = JSONUtils.parseObjectMap(listService.selectAllListById(ENCRYPT_LIST_ID));
        }
        List<Map<String, Object>> searchList = (List<Map<String, Object>>) list.get("search");
        List<Map<String, Object>> colList = (List<Map<String, Object>>) list.get("col");
        request.setAttribute("fromSearchField", searchList);
        request.setAttribute("fromColField", colList);
        request.setAttribute("IS_DEBUG", request.getParameter("IS_DEBUG"));
        request.setAttribute("_EZ_SERVER_NAME", "//" + request.getServerName() + ":" + request.getServerPort());
        return EzBootstrap.instance().getAdminStyle() + "/custom_cols_cache";
    }


    @EzMapping("navs.html")
    public void navs(HttpServletRequest request, HttpServletResponse response) throws Exception {
        InitVO vo = new InitVO();
        List<Info> toproot = new ArrayList<>();
        List<Info> root = new ArrayList<>();
        Info listN = new Info();
        listN.setTitle("列表管理");
        listN.setId("1");
        listN.setPid("0");
        listN.setHref("/ezadmin/list/list-listHtml");
        root.add(listN);

        Info listF = new Info();
        listF.setTitle("表单管理");
        listF.setId("2");
        listF.setPid("0");
        listF.setHref("/ezadmin/list/list-formHtml");
        root.add(listF);

        Info TOP = new Info();
        TOP.setTitle("系统管理");
        TOP.setChild(root);
        toproot.add(TOP);
        vo.homeInfo(EzBootstrap.instance().getSystemName(), "").
                logoInfo("", "/ezadmin/index.html", EzBootstrap.instance().getConfig().get("logo") + "")
                .setMenuInfo(toproot);
        EzResult.instance().msg("0", "ok")
                .data(vo).printJSONUtils(response);
    }

    @EzMapping("doOrder.html")
    public EzResult doOrder(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String formId =  Utils.trimNull(request.getAttribute("FORM_ID"));
        String ENCRYPT_LIST_ID = Utils.trimNull(request.getAttribute("ENCRYPT_LIST_ID"));

        try {
            if (StringUtils.isBlank(formId)&&StringUtils.isBlank(ENCRYPT_LIST_ID)) {
                return EzResult.instance().code("404");
            }
            Map<String, Object> listDb = listService.selectAllListMapById( ENCRYPT_LIST_ID);
            Map<String, Object> core = (Map<String, Object>) listDb.get("core");
            String orderExpress=Utils.trimNull(core.get(JsoupUtil.DISPLAYORDER_EXPRESS));
            Map<String,Object> searchParamsValues=requestToMap(request );
            Map<String, String> sessionParamMap = sessionToMap(request.getSession());
            DataSource dataSource= EzBootstrap.instance().getDataSourceByKey(StringUtils.lowerCase(Utils.trimNull(core.get(JsoupUtil.DATASOURCE))) );

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
