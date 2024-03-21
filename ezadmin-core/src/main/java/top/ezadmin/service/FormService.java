package top.ezadmin.service;

import java.util.Map;

public interface FormService {

    public  Map<String, Object> selectAllFormMapById(String encodeId) throws Exception;
    void fillFormById( Map<String, Object> form,Map<String,Object> request,Map<String,String> session
    ) throws Exception;

    Map<String, Object> selectAllFormByHtml(String ezConfig,String adminStyle);
     public Map<String,Object>  selectConfigEditForm(String code ) throws Exception;

    String selectPublishFormById(String encryptListId) throws Exception;
}
