package com.ezadmin.biz.list.controller;


import com.alibaba.fastjson.JSON;
import com.ezadmin.biz.emmber.list.ColumnVO;
import com.ezadmin.biz.list.controller.ListController;
import com.ezadmin.biz.list.emmber.list.ExportEzList;
import com.ezadmin.biz.list.service.ListService;
import com.ezadmin.common.annotation.EzConfig;
import com.ezadmin.common.annotation.EzMapping;
import com.ezadmin.common.constants.SessionConstants;
import com.ezadmin.common.utils.*;
import com.ezadmin.web.Config;
import com.ezadmin.EzBootstrap;
import com.ezadmin.biz.base.controller.BaseController;
import com.ezadmin.biz.base.service.CoreService;

import com.ezadmin.biz.list.emmber.list.EzList;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@EzMapping("/ezadmin")
public class ExportController extends BaseController {
    Logger log = LoggerFactory.getLogger(ListController.class);
    ListService listService = EzProxy.singleInstance(ListService.class);
    CoreService coreService = EzProxy.singleInstance(CoreService.class);
    String exportPath = "/data/export/";
    @EzConfig
    Config config;
    private final Map<String,String> existList=new ConcurrentHashMap<String,String>();
    EzBootstrap bootstrap=EzBootstrap.instance();


    @EzMapping("/list/export.html")
    public void listCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String listId = Utils.trimNull(request.getAttribute("LIST_ID"));
        String ENCRYPT_LIST_ID =Utils.trimNull(request.getAttribute("ENCRYPT_LIST_ID")) ;
        String _BLANK_PARAM_COLUMN =Utils.trimNull(request.getParameter("_BLANK_PARAM_COLUMN")) ;
        String orderedColumn[]=null;
        if(StringUtils.isNotBlank(_BLANK_PARAM_COLUMN)){
            orderedColumn=_BLANK_PARAM_COLUMN.split(",");
        }
        String key=listId+"_"+ENCRYPT_LIST_ID;
        try {
            int currentCount=0;

            String sessionUserId = Utils.trimNull(request.getSession().getAttribute(SessionConstants.EZ_SESSION_USER_ID_KEY));
            String username = Utils.trimNull(request.getSession().getAttribute(SessionConstants.EZ_SESSION_USER_NAME_KEY));
            String ip = IpUtils.getRealIp(request);
            if(existList.containsKey(key)){
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");
                Map<String, String> map = new HashMap<String, String>();
                map.put("status", "failure");
                map.put("message", key+"导出失败：当前已有人员"+existList.get(key)+"正在导出此列表，请等待对方先导出完成。"  );
                response.getWriter().println(JSON.toJSONString(map));
                return;
            }else{
                existList.put(key,sessionUserId+username+ip);
            }
            Utils.addLog("start list html list_id=" + listId);
            Map<String, Object> requestParamMap = requestToMap(request);
            requestParamMap.put("currentPage", "1");
            requestParamMap.put("perPageInt", "1000");
            EzList list = new ExportEzList(NumberUtils.toInt(listId),ENCRYPT_LIST_ID, bootstrap.getOriginDataSource(), requestParamMap);

            if (bootstrap.getDataSourceByKey(list.getEzListDto().getDataSource()) == null) {
                throw new IllegalStateException("数据库配置错误 " +ENCRYPT_LIST_ID+" "+ list.getEzListDto().getDataSource());
            }
            //boolean CUSTOM_FLAG=listService.removeNotSelectColumnData(list,sessionUserId,ENCRYPT_LIST_ID);

            if(orderedColumn!=null&&orderedColumn.length>0){
                //Iterator<ColumnVO> columnVOIterator=list.getEzListDto().getColumnItemList().iterator();

                List<Map<String,String>> orderedColList=new ArrayList<>();
                for (int j = 0; j < orderedColumn.length; j++) {
                    for (int i = 0; i < list.getEzListDto().getColumnItemList().size(); i++) {
                            if(orderedColumn[j].equalsIgnoreCase(list.getEzListDto().getColumnItemList().get(i).get(JsoupUtil.ITEM_NAME))){
                                orderedColList.add(list.getEzListDto().getColumnItemList().get(i));
                                break;
                            }
                    }
                }
                list.getEzListDto().setColumnItemList(orderedColList);
            }

            List<List<Object>> data = list.renderExcel();
            if (list.getPagination().getTotalPage() > 1 && Utils.isNotEmpty(data)) {
                for (int i = 2; i <= list.getPagination().getTotalPage(); i++) {
                    list.getPagination().setCurrentPage(i);
                    list.getEzListDto().setDataList(list.loadingData());
                    List<List<Object>> dataTemp = list.renderExcel();
                    if (!Utils.isNotEmpty(dataTemp)) {
                        break;
                    }
                    data.addAll(dataTemp);
                }
            }
            List<List<String>> head = new ArrayList<List<String>>();
            List<String> head0 = new ArrayList<String>();
            head0.add("序号");
            head.add(head0);
            for (int i = 0; i < list.getEzListDto().getColumnItemList().size(); i++) {
                List<String> head1 = new ArrayList<String>();
                head1.add(list.getEzListDto().getColumnItemList().get(i).get(JsoupUtil.LABEL));
                head.add(head1);
            }

            try {
                EzBootstrap.instance().getExport().export(list.getEzListDto().getListName(),head,data,response);
                //记录日志
                logger.info("ezadmin export {} {} {} {}",sessionUserId,ip, list.getEzListDto().getListName());
            } catch (Exception e) {
                logger.error("",e);
                // 重置response
                response.reset();
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");
                Map<String, String> map = new HashMap<String, String>();
                map.put("status", "failure");
                map.put("message", "下载文件失败" + e.getMessage());
                response.getWriter().println(JSON.toJSONString(map));
            }
        }catch (Exception e){
            logger.error("",e);
        }finally {

            existList.remove(key);
        }
        // return "OK";
    }

    /**
     * @param request  request.getParameter("LOG_ID")
     * @param response
     * @throws Exception
     */
    @EzMapping("/list/downloadExport.html")
    public void downloadExport(HttpServletRequest request, HttpServletResponse response) throws Exception {
        OutputStream outputStream = response.getOutputStream();
        try {
            String logId = request.getParameter("LOG_ID");
            String userID = Utils.trimNull(request.getSession().getAttribute(SessionConstants.EZ_SESSION_USER_ID_KEY));

            Map<String, Object> file = listService.selectLogById(logId, userID);
            byte[] bytes = ( byte[]) file.get("FILE_BLOB");
            String fileName = Utils.trimNull(file.get("FILE_NAME"));
            String headStr = "attachment; filename=\"" + new String(fileName.getBytes(), "iso8859-1") + "\"";
            response.setContentType("application/octet-stream");
            response.setContentType("application/x-download");
            response.setHeader("Content-Disposition", headStr);
            outputStream.write(bytes );
        } catch (Exception e) {
            logger.error("", e);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html;charset=UTF-8");
            Map<String, String> map = new HashMap<>();
            map.put("code", "404");
            map.put("message", "下载异常，" + e.getMessage());
            outputStream.write(JSONUtils.toJSONString(map).getBytes());
        }
    }
}
