 package top.ezadmin.controller;
import top.ezadmin.common.NotExistException;
import top.ezadmin.common.utils.*;
import top.ezadmin.service.ListService;
import top.ezadmin.common.annotation.EzMapping;
import top.ezadmin.common.constants.SessionConstants;
 import top.ezadmin.EzClientBootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.util.CollectionUtils;

 @EzMapping("/topezadmin")
 public class ExportController extends BaseController {
    Logger log = LoggerFactory.getLogger(ExportController.class);
    ListService listService = EzProxy.singleInstance(ListService.class);
    private final Map<String,String> existList=new ConcurrentHashMap<String,String>();

    @EzMapping("/list/export.html")
    public void listCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String listId = Utils.trimNull(request.getAttribute("LIST_ID"));
        String ENCRYPT_LIST_ID =Utils.trimNull(request.getAttribute("ENCRYPT_LIST_ID")) ;
        String _BLANK_PARAM_COLUMN =Utils.trimNull(request.getParameter("_BLANK_PARAM_COLUMN")) ;
        String EZ_PER_PAGE_SIZE =Utils.trimEmptyDefault(request.getParameter("EZ_PER_PAGE_SIZE"),"1000") ;
        String orderedColumn[]=null;
        if(StringUtils.isNotBlank(_BLANK_PARAM_COLUMN)){
            orderedColumn=_BLANK_PARAM_COLUMN.split(",");
        }
        String key=listId+"_"+ENCRYPT_LIST_ID;
        try {
            String sessionUserId = Utils.trimNull(request.getSession().getAttribute(SessionConstants.EZ_SESSION_USER_ID_KEY));
            String username = Utils.trimNull(request.getSession().getAttribute(SessionConstants.EZ_SESSION_USER_NAME_KEY));
            String ip = IpUtils.getRealIp(request);
            if(existList.containsKey(key)){
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");
                Map<String, String> map = new HashMap<String, String>();
                map.put("status", "failure");
                map.put("message", key+"导出失败：当前已有人员"+existList.get(key)+"正在导出此列表，请等待对方先导出完成。"  );
                response.getWriter().println(JSONUtils.toJSONString(map));
                return;
            }else{
                existList.put(key,sessionUserId+username+ip);
            }
            log.info("start export html list_id=" + listId);
            Map<String, Object> requestParamMap = requestToMap(request);
            Map<String, String> sessionParamMap = sessionToMap(request.getSession());

            requestParamMap.put("currentPage", "1");
            requestParamMap.put("perPageInt", EZ_PER_PAGE_SIZE);

            Map<String, Object> list=exportList(  ENCRYPT_LIST_ID,orderedColumn);
            Map<String,Object> coreMap=(Map<String,Object>)list.get("core");
            List<Map<String,Object>> colList=(List<Map<String,Object>>)list.get("col");

            listService.exportListById(list,requestParamMap,sessionParamMap);
            log.info("start finish load ez   list_id=" + listId);
            List<Map<String, Object>> dataList=(List<Map<String, Object>>)coreMap.get("dataList");
            if(CollectionUtils.isEmpty(dataList)){
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");
                Map<String, String> map = new HashMap<String, String>();
                map.put("status", "failure");
                map.put("message", "导出失败：未查询到可以导出的数据，请检查列表筛选项。"  );
                response.getWriter().println(JSONUtils.toJSONString(map));
                return;
            }
            List<List<Object>> data=new ArrayList<>();

            log.info("start fillpage  1 list_id=" + listId);
            fillExcelData(data,dataList,colList);
            log.info("ezadmin finishfill page:{} export {} {} {} {}",1,sessionUserId,ip, Utils.getStringByObject(coreMap,"listname"));
            int currentPage=2;
            while (Utils.isNotEmpty(dataList)&&dataList.size()>=NumberUtils.toInt(EZ_PER_PAGE_SIZE)) {
                requestParamMap.put("currentPage", currentPage++);
                Map<String, Object> listTemp   =exportList(  ENCRYPT_LIST_ID,orderedColumn);
                      //  JSONUtils.parseObjectMap(listService.selectPublishListById(ENCRYPT_LIST_ID)) ;
                listService.exportListById(listTemp,requestParamMap,sessionParamMap);
                Map<String,Object> coreMapTemp=(Map<String,Object>)listTemp.get("core");
                List<Map<String, Object>> dataTemp=(List<Map<String, Object>>)coreMapTemp.get("dataList");
                fillExcelData(data,dataTemp,colList);
                log.info("ezadmin finishfill page:{} export {} {} {} {}",currentPage,sessionUserId,ip, Utils.getStringByObject(coreMap,"listname"));
                dataList=dataTemp;
            }
            List<List<String>> head = new ArrayList<List<String>>();
            List<String> head0 = new ArrayList<String>();
            head0.add("序号");
            head0.add("");
            head0.add("");
            head.add(head0);
            for (int i = 0; i < colList.size(); i++) {
                Map<String,Object> col=(Map<String,Object>)colList.get(i);
                if(StringUtils.equalsIgnoreCase(col.get(JsoupUtil.HEAD_PLUGIN_CODE)+"","th-checkbox")
                        ||StringUtils.equalsIgnoreCase(col.get(JsoupUtil.BODY_PLUGIN_CODE)+"","td-pic")
                        ||StringUtils.equalsIgnoreCase(col.get(JsoupUtil.HEAD_PLUGIN_CODE)+"","th-numbers")
                        ||StringUtils.equalsIgnoreCase(col.get(JsoupUtil.BODY_PLUGIN_CODE)+"","td-radio")

                ){
                    continue;
                }

                List<String> head1 = new ArrayList<String>();
                //文案
                head1.add(Utils.getStringByObject(col,JsoupUtil.LABEL));
                //宽度
                head1.add(Utils.getStringByObject(col,JsoupUtil.WIDTH));
                //字段类型
                head1.add(Utils.getStringByObject(col,JsoupUtil.JDBCTYPE));
                head.add(head1);
            }
            log.info("ezadmin start export {} {} {} {}",sessionUserId,ip, Utils.getStringByObject(coreMap,"listname"));
            String fileName=request.getParameter("EXPORT_FILE_NAME");
            if(StringUtils.isBlank(fileName)){
                fileName=Utils.getStringByObject(coreMap,"listname");
            }
            EzClientBootstrap.instance().getExport().export(fileName,head,data,response);
            //记录日志
            log.info("ezadmin end export {} {} {} {}",sessionUserId,ip, Utils.getStringByObject(coreMap,"listname"));

        }catch (Exception e){
            log.error("",e);
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            Map<String, String> map = new HashMap<String, String>();
            map.put("status", "failure");
            map.put("message", "导出文件失败：" + e.getMessage());
            response.getWriter().println(JSONUtils.toJSONString(map));
        }finally {
            existList.remove(key);
        }
    }

    private Map<String, Object> exportList(String ENCRYPT_LIST_ID,String orderedColumn[]) throws Exception {
        Map<String, Object> list = JSONUtils.parseObjectMap(listService.selectPublishListById(ENCRYPT_LIST_ID)) ;
        if(!Utils.isNotEmpty(list)){
            throw new NotExistException();
        }
        Map<String,Object> coreMap=(Map<String,Object>)list.get("core");
        List<Map<String,Object>> colList=(List<Map<String,Object>>)list.get("col");
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
        }else{
            list.put("col",colList);
        }
        Iterator<Map<String,Object>> it= colList.iterator();
        while(it.hasNext()){
            Map<String,Object> col= it.next();
            if( StringUtils.equalsIgnoreCase(col.get(JsoupUtil.SPECIALCOL)+"","1")
//                    ||StringUtils.equalsIgnoreCase(col.get(JsoupUtil.BODY_PLUGIN_CODE)+"","td-pic")
//                    ||StringUtils.equalsIgnoreCase(col.get(JsoupUtil.HEAD_PLUGIN_CODE)+"","th-numbers")
//                    ||StringUtils.equalsIgnoreCase(col.get(JsoupUtil.BODY_PLUGIN_CODE)+"","td-radio")
            ){
                it.remove();
            }
        }
        return list;
    }




     private void fillExcelData(List<List<Object>> data, List<Map<String, Object>> dataList,List<Map<String,Object>> colList) {
        if(Utils.isEmpty(dataList)){
            return;
        }
        for (int i = 0; i < dataList.size(); i++) {
             List<Object> row=new ArrayList<>();
             row.add(data.size()+1);
             row.addAll((List<String>)dataList.get(i).get("tds"));
             data.add(row);
         }
     }
 }
