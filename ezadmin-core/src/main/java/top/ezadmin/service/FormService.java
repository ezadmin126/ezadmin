package top.ezadmin.service;

import java.util.Map;

public interface FormService {
    void fillFormById( Map<String, Object> form,Map<String,Object> request,Map<String,String> session) throws Exception;
    Map<String,Object>  selectConfigEditForm(String code ) throws Exception;
    String selectPublishFormById(String encryptListId) throws Exception;
}
