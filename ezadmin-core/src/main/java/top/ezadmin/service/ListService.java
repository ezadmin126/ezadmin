package top.ezadmin.service;

 import top.ezadmin.dao.model.ItemInitData;
 import top.ezadmin.common.utils.Page;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

public interface ListService {

    List<Map<String, Object>> getDataListByListId(DataSource dataSource,Map<String, Object> list, Map<String, Object> request, Map<String, String> session, Page page) throws Exception;

    long getDataCountByListId(DataSource dataSource,Map<String, Object> list, Map<String, Object> request, Map<String, String> session) throws Exception;


      ItemInitData getSelectItems(DataSource datasource, String initData, String initType, Map<String, Object> params
    ) throws Exception;

      String selectAllListById(String encodeId) throws Exception;

      Map<String, Object> selectAllListMapById(String encodeId) throws Exception;
    Map<String, Object> selectAllListByHtml(String html) throws Exception;

    void fillListById(Map<String, Object> list, Map<String, Object> requestParamMap, Map<String, String> sessionParamMap) throws Exception;
    void exportListById(Map<String, Object> list, Map<String, Object> requestParamMap, Map<String, String> sessionParamMap) throws Exception;
    void fillCountById(Map<String, Object> list, Map<String, Object> requestParamMap, Map<String, String> sessionParamMap) throws Exception;

    void fillTreeById(Map<String, Object> list, Map<String, Object> requestParamMap, Map<String, String> sessionParamMap)throws Exception;


}
