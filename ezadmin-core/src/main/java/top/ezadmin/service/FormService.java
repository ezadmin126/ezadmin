package top.ezadmin.service;

import java.util.Map;

public interface FormService {

    String selectAllFormById( String encodeId) throws Exception;
    public  Map<String, Object> selectAllFormMapById(String encodeId) throws Exception;
    void fillFormById( Map<String, Object> form,Map<String,Object> request,Map<String,String> session
    ) throws Exception;

    Map<String, Object> selectAllFormByHtml(String ezConfig,String adminStyle);
    public Map<String,Object>  selectConfigPublishForm(String code) throws Exception;
    public Map<String,Object>  selectConfigEditForm(String code ) throws Exception;
}
