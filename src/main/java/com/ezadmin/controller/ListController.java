package com.ezadmin.controller;

import com.ezadmin.dao.model.Info;
import com.ezadmin.dao.model.InitVO;
import com.ezadmin.dao.ListDao;
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
import java.util.*;

@EzMapping("/ezadmin/list/")
public class ListController extends BaseController {

    EzBootstrap bootstrap= EzBootstrap.instance();
    private ListService listService = EzProxy.singleInstance(ListService.class);

    @EzMapping("list.html")
    public String list(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String listId = Utils.trimNull(request.getAttribute("LIST_ID"));
        String ENCRYPT_LIST_ID = Utils.trimNull(request.getAttribute("ENCRYPT_LIST_ID"));
        try {
            long start = System.currentTimeMillis();

            logger.info("ezadmin LIST={} START ",ENCRYPT_LIST_ID);

            Map<String, Object> requestParamMap =requestToMap(request);
            if(NumberUtils.toInt(""+requestParamMap.get("perPageInt"))>5000){
                requestParamMap.put("perPageInt",5000);
            }
            requestParamMap.put("ContextPath", request.getContextPath());

            Map<String, String> sessionParamMap = sessionToMap(request.getSession());

            Map<String, Object> list=new HashMap<>();
            if(StringUtils.isNotBlank(ENCRYPT_LIST_ID)){
                list=   JSONUtils.parseObjectMap(listService.selectAllListById(ENCRYPT_LIST_ID))  ;
            }
            if (list  == null||list.isEmpty()) {
                EzResult.instance().code("404").setMessage("没有找到配置文件" +ENCRYPT_LIST_ID)
                        .setSuccess(false)
                        .printJSONUtils(response);
                return "404";
            } else {
                request.setAttribute(RequestParamConstants._SEARCH_ITEM_DISPLAY, request.getParameter("_SEARCH_ITEM_DISPLAY"));
                request.setAttribute("_EZ_MOBILE_FLAG",request.getParameter("_EZ_MOBILE_FLAG"));
                request.setAttribute("listUrl", request.getContextPath()+"/ezadmin/list/list-" + ENCRYPT_LIST_ID);
                request.setAttribute("_EZ_SERVER_NAME",  "//"+request.getServerName()+":"+request.getServerPort());
                if(logger.isDebugEnabled()){
                    logger.debug("EZADMIN LIST={} 结束执行列表  ,总共耗时：{} ms",ENCRYPT_LIST_ID,(System.currentTimeMillis() - start) );
                }
                listService.fillListById(list,requestParamMap,sessionParamMap);
                request.setAttribute("data",list);
                return "layui/list/list";
            }
        }catch(Exception e){
            EzResult.instance().code("500").setMessage("服务器异常" +ExceptionUtils.getFullStackTrace(e))
                    .setSuccess(false)
                    .printJSONUtils(response);
            logger.error("EZADMIN LIST={} 服务器异常 ",ENCRYPT_LIST_ID,e);
            return "500";
        }
    }

