package com.ezadmin.biz.form.service;

import java.util.List;
import java.util.Map;

public interface FormService {
    String test(String i,Integer j);

    Map<String, String> selectFormById(String formId,String encodeId) throws Exception;
    String selectAllFormById( String encodeId) throws Exception;
    public  Map<String, Object> selectAllFormMapById(String encodeId) throws Exception;
    void fillFormById( Map<String, Object> form,Map<String,Object> request,Map<String,String> session
    ) throws Exception;

    List<Map<String, String>> getItemListByFormId(String formId,String code) throws Exception;


    List<Map<String, String>> getNavbarListByFormId(String form_id,String code) throws Exception;
}
