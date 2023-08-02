package com.ezadmin.biz.list.service;

import com.ezadmin.biz.emmber.list.EzListDTO;
import com.ezadmin.biz.model.ItemInitData;
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
    Map<String,String> selectListById(String pageId,String encodeListId)  ;
      Map<String, String> selectTemplateById(String tempId) throws Exception;


    List<Map<String, String>> getDataListByListId(DataSource dataSource, EzListDTO list, Map<String, Object> request , Map<String, String> session, Page page) throws Exception;

    long getDataCountByListId(DataSource dataSource, EzListDTO list, Map<String, Object> request , Map<String, String> session) throws Exception;



    public List<Map<String, String>> selectSearchByListId( String encodeListId) throws Exception ;

 
    public List<Map<String, String>> selectNavByListId( String encodeListId) throws Exception ;

    
    
    public List<Map<String, String>> selectRowButtonByListId(String listId,String encodeListId) throws Exception ;

    
    
    public List<Map<String, String>> selectTableButtonByListId(String listId,String encodeListId) throws Exception ;

    public List<Map<String, String>> selectColumnByListId(String listId,String encodeListId) throws Exception;


    /**
     *
     * @param
     * @return md5 ,FILE_ID
     * @throws Exception
     */
//    Map<String, Object> addFile(File file) throws  Exception;
//    void logExport(String type,String userId,String ip,String url,String orgFileName,String bizId,String bizName) throws  Exception;

    Map<String, Object> selectLogById(String logId, String userID) throws Exception;

    public ItemInitData getSelectItems(DataSource datasource, String initData, String initType, Map<String, Object> params
    ) throws Exception ;

   // List<Map<String, String>> selectSearchFieldByUserId(EzSqlogDataSource datasource, Integer toLong, String encodeListId, String userId) throws Exception;

  //  List<Map<String, String>> selectColsFieldByUserId(EzSqlogDataSource datasource,Integer toLong,  String encodeListId,String userId) throws Exception;

   // void mergeColsByUser(String listId,String name,String ip, String sessionUserId, List<Long> searchIds, List<Long> fieldIds) throws Exception;
//    Map<String, Set<String>> filterMyUserColumn(Integer listId, String sessionUserId,String encodeId);
//
//    boolean removeNotSelectColumnData(EzList list, String sessionUserId,String encodeid);
   public Map<String, String> getDbTemplateByCode(String code, int templateId,String fold) throws Exception  ;

}
