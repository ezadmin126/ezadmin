 package com.ezadmin.controller;
//
//
import com.alibaba.fastjson.JSON;
import com.ezadmin.service.ListService;
import com.ezadmin.common.annotation.EzMapping;
import com.ezadmin.common.constants.SessionConstants;
import com.ezadmin.common.utils.*;
 import com.ezadmin.EzBootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ConcurrentHashMap;
import com.ezadmin.common.utils.EzProxy;
import com.ezadmin.common.utils.IpUtils;
import com.ezadmin.common.utils.JsoupUtil;
import com.ezadmin.common.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

 @EzMapping("/ezadmin")
 public class ExportController extends BaseController {
    Logger log = LoggerFactory.getLogger(ListController.class);
    ListService listService = EzProxy.singleInstance(ListService.class);
     String exportPath = "/data/export/";

    private final Map<String,String> existList=new ConcurrentHashMap<String,String>();
    EzBootstrap bootstrap=EzBootstrap.instance();
//
//
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
            Map<String, String> sessionParamMap = sessionToMap(request.getSession());

            requestParamMap.put("currentPage", "1");
            requestParamMap.put("perPageInt", "1000");
          //  EzList list = new ExportEzList(NumberUtils.toInt(listId),ENCRYPT_LIST_ID, bootstrap.getOriginDataSource(), requestParamMap);
            Map<String, Object> list=new HashMap<>();
            if(StringUtils.isNotBlank(ENCRYPT_LIST_ID)){
                list=   JSONUtils.parseObjectMap(listService.selectAllListById(ENCRYPT_LIST_ID))  ;
            }
            listService.fillListById(list,requestParamMap,sessionParamMap);

            //boolean CUSTOM_FLAG=listService.removeNotSelectColumnData(list,sessionUserId,ENCRYPT_LIST_ID);
            Map<String,Object> coreMap=(Map<String,Object>)list.get("core");
            List<Map<String,Object>> searchList=(List<Map<String,Object>>)list.get("search");
            List<Map<String,Object>> tabList=(List<Map<String,Object>>)list.get("tab");
            List<Map<String,Object>> tablebtnList=(List<Map<String,Object>>)list.get("tablebtn");
            List<Map<String,Object>> colList=(List<Map<String,Object>>)list.get("col");
            List<Map<String,Object>> rowList=new ArrayList<>();
            List<Map<String,Object>> rowbtnList=(List<Map<String,Object>>)list.get("rowbtn");

            if(orderedColumn!=null&&orderedColumn.length>0){
                List<Map<String,Object>> orderedColList=new ArrayList<>();
                for (int j = 0; j < orderedColumn.length; j++) {
                    for (int i = 0; i < colList.size(); i++) {
                        Map<String,Object> col=(Map<String,Object>)colList.get(i);
                        String itemName=Utils.getStringByObject(colList.get(i), JsoupUtil.ITEM_NAME);
                            if(orderedColumn[j].equalsIgnoreCase(itemName)){
                                orderedColList.add(col);
                                break;
                            }
                    }
                }
                colList=orderedColList;
                list.put("col",orderedColList);
            }
            Map<String, Object> pagination=(Map<String, Object>)list.get("page");
            List<Map<String, Object>> dataList=(List<Map<String, Object>>)coreMap.get("dataList");
            List<List<Object>> data=new ArrayList<>();
            fillExcelData(data,dataList,colList);


            if (NumberUtils.toInt(pagination.get("totalPage")+"") > 1 && Utils.isNotEmpty(dataList)) {
                for (int i = 2; i <= NumberUtils.toInt(pagination.get("totalPage")+""); i++) {
                    requestParamMap.put("currentPage", i);
                    Map<String, Object> listTemp=new HashMap<>();
                    if(StringUtils.isNotBlank(ENCRYPT_LIST_ID)){
                        listTemp=   JSONUtils.parseObjectMap(listService.selectAllListById(ENCRYPT_LIST_ID))  ;
                    }
                    listService.fillListById(listTemp,requestParamMap,sessionParamMap);
                    Map<String,Object> coreMapTemp=(Map<String,Object>)listTemp.get("core");
                    List<Map<String, Object>> dataTemp=(List<Map<String, Object>>)coreMapTemp.get("dataList");
                    fillExcelData(data,dataTemp,colList);
                }
            }
            List<List<String>> head = new ArrayList<List<String>>();
            List<String> head0 = new ArrayList<String>();
            head0.add("序号");
            head.add(head0);
            for (int i = 0; i < colList.size(); i++) {
                Map<String,Object> col=(Map<String,Object>)colList.get(i);

                if(StringUtils.equalsIgnoreCase(col.get(JsoupUtil.ITEM_NAME)+"","firstcol")){
                    continue;
                }
                List<String> head1 = new ArrayList<String>();
                head1.add(Utils.getStringByObject(col,JsoupUtil.LABEL));
                head.add(head1);
            }
            try {
                EzBootstrap.instance().getExport().export(Utils.getStringByObject(coreMap,"listname"),head,data,response);
                //记录日志
                logger.info("ezadmin export {} {} {} {}",sessionUserId,ip, Utils.getStringByObject(coreMap,"listname"));
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

     private void fillExcelData(List<List<Object>> data, List<Map<String, Object>> dataList,List<Map<String,Object>> colList) {
         for (int i = 0; i < dataList.size(); i++) {
             List<Object> row=new ArrayList<>();
             row.add(data.size()+1);
             for (int j = 0; j < colList.size(); j++) {
                 String itemName=Utils.getStringByObject(colList.get(j),JsoupUtil.ITEM_NAME);
                 if(StringUtils.equalsIgnoreCase(itemName,"firstcol")){
                    continue;
                 }
                row.add(dataList.get(i).get(itemName));
             }
             data.add(row);
         }
     }
//
//    /**
//     * @param request  request.getParameter("LOG_ID")
//      * @throws Exception
//     */
//    @EzMapping("/list/downloadExport.html")
//    public void downloadExport(HttpServletRequest request, HttpServletResponse response) throws Exception {
//        OutputStream outputStream = response.getOutputStream();
//        try {
//            String logId = request.getParameter("LOG_ID");
//            String userID = Utils.trimNull(request.getSession().getAttribute(SessionConstants.EZ_SESSION_USER_ID_KEY));
//
//            Map<String, Object> file = listService.selectLogById(logId, userID);
//            byte[] bytes = ( byte[]) file.get("FILE_BLOB");
//            String fileName = Utils.trimNull(file.get("FILE_NAME"));
//            String headStr = "attachment; filename=\"" + new String(fileName.getBytes(), "iso8859-1") + "\"";
//            response.setContentType("application/octet-stream");
//            response.setContentType("application/x-download");
//            response.setHeader("Content-Disposition", headStr);
//            outputStream.write(bytes );
//        } catch (Exception e) {
//            logger.error("", e);
//            response.setCharacterEncoding("UTF-8");
//            response.setContentType("text/html;charset=UTF-8");
//            Map<String, String> map = new HashMap<>();
//            map.put("code", "404");
//            map.put("message", "下载异常，" + e.getMessage());
//            outputStream.write(JSONUtils.toJSONString(map).getBytes());
//        }
//    }
 }
