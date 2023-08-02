package com.ezadmin.biz.list.emmber.list;

import com.ezadmin.biz.emmber.ListRow;
import com.ezadmin.biz.emmber.list.*;
import com.ezadmin.biz.list.service.ListService;
import com.ezadmin.biz.model.EzSearchModel;
import com.ezadmin.common.enums.OrderEnum;
import com.ezadmin.common.enums.template.TemplateEnum;
import com.ezadmin.common.utils.*;
import com.ezadmin.plugins.parser.MapParser;
import com.ezadmin.EzBootstrap;
import com.ezadmin.biz.base.service.CoreService;

import com.ezadmin.plugins.express.executor.DefaultExpressExecutor;

import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.Context;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 **/
public abstract  class AbstractEzList implements EzList {
    protected static Logger LOG= LoggerFactory.getLogger(AbstractEzList.class);
    private CoreService coreService = EzProxy.singleInstance(CoreService.class);
    private ListService listService = EzProxy.singleInstance(ListService.class);

    protected Map<String, Object> requestParamMap=new HashMap<>();
    protected Map<String, String> sessionParamMap=new HashMap<>();

   // Map<String,Object> listMap;
    protected Page pagination;
    protected DataSource dataSource;
    EzListDTO ezListDTO;
    @Override
    public EzListDTO getEzListDto() {
        return ezListDTO;
    }


    public boolean isEditing(){
        return StringUtils.equals(Utils.trimNull(getRequestParamMap().get("IS_DEBUG")),"1") ;
    }

    public AbstractEzList(DataSource dataSource,Map<String, Object> requestParamMap,Map<String, String> sessionParamMap){
        pagination= new Page(requestParamMap);
        this.requestParamMap=requestParamMap;
        this.sessionParamMap=sessionParamMap;
        this.dataSource=dataSource;
    }
    public Map<String, Object> getRequestParamMap(){
        return requestParamMap;
    }

    public EzListDTO loading(){
        ezListDTO=loadingBase();
       // String base= JSONUtils.toJSONString(ezListDTO);
       // listMap=ezListDTO.toBeanMap();//JSONUtils.parseObject(base,Map.class);
//        try {
//            listMap.put("ENCODE_LIST_ID", DESUtils.encryptDES(ezListDTO.getListId() + ""));
//        }catch (Exception e){}
        ezListDTO.setColumnItemList(loadingColumn());
        ezListDTO.setNavBarsList(loadingNavBar());
        ezListDTO.setRowButtonItemList(loadingRowButton());
        ezListDTO.setTableButtonItemList(loadingTableButton());
        ezListDTO.setSearchItemList(loadingSearch());
        ezListDTO.setPage(loadingPage());
        ezListDTO.setDataList(loadingData());
        return ezListDTO;
    }
    @Override
    public EzListDTO loadingBase() {
        return new EzListDTO();
    }

    @Override
    public List<Map<String,String>> loadingNavBar() {
        return new ArrayList<>();
    }

    @Override
    public List<EzSearchModel> loadingSearch() {
        return new ArrayList<>();
    }

    @Override
    public List<Map<String, String>> loadingTableButton() {
        return new ArrayList<>();
    }

    @Override
    public List<Map<String,String>> loadingColumn() {
        return new ArrayList<>();
    }

    @Override
    public List<Map<String,String>> loadingRowButton() {
        return new ArrayList<>();
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

    @Override
    public List<Map<String, String>> loadingData()   {
        return new ArrayList<>();
    }

    @Override
    public void renderHtml() {

    }
    @Override
    public List<List<Object>> renderExcel() {
        return null;
    }
    public Page getPagination() {
        return pagination;
    }

    public void setPagination(Page pagination) {
        this.pagination = pagination;
    }
}