    @EzMapping("count.html")
    public EzResult count(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String ENCRYPT_LIST_ID = Utils.trimNull(request.getAttribute("ENCRYPT_LIST_ID"));
            if (Utils.getLog() != null) {
                Utils.addLog("开始执行列表 list_id=" + ENCRYPT_LIST_ID);
            }
            Map<String, Object> requestParamMap =requestToMap(request);
            Map<String, String> sessionParamMap = sessionToMap(request.getSession());

            Map<String, Object> list=new HashMap<>();
            if(StringUtils.isNotBlank(ENCRYPT_LIST_ID)){
                list=   JSONUtils.parseObjectMap(listService.selectAllListById(ENCRYPT_LIST_ID))  ;
            }

            listService.fillCountById(list,requestParamMap,sessionParamMap);

             request.setAttribute("_EZ_MOBILE_FLAG",request.getParameter("_EZ_MOBILE_FLAG"));
            request.setAttribute("listUrl", request.getContextPath()+"/ezadmin/list/list-" + ENCRYPT_LIST_ID);
             request.setAttribute("_EZ_SERVER_NAME",  "//"+request.getServerName()+":"+request.getServerPort());

            return EzResult.instance().data(list)
                    .count(1000);
        }catch(Exception e){
            Utils.addLog("结束执行列表  异常："  ,e);
            return  EzResult.instance().code("500").setMessage(ExceptionUtils.getFullStackTrace(e));
        }
    }


    @EzMapping("tree.html")
    public String tree(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String listId = Utils.trimNull(request.getAttribute("LIST_ID"));
        String ENCRYPT_LIST_ID = Utils.trimNull(request.getAttribute("ENCRYPT_LIST_ID"));
        try {
            long start = System.currentTimeMillis();

            logger.info("ezadmin LIST={} START ",ENCRYPT_LIST_ID);

            Map<String, Object> requestParamMap =requestToMap(request);
            if(NumberUtils.toInt(""+requestParamMap.get("perPageInt"))>5000){
                requestParamMap.put("perPageInt",5000);
            }
            requestParamMap.put("ContextPath", request.getContextPath());
            requestParamMap.put("loadDataFlag",0);
            Map<String, String> sessionParamMap = sessionToMap(request.getSession());

            Map<String, Object> list=new HashMap<>();
            if(StringUtils.isNotBlank(ENCRYPT_LIST_ID)){
                list=   JSONUtils.parseObjectMap(listService.selectAllListById(ENCRYPT_LIST_ID))  ;
            }

            listService.fillListById(list,requestParamMap,sessionParamMap);
            Map<String, Object> core= (Map<String, Object>)  list.get("core");

                //组装col
            List col=new ArrayList();

            col.add(JSONUtils.parseObjectMap("{\"type\": \"checkbox\" }"));

            List<Map<String,Object>> colList=(List<Map<String,Object>>)list.get("col");
            for (int i = 0; i < colList.size(); i++) {
                String laydata=Utils.getStringByObject(colList.get(i),JsoupUtil.LAYDATA);
                col.add(JSONUtils.parseObjectMap(laydata));
            }
                String layout=Utils.trimNull(core.get(JsoupUtil.LAYDATA));
                Map m=JSONUtils.parseObjectMap(layout);
                m.put("title","操作");
                m.put("toolbar","#TPL-treeTable-demo-tools");
                col.add(m);
// col.add(JSONUtils.parseObjectMap("{ fixed: \"right\", title: \"操作\", width: 181, align: \"center\", toolbar: \"#TPL-treeTable-demo-tools\"} "));
                request.setAttribute("data", list);
                request.setAttribute("coldata",JSONUtils.toJSONString(col));
                request.setAttribute(RequestParamConstants._SEARCH_ITEM_DISPLAY, request.getParameter("_SEARCH_ITEM_DISPLAY"));
                request.setAttribute("_EZ_MOBILE_FLAG",request.getParameter("_EZ_MOBILE_FLAG"));
                request.setAttribute("listUrl", request.getContextPath()+"/ezadmin/list/tree-" + ENCRYPT_LIST_ID);

                request.setAttribute("_EZ_SERVER_NAME",  "//"+request.getServerName()+":"+request.getServerPort());

                return "layui/list/treelist";

        }catch(Exception e){
            EzResult.instance().code("500").setMessage("服务器异常" +ExceptionUtils.getFullStackTrace(e))
                    .setSuccess(false)
                    .printJSONUtils(response);
            logger.error("EZADMIN LIST={} 服务器异常 ",ENCRYPT_LIST_ID,e);
            return "500";
        }
    }
    @EzMapping("treedata.html")
    public EzResult treedata(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String listId = Utils.trimNull(request.getAttribute("LIST_ID")) ;
        String ENCRYPT_LIST_ID =Utils.trimNull(request.getAttribute("ENCRYPT_LIST_ID")) ;
        long start=System.currentTimeMillis();
        if(Utils.getLog()!=null) {
            Utils.addLog("开始执行列表 list_id=" + listId);
        }
        Map<String, Object> requestParamMap =requestToMap(request);
        Map<String, String> sessionParamMap = sessionToMap(request.getSession());

        String sessionUserId = Utils.trimNull(request.getSession().getAttribute(SessionConstants.EZ_SESSION_USER_ID_KEY));
        sessionParamMap.put(SessionConstants.EZ_SESSION_USER_ID_KEY,sessionUserId);
        Map<String, Object> list=new HashMap<>();
        if(StringUtils.isNotBlank(ENCRYPT_LIST_ID)){
            list=   JSONUtils.parseObjectMap(listService.selectAllListById(ENCRYPT_LIST_ID))  ;
        }
        listService.fillTreeById(list,requestParamMap,sessionParamMap);
        Map<String, Object> core= (Map<String, Object>)  list.get("core");
        return EzResult.instance().data(core.get("dataList"));
    }
