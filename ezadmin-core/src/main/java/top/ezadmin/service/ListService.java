package top.ezadmin.service;

 import top.ezadmin.dao.model.ItemInitData;
 import top.ezadmin.common.utils.Page;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

public interface ListService {

    List<Map<String, Object>> getDataListByListId(DataSource dataSource,Map<String, Object> list, Map<String, Object> request, Map<String, String> session, Page page) throws Exception;

    long getDataCountByListId(DataSource dataSource,Map<String, Object> list, Map<String, Object> request, Map<String, String> session) throws Exception;

    /**
     * 获取编辑状态下的配置
     * @param code
     * @throws Exception
     */
    public Map<String,Object>  selectConfigEditList(String code ) throws Exception;
    public Map<String,Object>  selectConfigHistoryList(String code,String version ) throws Exception;

      ItemInitData getSelectItems(DataSource datasource, String initData, String initType, Map<String, Object> params
    ) throws Exception;

      String selectPublishListById(String encodeId) throws Exception;

    Map<String, Object> selectAllListByHtml(String html) throws Exception;

    void fillListById(Map<String, Object> list, Map<String, Object> requestParamMap, Map<String, String> sessionParamMap) throws Exception;
    void exportListById(Map<String, Object> list, Map<String, Object> requestParamMap, Map<String, String> sessionParamMap) throws Exception;
    void fillCountById(Map<String, Object> list, Map<String, Object> requestParamMap, Map<String, String> sessionParamMap) throws Exception;

    void fillTreeById(Map<String, Object> list, Map<String, Object> requestParamMap, Map<String, String> sessionParamMap)throws Exception;


    String selectDbPublishListById(String encryptListId) throws Exception ;
}
