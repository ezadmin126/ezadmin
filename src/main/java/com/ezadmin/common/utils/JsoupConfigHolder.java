package com.ezadmin.common.utils;

import com.ezadmin.dao.FormDao;
import com.ezadmin.dao.ListDao;
import com.ezadmin.dao.PluginsDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.*;

public class JsoupConfigHolder extends  JsoupUtil{
    public static  Logger logger= LoggerFactory.getLogger(JsoupConfigHolder.class);

    public static void init(boolean cache) {
        try {
            logger.info("start init ezadmin plugins ");
            PluginsDao.getInstance().init();
            logger.info("start init ezadmin list ");
            ListDao.getInstance().init();
            logger.info("start init ezadmin form ");
            FormDao.getInstance().init();
            logger.info("end init ezadmin   ");
        } catch (Exception e) {
            logger.error("",e);
            throw new RuntimeException(e);
        }
    }
    public static boolean existHtmlForm(String encodeListId) {
        return FormDao.getInstance().existHtmlForm(encodeListId.toLowerCase());
    }

    public static Map<String, String> selectFormById(String formCode) {
            return FormDao.getInstance().selectFormById(formCode);
    }


    public static List<Map<String, String>> getItemListByFormId(String encodeId) {
         return FormDao.getInstance().getItemListByFormId(encodeId);
    }

    public static Map<String, String> getDbTemplateByCode(String code, String fold) {
        return PluginsDao.getInstance().getDbTemplateByCode(code,fold);
    }
    public  static List<Map<String, Object>> listPlugin(String pre) {
        return PluginsDao.getInstance().listPlugin(pre);
    }
    public static  List<Map<String, Object>> formPlugin() {
        return PluginsDao.getInstance().formPlugin("");
    }
    //**编辑


    public static List<Map<String, String>> selectListByHtmlConfig(String page, String name, String url) {
        return  ListDao.getInstance().selectListByHtmlConfig(page,name,url);
    }

    public static int listSize(String page, String name, String url) {
       return  ListDao.getInstance().listSize(page,name,url);
    }


    public static int formSize(String page, String name, String url) {
        return FormDao.getInstance().formSize(page,name,url);

    }


    public static void clear() {
         ListDao.getInstance().clear();
         FormDao.getInstance().clear();
    }



}
