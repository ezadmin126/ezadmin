package com.ezadmin.biz.list.emmber.list;

import com.ezadmin.biz.emmber.list.ColumnVO;
import com.ezadmin.biz.emmber.list.EzListDTO;
import com.ezadmin.biz.emmber.list.SearchVO;
import com.ezadmin.biz.list.service.ListService;
import com.ezadmin.biz.model.EzSearchModel;
import com.ezadmin.common.utils.*;
import com.ezadmin.EzBootstrap;
import com.ezadmin.biz.base.service.CoreService;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 **/
public class SumEzList extends DefaultEzList {

    private Integer listId;    private String encodeListId;


    private CoreService coreService = EzProxy.singleInstance(CoreService.class);
    private ListService listService = EzProxy.singleInstance(ListService.class);



    public SumEzList(DataSource dataSource, Map<String, Object>  requestParamMap, Map<String, String>  sessionParamMap) {
        super(dataSource,requestParamMap,sessionParamMap);
    }
    public SumEzList(Integer listId,String encodeListId,DataSource dataSource, Map<String, Object>  requestParamMap, Map<String, String>  sessionParamMap) {
        super(dataSource,requestParamMap,sessionParamMap);
        this.listId=listId;
        this.encodeListId=encodeListId;

        loading();
    }

    public boolean isEditing(){
       return StringUtils.equals(Utils.getStringByObject(getRequestParamMap(),"IS_DEBUG"),"1") ;
    }

    @Override
    public EzListDTO loadingBase() {
        EzListDTO list= null;// new EzListDTO();
        try {
            Map<String, String> listDb = listService.selectListById(""+listId,encodeListId);

            list=EzListDTO.mapTo(listDb);
            try {
                if(StringUtils.isBlank(encodeListId)){
                    list.setEncodeListId(DESUtils.encryptDES("" + listDb.get("LIST_ID")));
                }else{
                    list.setEncodeListId(encodeListId);
                }
                listId= NumberUtils.toInt(listDb.get("LIST_ID"));
            }catch (Exception e){
                //ignor
            }
//            if(isEditing()){
//                Map<String, String> template=listService.selectTemplateById("3");
//                list.setTemplateId(3);
//                list.setTemplateBodyList(template.get("TEMPLATE_BODY_LIST"));
//            }else{
//                list.setTemplateBodyList(listDb.get("TEMPLATE_BODY_LIST"));
//            }
            //自动替换数据库
            dataSource= EzBootstrap.instance().getDataSourceByKey(listDb.get("DATASOURCE"));
            return list;
        } catch (Exception e) {
            if(Utils.getLog()!=null) {
                Utils.addLog("",ExceptionUtils.getFullStackTrace(e));
            }
        }
        return new EzListDTO();
    }



    @Override
    public List<EzSearchModel> loadingSearch() {
           return super.loadingSearch();
    }


    @Override
    public List<Map<String, String>> loadingColumn() {

        return super.loadingColumn();
    }


    @Override
    public List<Map<String, String>> loadingData()   {
        List<Map<String, String>> dataList = new ArrayList<>();
        long totalCount = 0;
        if (StringUtils.equals(ezListDTO.getDefaultEmpty(), "1")&&StringUtils.equalsIgnoreCase(requestParamMap.get("default_empty")+"","1")) {
            //TODO 默认第一次不展示数据
        }else{
            try {
                totalCount =1;// listService.getDataCountByListId(dataSource, ezListDTO, requestParamMap,sessionParamMap);
                Utils.addLog("count执行完毕，总数：" + totalCount);
                if (totalCount > 0) {
                    dataList = listService.getDataListByListId(dataSource, ezListDTO, requestParamMap,sessionParamMap, null);
                    Utils.addLog("select执行完毕，总数：" + (dataList == null ? 0 : dataList.size()));
                }
            }catch(Exception e){
                LOG.error("EZADMIN LIST={}   ",encodeListId,e);
            }
        }
        pagination.setTotalRecord(totalCount);
        return dataList;
    }

    @Override
    public void renderHtml() {

    }
}
