package com.ezadmin.biz.list.controller;

import com.alibaba.fastjson.JSON;
import com.ezadmin.biz.base.controller.Info;
import com.ezadmin.biz.base.controller.InitVO;
import com.ezadmin.biz.dao.Dao;
import com.ezadmin.biz.dao.ListDao;
import com.ezadmin.biz.list.emmber.list.*;
import com.ezadmin.biz.list.service.ListService;
import com.ezadmin.biz.model.TreeVO;
import com.ezadmin.common.EzAdminRuntimeException;
import com.ezadmin.common.annotation.EzMapping;
import com.ezadmin.common.constants.RequestParamConstants;
import com.ezadmin.common.constants.SessionConstants;
import com.ezadmin.common.enums.ExceptionCode;
import com.ezadmin.common.utils.*;
import com.ezadmin.plugins.sqlog.EzSqlogDataSource;
import com.ezadmin.web.EzResult;
import com.ezadmin.EzBootstrap;
import com.ezadmin.biz.base.controller.BaseController;


import org.apache.commons.lang.exception.ExceptionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.util.*;

@EzMapping("/ezadmin/list/")
public class ListController extends BaseController {

    EzBootstrap bootstrap= EzBootstrap.instance();

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

            EzList list = new DefaultEzList( ENCRYPT_LIST_ID, bootstrap.getOriginDataSource(), requestParamMap,sessionParamMap);
            if (list.getEzListDto() == null) {
                if(logger.isDebugEnabled()){
                    logger.debug("EZADMIN LIST={} 没有找到配置文件 ",ENCRYPT_LIST_ID);
                }
                EzResult.instance().code("404").setMessage("没有找到配置文件" +ENCRYPT_LIST_ID)
                        .setSuccess(false)
                        .printJSONUtils(response);
                return "404";
            } else {
//                if (EzBootstrap.instance().getDataSourceByKey(list.getEzListDto().getDataSource()) == null&&!StringUtils.equalsIgnoreCase("system",list.getEzListDto().getDataSource())) {
//                    logger.info("ezadmin 数据库配置错误{}",ENCRYPT_LIST_ID);
//                    EzResult.instance().code("404").setMessage("数据库配置错误" + list.getEzListDto().getDataSource())
//                            .setSuccess(false)
//                            .printJSONUtils(response);
//                    return "404";
//                }
                if(logger.isDebugEnabled()){
                    logger.debug("EZADMIN LIST={} 开始渲染HTML ",ENCRYPT_LIST_ID);
                }
                list.renderHtml();
                if(logger.isDebugEnabled()){
                    logger.debug("EZADMIN LIST={} 结束渲染HTML  ",ENCRYPT_LIST_ID);
                }
                request.setAttribute("data", list.getEzListDto());
                request.setAttribute(RequestParamConstants._SEARCH_ITEM_DISPLAY, request.getParameter("_SEARCH_ITEM_DISPLAY"));
                request.setAttribute("_EZ_MOBILE_FLAG",request.getParameter("_EZ_MOBILE_FLAG"));

                request.setAttribute("_EZ_LIST_EMPTY_NAME",list.getEzListDto().getListEmptyName());

                request.setAttribute("listUrl", request.getContextPath()+"/ezadmin/list/list-" + ENCRYPT_LIST_ID);
                request.setAttribute("EZ_NAME",list.getEzListDto().getListName() );

                request.setAttribute("_EZ_SERVER_NAME",  "//"+request.getServerName()+":"+request.getServerPort());

                // ThymeleafUtils.writeHtml(list.getEzListDto().getTemplateBodyList(), request, response);
//                if (Utils.getLog() != null) {
//                    Utils.addLog("渲染列表结束" + ENCRYPT_LIST_ID);
//                }

                if(logger.isDebugEnabled()){
                    logger.debug("EZADMIN LIST={} 结束执行列表  ,总共耗时：{} ms",ENCRYPT_LIST_ID,(System.currentTimeMillis() - start) );
                }
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
    private ListService listService = EzProxy.singleInstance(ListService.class);

    @EzMapping("doOrder.html")
    public EzResult doOrder(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String formId =  Utils.trimNull(request.getAttribute("FORM_ID"));
        String ENCRYPT_LIST_ID = Utils.trimNull(request.getAttribute("ENCRYPT_LIST_ID"));

        try {
            if (StringUtils.isBlank(formId)&&StringUtils.isBlank(ENCRYPT_LIST_ID)) {
                return EzResult.instance().code("404");
            }
            Map<String, String> listDb = listService.selectListById(""  ,ENCRYPT_LIST_ID);

            DataSource dataSource= EzBootstrap.instance().getDataSourceByKey(StringUtils.lowerCase(listDb.get(JsoupUtil.DATASOURCE)) );

            //配置项
            String orderTable = Utils.trimNullDefault(listDb.get("ordertable"),"");
            String orderId = Utils.trimNullDefault(listDb.get("orderid"),"ID");
            String orderColumn = Utils.trimNullDefault(listDb.get("ordercolumn"),"DISPLAY_ORDER");

            String displayOrderSelect = Utils.trimNull(listDb.get("displayOrderSelect") );
            String displayOrderUpdate = Utils.trimNull(listDb.get("displayOrderUpdate"));

            //业务参数
            String dataId=request.getParameter("orderId");
            String dataDisplayOrder=request.getParameter("displayOrder");

            //再换一种算法
            String allSql="select " +orderId +", "+orderColumn +" from "+orderTable +" where IS_DEL=0  " +
                    " and "+orderId+"!= ?  order by "+orderColumn+" asc  limit 1000";
            if(StringUtils.isNotBlank(displayOrderSelect)){
                allSql=displayOrderSelect;
            }

            String updateSql=" update "+orderTable+ " set "+orderColumn+ "=?  where "+ orderId+" =? ";

            if(StringUtils.isNotBlank(displayOrderUpdate)){
                updateSql=displayOrderUpdate;
            }

            //把当前这条数据的order改正确
            Dao.getInstance().executeUpdate(dataSource,StringUtils.safeDb(updateSql),new Object[]{dataDisplayOrder,dataId});
            if(NumberUtils.toInt(dataDisplayOrder)==0){
                return EzResult.instance() ;
            }

            //得到一个list
            List<Map<String,String>> all= Dao.getInstance().executeQueryString(dataSource,StringUtils.safeDb(allSql),new Object[]{dataId});
            Map<String,String> map=new HashMap<>();
            map.put(orderId,dataId);
            map.put(orderColumn,dataDisplayOrder);
            all.add(NumberUtils.toInt(dataDisplayOrder)-1,map);

            for (int i = 0; i < all.size(); i++) {
                Map<String,String> data=all.get(i);
                //数据库现在的
                String id=data.get(orderId);
                String displayOrder=data.get(orderColumn);
                if(!StringUtils.equals(displayOrder,(i+1)+"")){
                    Dao.getInstance().executeUpdate(dataSource,StringUtils.safeDb(updateSql),new Object[]{(i+1),id});
                }
            }
            return EzResult.instance() ;
        }
        catch (Exception e) {
            logger.error("ezform doOrder error {} {}  ",formId,ENCRYPT_LIST_ID ,e);
            return EzResult.instance().setSuccess(false).code("500").setMessage("服务器异常");
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

            CountEzList list = new CountEzList( ENCRYPT_LIST_ID,   requestParamMap,sessionParamMap);
            if (list.getEzListDto() == null) {
                if (Utils.getLog() != null) {
                    Utils.addLog("cannot find list where list_id=" + ENCRYPT_LIST_ID);
                }
                return  EzResult.instance().code("404");

            } else {
//                if (EzBootstrap.instance().getDataSourceByKey(list.getEzListDto().getDataSource()) == null) {
//                    EzResult.instance().code("404").setMessage("数据库配置错误" + list.getEzListDto().getDataSource())
//                            .setSuccess(false)
//                            .printJSONUtils(response);
//                    return  EzResult.instance().code("404");
//                }
                request.setAttribute("data", list.getEzListDto().getPage());
                request.setAttribute("_EZ_MOBILE_FLAG",request.getParameter("_EZ_MOBILE_FLAG"));
                request.setAttribute("listUrl", request.getContextPath()+"/ezadmin/list/list-" + ENCRYPT_LIST_ID);
                request.setAttribute("_EZ_SERVER_NAME",  "//"+request.getServerName()+":"+request.getServerPort());

            }
            return EzResult.instance().data(list.getEzListDto().getPage())
                    .count(list.getEzListDto().getPage().getTotalRecord());
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
            requestParamMap.put("perPageInt",100000);
            Map<String, String> sessionParamMap = sessionToMap(request.getSession());

            EzList list = new TreeEzList( ENCRYPT_LIST_ID, bootstrap.getOriginDataSource(), requestParamMap,sessionParamMap);
            if (list.getEzListDto() == null) {
                if(logger.isDebugEnabled()){
                    logger.debug("EZADMIN LIST={} 没有找到配置文件 ",ENCRYPT_LIST_ID);
                }
                EzResult.instance().code("404").setMessage("没有找到配置文件" +ENCRYPT_LIST_ID)
                        .setSuccess(false)
                        .printJSONUtils(response);
                return "404";
            } else {
                if(logger.isDebugEnabled()){
                    logger.debug("EZADMIN LIST={} 开始渲染HTML ",ENCRYPT_LIST_ID);
                }
                list.renderHtml();
                if(logger.isDebugEnabled()){
                    logger.debug("EZADMIN LIST={} 结束渲染HTML  ",ENCRYPT_LIST_ID);
                }
                //组装col
                List col=new ArrayList();

                col.add(JSONUtils.parseObjectMap("{\"type\": \"checkbox\", \"fixed\": \"left\"}"));

                for (int i = 0; i < list.getEzListDto().getColumnItemList().size(); i++) {
                    String laydata=list.getEzListDto().getColumnItemList()

                            .get(i).get(JsoupUtil.LAYDATA);
                    col.add(JSONUtils.parseObjectMap(laydata));
                }
                col.add(JSONUtils.parseObjectMap("{ fixed: \"right\", title: \"操作\", width: 181, align: \"center\", toolbar: \"#TPL-treeTable-demo-tools\"} "));


                request.setAttribute("data", list.getEzListDto());
                request.setAttribute("coldata",JSONUtils.toJSONString(col));
                request.setAttribute(RequestParamConstants._SEARCH_ITEM_DISPLAY, request.getParameter("_SEARCH_ITEM_DISPLAY"));
                request.setAttribute("_EZ_MOBILE_FLAG",request.getParameter("_EZ_MOBILE_FLAG"));

                request.setAttribute("_EZ_LIST_EMPTY_NAME",list.getEzListDto().getListEmptyName());

                request.setAttribute("listUrl", request.getContextPath()+"/ezadmin/list/tree-" + ENCRYPT_LIST_ID);
                request.setAttribute("EZ_NAME",list.getEzListDto().getListName() );

                request.setAttribute("_EZ_SERVER_NAME",  "//"+request.getServerName()+":"+request.getServerPort());

                // ThymeleafUtils.writeHtml(list.getEzListDto().getTemplateBodyList(), request, response);
//                if (Utils.getLog() != null) {
//                    Utils.addLog("渲染列表结束" + ENCRYPT_LIST_ID);
//                }

                if(logger.isDebugEnabled()){
                    logger.debug("EZADMIN LIST={} 结束执行列表  ,总共耗时：{} ms",ENCRYPT_LIST_ID,(System.currentTimeMillis() - start) );
                }
                return "layui/list/treelist";
            }
        }catch(Exception e){
            EzResult.instance().code("500").setMessage("服务器异常" +ExceptionUtils.getFullStackTrace(e))
                    .setSuccess(false)
                    .printJSONUtils(response);
            logger.error("EZADMIN LIST={} 服务器异常 ",ENCRYPT_LIST_ID,e);
            return "500";
        }
    }
    @EzMapping("treedata.html")
    public EzResult listtree(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
        requestParamMap.put("perPageInt",100000);
        EzList list = new DefaultEzList( ENCRYPT_LIST_ID, bootstrap.getOriginDataSource(), requestParamMap,sessionParamMap);
        if (list.getEzListDto()==null) {
            if(Utils.getLog()!=null){
                Utils.addLog("cannot find list where list_id="+listId);
            }
            notFound(false,request,response);

        }else {
            if (EzBootstrap.instance().getDataSourceByKey(list.getEzListDto().getDataSource()) == null) {
                throw new IllegalStateException("数据库配置错误" + list.getEzListDto().getDataSource());
            }

        }
        return EzResult.instance().data(  list.getEzListDto().getDataList());
    }

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


        EzList list = new DefaultEzList( ENCRYPT_LIST_ID, bootstrap.getOriginDataSource(), requestParamMap,sessionParamMap);
        CountEzList lis2t = new CountEzList( ENCRYPT_LIST_ID,  requestParamMap,sessionParamMap);

        if(Utils.getLog()!=null) {
            Utils.addLog("requestParamMap " + requestParamMap);

        }
        if(Utils.getLog()!=null) {
            Utils.addLog("sessionParamMap " + JSONUtils.toJSONString(sessionParamMap));
        }
        if (list.getEzListDto()==null) {
            if(Utils.getLog()!=null){
                Utils.addLog("cannot find list where list_id="+ENCRYPT_LIST_ID);
            }
            response.getWriter().write("404");
            return ;
        }
        if(bootstrap.getDataSourceByKey(list.getEzListDto().getDataSource())==null){
            Utils.addLog("cannot find database where list_id="+ENCRYPT_LIST_ID+",datasource="+list.getEzListDto().getDataSource());
            return;
        }
        list.renderHtml();
        request.setAttribute("data",list.getEzListDto());
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

        EzList list = new DefaultEzList( ENCRYPT_LIST_ID, bootstrap.getOriginDataSource(), requestParamMap,sessionParamMap);
        if (list.getEzListDto()==null) {
            if(Utils.getLog()!=null){
                Utils.addLog("cannot find list where list_id="+listId);
            }
            return EzResult.instance().data(404);
        }

        if(bootstrap.getDataSourceByKey(list.getEzListDto().getDataSource())==null){
            throw new IllegalStateException("数据库配置错误"+list.getEzListDto().getDataSource());
        }
        list.renderHtml();
        request.setAttribute("data",list.getEzListDto());
        request.setAttribute("_SEARCH_ITEM_DISPLAY",request.getParameter("_SEARCH_ITEM_DISPLAY"));

        request.setAttribute("listUrl",request.getContextPath()+"/ezadmin/list/list-"+ENCRYPT_LIST_ID);
        // return "list/list";
        if(Utils.getLog()!=null) {
            Utils.addLog("结束执行列表 list_id=" + listId + ",总共耗时：" + (System.currentTimeMillis() - start) + "ms");
        }
        return EzResult.instance().data(list.getEzListDto().getDataList()).count(list.getEzListDto().getDataList().size());
    }
    @EzMapping("treeapi.html")
    public void treeapi(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String listId =Utils.trimNull(request.getAttribute("LIST_ID")) ;
        String ENCRYPT_LIST_ID =Utils.trimNull(request.getAttribute("ENCRYPT_LIST_ID")) ;
        long start=System.currentTimeMillis();
        if(Utils.getLog()!=null) {
            Utils.addLog("开始执行列表 list_id=" + listId);
        }
        Map<String, Object> requestParamMap =requestToMap(request);

        Map<String, String> sessionParamMap = sessionToMap(request.getSession());

        EzList list = new DefaultEzList( ENCRYPT_LIST_ID, bootstrap.getOriginDataSource(), requestParamMap,sessionParamMap);
        if (list.getEzListDto()==null) {
            if(Utils.getLog()!=null){
                Utils.addLog("cannot find list where list_id="+listId);
            }
            notFound(true,request,response)  ;
        }

        if(bootstrap.getDataSourceByKey(list.getEzListDto().getDataSource())==null){
            throw new IllegalStateException("数据库配置错误"+list.getEzListDto().getDataSource());
        }
        list.renderHtml();
        request.setAttribute("data",list.getEzListDto());
        request.setAttribute("_SEARCH_ITEM_DISPLAY",request.getParameter("_SEARCH_ITEM_DISPLAY"));

        request.setAttribute("listUrl",request.getContextPath()+"/ezadmin/list/list-"+ENCRYPT_LIST_ID);
        // return "list/list";
        if(Utils.getLog()!=null) {
            Utils.addLog("结束执行列表 list_id=" + listId + ",总共耗时：" + (System.currentTimeMillis() - start) + "ms");
        }
        request.setAttribute("_ID", request.getParameter("_ID"));
        request.setAttribute("_PARENT_ID", request.getParameter("_PARENT_ID"));
        List<TreeVO> treeVOList=new ArrayList<>();
        for (int i = 0; i < list.getEzListDto().getDataList().size(); i++) {
            Map<String,String> row=list.getEzListDto().getDataList().get(i);
            TreeVO root2=new TreeVO();
            root2.setId(row.get("_ID"));
            root2.setText(row.get("TEXT"));
            root2.setChildren(Boolean.parseBoolean(row.get("CHILDREN")));
            treeVOList.add(root2);
        }


        print(JSONUtils.toJSONString(treeVOList),response);

    }
    @EzMapping("sum.html")
    public EzResult sum(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String listId =Utils.trimNull(request.getAttribute("LIST_ID")) ;
        String ENCRYPT_LIST_ID =Utils.trimNull(request.getAttribute("ENCRYPT_LIST_ID")) ;
        long start=System.currentTimeMillis();
        if(Utils.getLog()!=null) {
            Utils.addLog("开始执行列表 sum  list_id=" + listId);
        }
        Map<String, Object> requestParamMap =requestToMap(request);

        Map<String, String> sessionParamMap = sessionToMap(request.getSession());
        requestParamMap.put("EZ_SUM_FLAG","1");

        EzList list = new SumEzList(NumberUtils.toInt(listId),ENCRYPT_LIST_ID, bootstrap.getOriginDataSource(), requestParamMap,sessionParamMap);
        if (list.getEzListDto()==null) {
            if(Utils.getLog()!=null){
                Utils.addLog("cannot find list where list_id="+listId);
            }
            return EzResult.instance().data(404);
        }

        if(bootstrap.getDataSourceByKey(list.getEzListDto().getDataSource())==null){
            throw new IllegalStateException("数据库配置错误"+list.getEzListDto().getDataSource());
        }
        list.renderHtml();
        request.setAttribute("data",list.getEzListDto());
        request.setAttribute("_SEARCH_ITEM_DISPLAY",request.getParameter("_SEARCH_ITEM_DISPLAY"));

        request.setAttribute("listUrl",request.getContextPath()+"/ezadmin/list/list-"+ENCRYPT_LIST_ID);
        // return "list/list";
        if(Utils.getLog()!=null) {
            Utils.addLog("结束执行列表 list_id=" + listId + ",总共耗时：" + (System.currentTimeMillis() - start) + "ms");
        }
        return EzResult.instance().data(list.getEzListDto().getDataList().get(0)).count(list.getEzListDto().getDataList().size());
    }
    @EzMapping("selectCols.html")
    public String selectCols(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String ENCRYPT_LIST_ID =Utils.trimNull(request.getAttribute("ENCRYPT_LIST_ID")) ;
        Map<String, Object> requestParamMap =requestToMap(request);
        Map<String, String> sessionParamMap = sessionToMap(request.getSession());
        requestParamMap.put("default_empty","1");
        EzList list = new EmptyEzList( ENCRYPT_LIST_ID, bootstrap.getOriginDataSource(), requestParamMap,sessionParamMap);
        request.setAttribute("fromSearchField",list.getEzListDto().getSearchItemList());

        request.setAttribute("fromColField",list.getEzListDto().getColumnItemList());
        request.setAttribute("IS_DEBUG",request.getParameter("IS_DEBUG"));
        request.setAttribute("_EZ_SERVER_NAME",  "//"+request.getServerName()+":"+request.getServerPort());
        return "layui/pages/custom_cols_cache";
    }
    @EzMapping("selectColsSubmit.html")
    public EzResult selectColsSave(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String ENCRYPT_LIST_ID =Utils.trimNull(request.getAttribute("ENCRYPT_LIST_ID")) ;
        Map<String, Object> requestParamMap =requestToMap(request);
        Map<String, String> sessionParamMap = sessionToMap(request.getSession());

        List<String> search= JSONUtils.parseArray(request.getParameter("search"),String.class);
        List<String> col=JSONUtils.parseArray(request.getParameter("column"),String.class);
        String fixNumber=request.getParameter("fixNumber");
        String fixNumberRight=request.getParameter("fixNumberRight");

        ListDao.getInstance().updateOrder(ENCRYPT_LIST_ID,search,col,fixNumber,fixNumberRight);


        return EzResult.instance();
    }

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
