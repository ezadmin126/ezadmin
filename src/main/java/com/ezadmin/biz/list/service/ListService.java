package com.ezadmin.biz.list.service;

import com.ezadmin.biz.dao.FormDao;
import com.ezadmin.biz.emmber.list.EzListDTO;
import com.ezadmin.biz.model.ItemInitData;
import com.ezadmin.common.annotation.EzCacheAnnotation;
import com.ezadmin.common.utils.JSONUtils;
import com.ezadmin.common.utils.Page;
import com.ezadmin.plugins.sqlog.EzSqlogDataSource;
import com.ezadmin.biz.list.emmber.list.EzList;

import javax.sql.DataSource;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ListService {
    Map<String, String> selectListById(String pageId, String encodeListId);

    Map<String, String> selectTemplateById(String tempId) throws Exception;


    List<Map<String, Object>> getDataListByListId(DataSource dataSource,Map<String, Object> list, Map<String, Object> request, Map<String, String> session, Page page) throws Exception;

    long getDataCountByListId(DataSource dataSource,Map<String, Object> list, Map<String, Object> request, Map<String, String> session) throws Exception;


    public List<Map<String, String>> selectSearchByListId(String encodeListId) throws Exception;


    public List<Map<String, String>> selectNavByListId(String encodeListId) throws Exception;


    public List<Map<String, String>> selectRowButtonByListId(String listId, String encodeListId) throws Exception;


    public List<Map<String, String>> selectTableButtonByListId(String listId, String encodeListId) throws Exception;

    public List<Map<String, String>> selectColumnByListId(String listId, String encodeListId) throws Exception;


    Map<String, Object> selectLogById(String logId, String userID) throws Exception;

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
