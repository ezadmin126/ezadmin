package com.ezadmin.service;

 import com.ezadmin.dao.model.ItemInitData;
import com.ezadmin.common.annotation.EzCacheAnnotation;
import com.ezadmin.common.utils.Page;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

public interface ListService {



    List<Map<String, Object>> getDataListByListId(DataSource dataSource,Map<String, Object> list, Map<String, Object> request, Map<String, String> session, Page page) throws Exception;

    long getDataCountByListId(DataSource dataSource,Map<String, Object> list, Map<String, Object> request, Map<String, String> session) throws Exception;


    public ItemInitData getSelectItems(DataSource datasource, String initData, String initType, Map<String, Object> params
    ) throws Exception;

    public Map<String, String> getDbTemplateByCode(String code, int templateId, String fold) throws Exception;

    public String selectAllListById(String encodeId) throws Exception;

    @EzCacheAnnotation
    public Map<String, Object> selectAllListMapById(String encodeId) throws Exception;

    void fillListById(Map<String, Object> list, Map<String, Object> requestParamMap, Map<String, String> sessionParamMap) throws Exception;
    void fillCountById(Map<String, Object> list, Map<String, Object> requestParamMap, Map<String, String> sessionParamMap) throws Exception;

    void fillTreeById(Map<String, Object> list, Map<String, Object> requestParamMap, Map<String, String> sessionParamMap)throws Exception;


}
