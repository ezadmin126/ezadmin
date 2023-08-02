package com.ezadmin.biz.form.service.impl;

import com.ezadmin.biz.form.service.FormService;
import com.ezadmin.common.annotation.EzCacheAnnotation;
import com.ezadmin.common.annotation.EzConfig;
import com.ezadmin.common.utils.EzProxy;
import com.ezadmin.common.utils.Utils;
import com.ezadmin.web.Config;
import com.ezadmin.EzBootstrap;
import com.ezadmin.biz.base.service.CoreService;
import com.ezadmin.biz.dao.Dao;
import com.ezadmin.common.utils.JsoupConfigHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FormServiceImpl implements FormService {
Logger logger= LoggerFactory.getLogger(FormServiceImpl.class);
    @EzConfig
    Config config;
    Dao dao=Dao.getInstance();
    EzBootstrap bootstrap=EzBootstrap.instance();
    CoreService coreService = EzProxy.singleInstance(CoreService.class);
    @Override
    @EzCacheAnnotation
    public String test(String i, Integer j) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return i+j+System.currentTimeMillis();
    } @EzCacheAnnotation
    public Map<String, String> selectFormById(String formId,String encodeId) throws Exception {
        Map<String, String> form=  JsoupConfigHolder.selectFormById(encodeId);
        if(Utils.isNotEmpty(form)){
            return form;
        }
        return Collections.emptyMap();
    }

    @Override
    @EzCacheAnnotation
    public List<Map<String, String>> getItemListByFormId(String formId,String encodeId ) throws Exception {
        if(JsoupConfigHolder.existHtmlForm(encodeId)){
            return  JsoupConfigHolder.getItemListByFormId(encodeId);
        }
        return Collections.emptyList();
    }
    @Override
    @EzCacheAnnotation
    public List<Map<String, String>> getNavbarListByFormId(String formId,String encodeId) throws Exception {

        if(JsoupConfigHolder.existHtmlForm(encodeId)){
            //暂时不做
            return  Collections.emptyList();//JsoupConfigHolder.getNavbarListByFormId(encodeId);
        }
        return Collections.emptyList();
    }

}