//
    @EzMapping("trace.html")
    public void trace(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String listId =Utils.trimNull(request.getAttribute("LIST_ID")) ;
        String ENCRYPT_LIST_ID =Utils.trimNull(request.getAttribute("ENCRYPT_LIST_ID")) ;
        long start=System.currentTimeMillis();
        if(Utils.getLog()!=null) {
            Utils.addLog("开始执行列表 ID=" + ENCRYPT_LIST_ID);
        }

        Map<String, Object> requestParamMap =requestToMap(request);


        Map<String, String> sessionParamMap = sessionToMap(request.getSession());
        Map<String, Object> list=new HashMap<>();
        if(StringUtils.isNotBlank(ENCRYPT_LIST_ID)){
            list=   JSONUtils.parseObjectMap(listService.selectAllListById(ENCRYPT_LIST_ID))  ;
        }
        listService.fillCountById(list,requestParamMap,sessionParamMap);
        listService.fillListById(list,requestParamMap,sessionParamMap);
        listService.fillTreeById(list,requestParamMap,sessionParamMap);

        if(Utils.getLog()!=null) {
            Utils.addLog("requestParamMap " + requestParamMap);

        }
        if(Utils.getLog()!=null) {
            Utils.addLog("sessionParamMap " + JSONUtils.toJSONString(sessionParamMap));
        }

        request.setAttribute("data",list);
        request.setAttribute("_SEARCH_ITEM_DISPLAY",request.getParameter("_SEARCH_ITEM_DISPLAY"));

        request.setAttribute("listUrl",request.getContextPath()+"/ezadmin/list/list-"+ENCRYPT_LIST_ID);
        if(Utils.getLog()!=null) {
            Utils.addLog("结束执行列表 list_id=" + listId + ",总共耗时：" + (System.currentTimeMillis() - start) + "ms");
        }
    }
    @EzMapping("api.html")
    public EzResult api(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String listId =Utils.trimNull(request.getAttribute("LIST_ID")) ;
        String ENCRYPT_LIST_ID =Utils.trimNull(request.getAttribute("ENCRYPT_LIST_ID")) ;
        long start=System.currentTimeMillis();
        if(Utils.getLog()!=null) {
            Utils.addLog("开始执行列表 list_id=" + listId);
        }
        Map<String, Object> requestParamMap =requestToMap(request);

        Map<String, String> sessionParamMap = sessionToMap(request.getSession());

        Map<String, Object> list=new HashMap<>();
        if(StringUtils.isNotBlank(ENCRYPT_LIST_ID)){
            list=   JSONUtils.parseObjectMap(listService.selectAllListById(ENCRYPT_LIST_ID))  ;
        }
         listService.fillListById(list,requestParamMap,sessionParamMap);

        Map<String,Object> coreMap=(Map<String,Object>)list.get("core");
        List<Map<String,Object>> dataList=(List<Map<String,Object>>)coreMap.get("dataList");

        request.setAttribute("data",list);
        request.setAttribute("_SEARCH_ITEM_DISPLAY",request.getParameter("_SEARCH_ITEM_DISPLAY"));

        request.setAttribute("listUrl",request.getContextPath()+"/ezadmin/list/list-"+ENCRYPT_LIST_ID);
        // return "list/list";
        if(Utils.getLog()!=null) {
            Utils.addLog("结束执行列表 list_id=" + listId + ",总共耗时：" + (System.currentTimeMillis() - start) + "ms");
        }
        return EzResult.instance().data(dataList).count(dataList.size());
    }
