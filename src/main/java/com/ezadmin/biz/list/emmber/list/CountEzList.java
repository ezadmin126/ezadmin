package com.ezadmin.biz.list.emmber.list;

import com.ezadmin.biz.emmber.list.ColumnVO;
import com.ezadmin.biz.emmber.list.EzListDTO;
import com.ezadmin.biz.emmber.list.SearchVO;
import com.ezadmin.biz.list.service.ListService;
import com.ezadmin.biz.model.EzSearchModel;
import com.ezadmin.common.EzAdminRuntimeException;
import com.ezadmin.common.enums.OrderEnum;
import com.ezadmin.common.utils.*;
import com.ezadmin.EzBootstrap;
import com.ezadmin.biz.base.service.CoreService;
 import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.*;

/**
 *
 **/
public class CountEzList  {
   private static Logger LOG= LoggerFactory.getLogger(CountEzList.class);


    private String encodeListId;

     private static  ListService listService = EzProxy.singleInstance(ListService.class);

    protected Map<String, Object> requestParamMap ;
    protected Map<String, String> sessionParamMap ;

    // Map<String,Object> listMap;
    protected Page pagination;

   private EzListDTO ezListDTO;
    public EzListDTO getEzListDto() {
        return ezListDTO;
    }


    public CountEzList( String encodeListId, Map<String, Object>  requestParamMap, Map<String, String>  sessionParamMap) {
       //  super(dataSource,requestParamMap,sessionParamMap);
        this.requestParamMap=requestParamMap;
        this.sessionParamMap=sessionParamMap;

        this.encodeListId=encodeListId;
        loading();
    }
    public EzListDTO loading(){
        ezListDTO=loadingBase();
//
         ezListDTO.setSearchItemList(loadingSearch());
         ezListDTO.setPage(loadingPage());
       ezListDTO.setDataList(loadingData());

        return ezListDTO;
    }

    public boolean isEditing(){
       return StringUtils.equals(requestParamMap.get("IS_DEBUG")+"","1") ;
    }

   
    public EzListDTO loadingBase() {
        EzListDTO list= null;// new EzListDTO();

        Map<String, String> listDb = listService.selectListById(""  ,encodeListId);

        list=EzListDTO.mapTo(listDb);
        list.setOriginMap(listDb);


        //自动替换数据库
       // dataSource=;
        return list;


    }



   
    public List<EzSearchModel> loadingSearch() {
        try {
            List<EzSearchModel> modelList=new ArrayList<>();
            List<Map<String, String>>  listDb = listService.selectSearchByListId( encodeListId);
            for (int i = 0; i < listDb.size(); i++) {
                EzSearchModel m=new EzSearchModel();
                //配置复制
                m.config().putAll(listDb.get(i));
                //request引用
                m.setpParam(requestParamMap);
                m.setSession(sessionParamMap);
                modelList.add(m);
            }
            return modelList;
        } catch (Exception e) {
            LOG.error("EZADMIN LIST={}  ",encodeListId,e);
        }
        return Collections.emptyList();
    }


    public Page loadingPage() {
        pagination=new Page(requestParamMap);
        for (int i = 0; i < ezListDTO.getColumnItemList().size(); i++) {
            Map<String,String> item = ezListDTO.getColumnItemList().get(i);
            if (StringUtils.equals(item.get(JsoupUtil.ORDER), "1")) {
                String orderValue=Utils.trimNull( Utils.getStringByObject(requestParamMap,item.get(JsoupUtil.ITEM_NAME)+"_ORDER"));
                if (StringUtils.isNotBlank(orderValue)) {
                    ezListDTO.setEzOrderName(item.get(JsoupUtil.ORDER_TYPE));
                    ezListDTO.setEzOrderValue(orderValue);
                    pagination.setOrderByClause(item.get(JsoupUtil.ITEM_NAME) + " " + OrderEnum.getSort(orderValue));
                    break;
                }
            }
        }
        if(StringUtils.isBlank(pagination.getOrderByClause())){
            pagination.setOrderByClause(ezListDTO.getDefaultOrder());
        }else{
            pagination.setOrderByClause(" order by "+pagination.getOrderByClause());
        }
        return pagination;
    }


   
    public List<Map<String, String>> loadingData()  {
        List<Map<String, String>> dataList = new ArrayList<>();
        long totalCount = 0;
        if (StringUtils.equals(ezListDTO.getDefaultEmpty(), "1")&&StringUtils.equalsIgnoreCase(requestParamMap.get("default_empty")+"","1")) {
            //TODO 默认第一次不展示数据
        }else{
            try {
                DataSource dataSource= EzBootstrap.instance().getDataSourceByKey(ezListDTO.getDataSource());

                    totalCount =  listService.getDataCountByListId(dataSource, ezListDTO, requestParamMap,sessionParamMap);
                    if(LOG.isDebugEnabled()){
                        LOG.debug("EZADMIN LIST={}  count执行完毕，总数：{} ",encodeListId ,totalCount);
                    }

            }
            catch (EzAdminRuntimeException ex){
                LOG.error("EZADMIN LIST={}  表达式错误 code={} message={} ",encodeListId,ex.code(),ex.getMessage(),ex);
            }
            catch(Exception e){
                LOG.error("EZADMIN LIST={}  渲染COUNT错误",encodeListId,e);
            }
        }
        pagination.setTotalRecord(totalCount);
        return dataList;
    }

   
    public void renderHtml() {

    }
}
