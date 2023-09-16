//package com.ezadmin.biz.list.emmber.list;
//
//import com.ezadmin.biz.emmber.list.ColumnVO;
//import com.ezadmin.biz.emmber.list.EzListDTO;
//import com.ezadmin.biz.emmber.list.SearchVO;
//import com.ezadmin.biz.list.service.ListService;
//import com.ezadmin.biz.model.EzSearchModel;
//import com.ezadmin.common.enums.ParamNameEnum;
//import com.ezadmin.common.utils.*;
//import com.ezadmin.EzBootstrap;
//import com.ezadmin.biz.base.service.CoreService;
//import org.apache.commons.lang.exception.ExceptionUtils;
//
//import javax.sql.DataSource;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Map;
//
///**
// *不加载数据，只加载配置
// **/
//public class EmptyEzList extends DefaultEzList {
//
//     private String encodeListId;
//
//     private ListService listService = EzProxy.singleInstance(ListService.class);
//
//
//
//    public EmptyEzList(DataSource dataSource, Map<String, Object>  requestParamMap, Map<String, String>  sessionParamMap) {
//        super(dataSource,requestParamMap,sessionParamMap);
//    }
//    public EmptyEzList(  String encodeListId, DataSource dataSource, Map<String, Object>  requestParamMap, Map<String, String>  sessionParamMap) {
//        super(dataSource,requestParamMap,sessionParamMap);
//         this.encodeListId=encodeListId;
//        loading();
//    }
//    public EzListDTO loading(){
//        ezListDTO=loadingBase();
//       // Utils.addLog("基础数据加载成功");
//      //  String base= JSONUtils.toJSONString(ezListDTO);
////        listMap=JSONUtils.parseObject(base,Map.class);
////        try {
////            listMap.put("ENCODE_LIST_ID", DESUtils.encryptDES(ezListDTO.getListId() + ""));
////        }catch (Exception e){}
//        ezListDTO.setColumnItemList(loadingColumn());
//        //Utils.addLog("表头配置加载成功");
//
//        ezListDTO.setSearchItemList(loadingSearch());
//       // Utils.addLog("搜索配置加载成功");
//        ezListDTO.setPage(loadingPage());
//        //Utils.addLog("分页设置加载成功");
//
//            ezListDTO.setDataList(loadingData());
//
//
//        return ezListDTO;
//    }
//
//    public boolean isEditing(){
//       return StringUtils.equals(getRequestParamMap().get("IS_DEBUG")+"","1") ;
//    }
//
//    @Override
//    public EzListDTO loadingBase() {
//        EzListDTO list= null;// new EzListDTO();
//        try {
//            Map<String, String> listDb = listService.selectListById("",encodeListId);
//
//            list=EzListDTO.mapTo(listDb);
//            list.setOriginMap(listDb);
//
//            //自动替换数据库
//            dataSource= EzBootstrap.instance().getDataSourceByKey(listDb.get("DATASOURCE"));
//            return list;
//        } catch (Exception e) {
//            if(Utils.getLog()!=null) {
//                Utils.addLog("",ExceptionUtils.getFullStackTrace(e));
//            }
//        }
//        return new EzListDTO();
//    }
//
//
//
//    @Override
//    public List<EzSearchModel> loadingSearch() {
//        try {
//            List<EzSearchModel> modelList=new ArrayList<>();
//            List<Map<String, String>>  listDb = listService.selectSearchByListId( encodeListId);
//            for (int i = 0; i < listDb.size(); i++) {
//                EzSearchModel m=new EzSearchModel();
//                //配置复制
//                m.config().putAll(listDb.get(i));
//                //request引用
//                m.setpParam(requestParamMap);
//                m.setSession(sessionParamMap);
//                modelList.add(m);
//            }
//            return modelList;
//        } catch (Exception e) {
//            LOG.error("EZADMIN LIST={}  ",encodeListId,e);
//        }
//        return Collections.emptyList();
//    }
//
//
//    @Override
//    public List<Map<String, String>>  loadingColumn() {
//        try {
//            List<Map<String, String>>  listDb = listService.selectColumnByListId(""  ,encodeListId);
//            for (int i = 0; i < listDb.size(); i++) {
//                Map<String, String> item=listDb.get(i);
//
//                item.put(ParamNameEnum.itemParamValue.getName(),Utils.trimNull(requestParamMap.get(item.get(JsoupUtil.ITEM_NAME))));
//                item.put(ParamNameEnum.itemParamValueStart.getName(),Utils.trimNull(requestParamMap.get(item.get(JsoupUtil.ITEM_NAME+ "_START"))));
//                item.put(ParamNameEnum.itemParamValueEnd.getName(),Utils.trimNull(requestParamMap.get(item.get(JsoupUtil.ITEM_NAME+ "_END"))));
//                item.put(ParamNameEnum.itemParamOrderValue.getName(),Utils.trimNull(requestParamMap.get(item.get(JsoupUtil.ITEM_NAME+ "_ORDER"))));
//            } ;
//            return listDb;
//        } catch (Exception e) {
//            LOG.error("EZADMIN LIST={}  ",encodeListId,e);
//        }
//        return Collections.emptyList();
//    }
//
//
//    @Override
//    public List<Map<String, String>> loadingData()   {
//        List<Map<String, String>> dataList = new ArrayList<>();
//        return dataList;
//    }
//
//    @Override
//    public void renderHtml() {
//
//    }
//}
