//package com.ezadmin.biz.list.emmber.list;
//
//import com.ezadmin.biz.emmber.list.ColumnVO;
//import com.ezadmin.biz.emmber.list.EzListDTO;
//import com.ezadmin.biz.emmber.list.SearchVO;
//import com.ezadmin.biz.list.service.ListService;
//import com.ezadmin.biz.model.EzSearchModel;
//import com.ezadmin.biz.model.ItemInitData;
//import com.ezadmin.common.constants.SelectKVContants;
//import com.ezadmin.common.constants.SessionConstants;
//import com.ezadmin.common.enums.JdbcTypeEnum;
//import com.ezadmin.common.enums.ParamNameEnum;
//import com.ezadmin.common.utils.*;
//import com.ezadmin.EzBootstrap;
//import com.ezadmin.biz.base.service.CoreService;
//import org.apache.commons.lang.ObjectUtils;
//import org.apache.commons.lang.exception.ExceptionUtils;
//
//import javax.sql.DataSource;
//import java.math.BigDecimal;
//import java.util.*;
//
///**
// *
// **/
//public class ExportEzList extends AbstractEzList {
//
//    private Integer listId;
//    private String encodeListId;
//
//    private CoreService coreService = EzProxy.singleInstance(CoreService.class);
//    private ListService listService = EzProxy.singleInstance(ListService.class);
//
//
//
//    public ExportEzList(DataSource dataSource, Map<String, Object>  requestParamMap) {
//        super(dataSource,requestParamMap,new HashMap<String, String>());
//    }
//    public ExportEzList(Integer listId,String   encodeListId, DataSource dataSource, Map<String, Object>  requestParamMap) {
//        super(dataSource,requestParamMap,new HashMap<String, String>());
//        this.listId=listId;
//        this.encodeListId=encodeListId;
//        loading();
//    }
//    public EzListDTO loading(){
//        ezListDTO=loadingBase();
//      //  listMap= JSONUtils.parseObject(JSONUtils.toJSONString(ezListDTO),Map.class);
//        ezListDTO.setColumnItemList(loadingColumn());
//        ezListDTO.setSearchItemList(loadingSearch());
//
//        ezListDTO.setPage(loadingPage());
//        try {
//            ezListDTO.setDataList(loadingData());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return ezListDTO;
//    }
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
//    public EzListDTO loadingBase() {
//        EzListDTO list=  new EzListDTO();
//        try {
//            Map<String, String> listDb = listService.selectListById(""+listId,encodeListId);
//            list=EzListDTO.mapTo(listDb);
//            try {
//                list.setEncodeListId(DESUtils.encryptDES("" + listId));
//            }catch (Exception e){
//                //ignor
//            }
//            //自动替换数据库
//            dataSource= EzBootstrap.instance().getDataSourceByKey(listDb.get("DATASOURCE"));
//
//            return list;
//        } catch (Exception throwables) {
//            throwables.printStackTrace();
//        }
//        return new EzListDTO();
//    }
//
//
//
//
//
//    @Override
//    public List<Map<String, String>>  loadingColumn() {
//        try {
//            List<Map<String, String>>  listDb = listService.selectColumnByListId(""  ,encodeListId);
//            for (int i = 0; i < listDb.size(); i++) {
//                Map<String, String> item=listDb.get(i);
//                item.put(ParamNameEnum.itemParamValue.getName(),Utils.trimNull(requestParamMap.get(item.get(JsoupUtil.ITEM_NAME))));
//                item.put(ParamNameEnum.itemParamValueStart.getName(),Utils.trimNull(requestParamMap.get(item.get(JsoupUtil.ITEM_NAME+ "_START"))));
//                item.put(ParamNameEnum.itemParamValueEnd.getName(),Utils.trimNull(requestParamMap.get(item.get(JsoupUtil.ITEM_NAME+ "_END"))));
//                item.put(ParamNameEnum.itemParamOrderValue.getName(),Utils.trimNull(requestParamMap.get(item.get(JsoupUtil.ITEM_NAME+ "_ORDER"))));
//            };
//            return listDb;
//        } catch (Exception e) {
//            LOG.error("EZADMIN LIST={}  ",encodeListId,e);
//        }
//        return Collections.emptyList();
//    }
//    @Override
//    public List<Map<String, String>> loadingData()  {
//        List<Map<String, String>> dataList = new ArrayList<>();
//        long totalCount = 0;
//        if (StringUtils.equals(ezListDTO.getDefaultEmpty(), "1")&&StringUtils.equalsIgnoreCase(requestParamMap.get("default_empty")+"","1")) {
//            //TODO 默认第一次不展示数据
//        }else{
//            try {
//              //  dataList = listService.getDataListByListId(dataSource,ezListDTO,requestParamMap,sessionParamMap,   pagination);
//            } catch (Exception e) {
//                LOG.error("",e);
//            }
//            if(dataList.size()<1000){
//                totalCount=dataList.size();
//            }else{
//            totalCount =600000;
//            }// listService.getDataCountByListId(dataSource,ezListDTO,requestParamMap );
//        }
//        pagination.setTotalRecord(totalCount);
//        return dataList;
//    }
//
//    @Override
//    public List<List<Object>> renderExcel() {
////        Map<String, Set<String>> customColsMap=listService.filterMyUserColumn(  listId, sessionParamMap==null?"":  sessionParamMap.get(SessionConstants.EZ_SESSION_USER_ID_KEY),encodeListId);
//        String globalEmptyShow= ezListDTO.getEmptyShow();
//        List<List<Object>> result=new ArrayList<>();
//        if (Utils.isNotEmpty(ezListDTO.getDataList())){
//            for (int i = 0; i < ezListDTO.getDataList().size(); i++) {
//                Map<String,String> dataRow=ezListDTO.getDataList().get(i);
//                List<Object> row=new ArrayList<>();
//                row.add(""+(pagination.getStartRecord()+i+1));
//                //补充数据列
//                for (int i1 = 0; i1 < ezListDTO.getColumnItemList().size(); i1++) {
//                    Map<String,String> column=ezListDTO.getColumnItemList().get(i1);
//
//                    String itemName=column.get(JsoupUtil.ITEM_NAME);
//
//                    String dataInDb = ObjectUtils.toString(dataRow.get(column.get(JsoupUtil.ITEM_NAME)));
//                    String columnEmptyShow= column.get(JsoupUtil.EMPTY_SHOW);
//                    dataInDb=calulateData(dataInDb,globalEmptyShow,columnEmptyShow,column.get(JsoupUtil.JDBCTYPE));
//                    if (StringUtils.isNotBlank(column.get(JsoupUtil.DATA))) {
//                        try {
//                            DataSource temp=dataSource;
//                            if(StringUtils.isNotBlank(column.get(JsoupUtil.DATASOURCE))&&EzBootstrap.instance().getDataSourceByKey(column.get(JsoupUtil.DATASOURCE))!=null){
//                                temp=EzBootstrap.instance().getDataSourceByKey(column.get(JsoupUtil.DATASOURCE));
//                            }
//                            ItemInitData selectItems=listService.getSelectItems(temp, column.get(JsoupUtil.DATA), column.get(JsoupUtil.DATATYPE),
//                                    requestParamMap);
//                            boolean hasValue=false;
//                            for (int j = 0; j < selectItems.getItems().size(); j++) {
//                                if(StringUtils.equalsIgnoreCase(selectItems.getItems().get(j).get(SelectKVContants.KEY)+"",dataInDb)){
//                                    row.add(selectItems.getItems().get(j).get(SelectKVContants.VALUE));
//                                    hasValue=true;
//                                    break;
//                                }
//                            }
//                            if(!hasValue){
//                                row.add("");
//                            }
//
//                        } catch (Exception e) {
//                            LOG.error("", e);
//                        }
//                    }else{
//                            try {
//                               if(JdbcTypeEnum.isNumber(column.get(JsoupUtil.JDBCTYPE))){
//                                   row.add(new BigDecimal(dataInDb));
//                               }
//                               else{
//                                   row.add(dataInDb) ;
//                               }
//                            }catch (Exception e){
//                                row.add(dataInDb) ;
//                            }
//                    }
//                };
//
//                result.add(row);
//            }
//
//        }
//        return result;
//    }
//
//    private String calulateData(String dataInDb, String globalEmptyShow, String columnEmptyShow,String jdbcType) {
//        if(StringUtils.isBlank(dataInDb)){
//            return Utils.trimNullDefault(dataInDb,columnEmptyShow,globalEmptyShow);
//        }
//        switch (JdbcTypeEnum.get(jdbcType)){
//            case NUMBER:
//            case NUMBER2:
//                BigDecimal result=   new BigDecimal(dataInDb).setScale(2,BigDecimal.ROUND_HALF_UP);
//                return result.toString();
//            case NUMBER1:
//                return  new BigDecimal(dataInDb).setScale(1,BigDecimal.ROUND_HALF_UP).toString();
//            case NUMBER3:
//                return  new BigDecimal(dataInDb).setScale(3,BigDecimal.ROUND_HALF_UP).toString();
//            case NUMBER4:
//                return  new BigDecimal(dataInDb).setScale(4,BigDecimal.ROUND_HALF_UP).toString();
//            case DATE:
//                return  EzDateUtils.toDateFormat(dataInDb);
//            case DATETIME:
//                return  EzDateUtils.toDateTimeFormat(dataInDb);
//            default:
//                return dataInDb;
//        }
//    }
//
//}