//    @EzMapping("treeapi.html")
//    public void treeapi(HttpServletRequest request, HttpServletResponse response) throws Exception {
//        String listId =Utils.trimNull(request.getAttribute("LIST_ID")) ;
//        String ENCRYPT_LIST_ID =Utils.trimNull(request.getAttribute("ENCRYPT_LIST_ID")) ;
//        long start=System.currentTimeMillis();
//        if(Utils.getLog()!=null) {
//            Utils.addLog("开始执行列表 list_id=" + listId);
//        }
//        Map<String, Object> requestParamMap =requestToMap(request);
//
//        Map<String, String> sessionParamMap = sessionToMap(request.getSession());
//
//        EzList list = new DefaultEzList( ENCRYPT_LIST_ID, bootstrap.getOriginDataSource(), requestParamMap,sessionParamMap);
//        if (list.getEzListDto()==null) {
//            if(Utils.getLog()!=null){
//                Utils.addLog("cannot find list where list_id="+listId);
//            }
//            notFound(true,request,response)  ;
//        }
//
//        if(bootstrap.getDataSourceByKey(list.getEzListDto().getDataSource())==null){
//            throw new IllegalStateException("数据库配置错误"+list.getEzListDto().getDataSource());
//        }
//        list.renderHtml();
//        request.setAttribute("data",list.getEzListDto());
//        request.setAttribute("_SEARCH_ITEM_DISPLAY",request.getParameter("_SEARCH_ITEM_DISPLAY"));
//
//        request.setAttribute("listUrl",request.getContextPath()+"/ezadmin/list/list-"+ENCRYPT_LIST_ID);
//        // return "list/list";
//        if(Utils.getLog()!=null) {
//            Utils.addLog("结束执行列表 list_id=" + listId + ",总共耗时：" + (System.currentTimeMillis() - start) + "ms");
//        }
//        request.setAttribute("_ID", request.getParameter("_ID"));
//        request.setAttribute("_PARENT_ID", request.getParameter("_PARENT_ID"));
//        List<TreeVO> treeVOList=new ArrayList<>();
//        for (int i = 0; i < list.getEzListDto().getDataList().size(); i++) {
//            Map<String,String> row=list.getEzListDto().getDataList().get(i);
//            TreeVO root2=new TreeVO();
//            root2.setId(row.get("_ID"));
//            root2.setText(row.get("TEXT"));
//            root2.setChildren(Boolean.parseBoolean(row.get("CHILDREN")));
//            treeVOList.add(root2);
//        }
//
//
//        print(JSONUtils.toJSONString(treeVOList),response);
//
//    }
//    @EzMapping("sum.html")
//    public EzResult sum(HttpServletRequest request, HttpServletResponse response) throws Exception {
//        String listId =Utils.trimNull(request.getAttribute("LIST_ID")) ;
//        String ENCRYPT_LIST_ID =Utils.trimNull(request.getAttribute("ENCRYPT_LIST_ID")) ;
//        long start=System.currentTimeMillis();
//        if(Utils.getLog()!=null) {
//            Utils.addLog("开始执行列表 sum  list_id=" + listId);
//        }
//        Map<String, Object> requestParamMap =requestToMap(request);
//
//        Map<String, String> sessionParamMap = sessionToMap(request.getSession());
//        requestParamMap.put("EZ_SUM_FLAG","1");
//
//        EzList list = new SumEzList(NumberUtils.toInt(listId),ENCRYPT_LIST_ID, bootstrap.getOriginDataSource(), requestParamMap,sessionParamMap);
//        if (list.getEzListDto()==null) {
//            if(Utils.getLog()!=null){
//                Utils.addLog("cannot find list where list_id="+listId);
//            }
//            return EzResult.instance().data(404);
//        }
//
//        if(bootstrap.getDataSourceByKey(list.getEzListDto().getDataSource())==null){
//            throw new IllegalStateException("数据库配置错误"+list.getEzListDto().getDataSource());
//        }
//        list.renderHtml();
//        request.setAttribute("data",list.getEzListDto());
//        request.setAttribute("_SEARCH_ITEM_DISPLAY",request.getParameter("_SEARCH_ITEM_DISPLAY"));
//
//        request.setAttribute("listUrl",request.getContextPath()+"/ezadmin/list/list-"+ENCRYPT_LIST_ID);
//        // return "list/list";
//        if(Utils.getLog()!=null) {
//            Utils.addLog("结束执行列表 list_id=" + listId + ",总共耗时：" + (System.currentTimeMillis() - start) + "ms");
//        }
//        return EzResult.instance().data(list.getEzListDto().getDataList().get(0)).count(list.getEzListDto().getDataList().size());
//    }
    @EzMapping("selectCols.html")
    public String selectCols(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String ENCRYPT_LIST_ID =Utils.trimNull(request.getAttribute("ENCRYPT_LIST_ID")) ;
        Map<String, Object> requestParamMap =requestToMap(request);
        Map<String, String> sessionParamMap = sessionToMap(request.getSession());
        requestParamMap.put("default_empty","1");
        Map<String, Object> list=new HashMap<>();
        if(StringUtils.isNotBlank(ENCRYPT_LIST_ID)){
            list=   JSONUtils.parseObjectMap(listService.selectAllListById(ENCRYPT_LIST_ID))  ;
        }
         List<Map<String,Object>> searchList=(List<Map<String,Object>>)list.get("search");
         List<Map<String,Object>> colList=(List<Map<String,Object>>)list.get("col");
          request.setAttribute("fromSearchField",searchList);
         request.setAttribute("fromColField",colList);
        request.setAttribute("IS_DEBUG",request.getParameter("IS_DEBUG"));
        request.setAttribute("_EZ_SERVER_NAME",  "//"+request.getServerName()+":"+request.getServerPort());
        return "layui/pages/custom_cols_cache";
    }
