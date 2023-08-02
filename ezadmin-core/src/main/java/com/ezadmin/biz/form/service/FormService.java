package com.ezadmin.biz.form.service;

import java.util.List;
import java.util.Map;

public interface FormService {
    String test(String i,Integer j);

    Map<String, String> selectFormById(String formId,String encodeId) throws Exception;

    List<Map<String, String>> getItemListByFormId(String formId,String code) throws Exception;


    List<Map<String, String>> getNavbarListByFormId(String form_id,String code) throws Exception;
}