//    @EzMapping("selectColsSubmit.html")
//    public EzResult selectColsSave(HttpServletRequest request, HttpServletResponse response) throws Exception {
//        String ENCRYPT_LIST_ID =Utils.trimNull(request.getAttribute("ENCRYPT_LIST_ID")) ;
//        Map<String, Object> requestParamMap =requestToMap(request);
//        Map<String, String> sessionParamMap = sessionToMap(request.getSession());
//
//        List<String> search= JSONUtils.parseArray(request.getParameter("search"),String.class);
//        List<String> col=JSONUtils.parseArray(request.getParameter("column"),String.class);
//        String fixNumber=request.getParameter("fixNumber");
//        String fixNumberRight=request.getParameter("fixNumberRight");
//
//        ListDao.getInstance().updateOrder(ENCRYPT_LIST_ID,search,col,fixNumber,fixNumberRight);
//
//
//        return EzResult.instance();
//    }

    @EzMapping("demo.html")
    public String demo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return  "layui/pages/demo";
    }

    @EzMapping("navs.html")
    public void navs(HttpServletRequest request, HttpServletResponse response) throws Exception {
        InitVO vo=new InitVO()  ;
        List<Info> toproot=new ArrayList<>();

        List<Info> root=new ArrayList<>();

        Info listN=new Info();
        listN.setTitle("列表管理");
        listN.setId("1");
        listN.setPid("0");
        listN.setHref("/ezadmin/list/list-listHtml");
        root.add(listN);

        Info listF=new Info();
        listF.setTitle("表单管理");
        listF.setId("2");
        listF.setPid("0");
        listF.setHref("/ezadmin/list/list-formHtml");
        root.add(listF);

        Info TOP=new Info( );
        TOP.setTitle("系统管理");
        TOP.setChild(root);
        toproot.add(TOP);
        vo.homeInfo(EzBootstrap.instance().getSystemName(),"").
                logoInfo("","/ezadmin/index.html",EzBootstrap.instance().getConfig().get("logo")+"")
                .setMenuInfo(toproot);
        EzResult.instance().msg("0","ok")
                .data(vo).printJSONUtils(response);
    }

}
