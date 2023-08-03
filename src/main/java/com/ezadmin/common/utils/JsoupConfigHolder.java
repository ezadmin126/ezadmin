package com.ezadmin.common.utils;

import com.ezadmin.biz.dao.FormDao;
import com.ezadmin.biz.dao.ListDao;
import com.ezadmin.biz.dao.PluginsDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.*;
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
    public static Map<String, String> selectListById(String encodeListId) {
        return ListDao.getInstance().selectListById(encodeListId);
    }
    public static List<Map<String, String>> selectSearchByListId(String encodeListId) {
        return ListDao.getInstance().selectSearchByListId(encodeListId);

    }
    public static List<Map<String, String>> selectNavByListId(String encodeListId) {
        return ListDao.getInstance().selectNavByListId(encodeListId);
    }
    public static List<Map<String, String>> selectRowButtonByListId(String encodeListId) {

        return ListDao.getInstance().selectRowButtonByListId(encodeListId);

    }
    public static List<Map<String, String>> selectTableButtonByListId(String encodeListId) {
        return ListDao.getInstance().selectTableButtonByListId(encodeListId);

    }
    public static List<Map<String, String>> selectColumnByListId(String encodeListId) {
        return ListDao.getInstance().selectColumnByListId(encodeListId);

    }

    public static boolean existHtmlList(String encodeListId) {
        return ListDao.getInstance().existHtmlList(encodeListId.toLowerCase());
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

    public static Map<String, String> getItemListByFormIdAndName(String encodeId, String name) {
        List<Map<String, String>> list =getItemListByFormId(encodeId);
        for (int i = 0; i < list.size(); i++) {
            if (StringUtils.equalsIgnoreCase(list.get(i).get(JsoupUtil.ITEM_NAME), name)) {
                return list.get(i);
            }
        }
        return Collections.emptyMap();
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
    /**
     * 根据名称获取 search
     *

     * @return
     */
    public static Map<String, String> selectSearchByListId(String encodeListId, String name) {
        List<Map<String, String>> list =selectSearchByListId(encodeListId);
        for (int i = 0; i < list.size(); i++) {
            if (StringUtils.equalsIgnoreCase(list.get(i).get(JsoupUtil.ITEM_NAME), name)) {
                return list.get(i);
            }
        }
        return Collections.emptyMap();
    }

    public static List<Map<String, String>> selectListByHtmlConfig(String page, String name, String url) {
        return  ListDao.getInstance().selectListByHtmlConfig(page,name,url);
    }

    public static int listSize(String page, String name, String url) {
       return  ListDao.getInstance().listSize(page,name,url);
    }




    public static List<Map<String, String>> selectFormByHtmlConfig(String page, String name, String url) {
         return FormDao.getInstance().selectFormByHtmlConfig(page,name,url);
    }

    public static int formSize(String page, String name, String url) {
        return FormDao.getInstance().formSize(page,name,url);

    }

    public static Integer updateSearchByListId(String encodeListId, String name, Map<String, String> request) throws IOException {
        return ListDao.getInstance().updateSearchByListId(encodeListId,name,request);
    }

    public static Integer deleteSearchByListId(String encodeListId, String name) throws IOException {
        return ListDao.getInstance().deleteSearchByListId(encodeListId,name);
    }

    public static void updateListSearchSort(String encodeListId, String addSearch) {
         ListDao.getInstance().updateListSearchSort(encodeListId,addSearch);
    }

    /**

     */
    public static void updateListColumnSort(String encodeListId, String addSearch) {
         ListDao.getInstance().updateListColumnSort(encodeListId,addSearch);
    }

    //通过name查找
    public static Map<String, String> selectColumnByListId(String encodeListId, String name) {
        List<Map<String, String>>  list=selectColumnByListId(encodeListId);
        for (int i = 0; i < list.size(); i++) {
            if (StringUtils.equalsIgnoreCase(list.get(i).get(JsoupUtil.ITEM_NAME), name)) {
                return list.get(i);
            }
        }
        return Collections.emptyMap();
    }

    //通过name查找
    public static Integer updateColumnByListId(String encodeListId, String name, Map<String, String> request) throws IOException {
        return ListDao.getInstance().updateColumnByListId(encodeListId,name,request);
    }



    //通过name查找
    public static Integer deleteColumnByListId(String encodeListId, String name) throws IOException {
        return ListDao.getInstance().deleteColumnByListId(encodeListId,name);
    }

    public static Map<String, String> selectCoreByListId(String encodeListId, String name) {
        Map<String, String> list =selectListById(encodeListId);
        if (Utils.isNotEmpty(list)) {
            return list;
        }
        return Collections.emptyMap();
    }

    public static Integer updateCoreByListId(String encodeListId, String name, Map<String, String> request) throws IOException {
        return ListDao.getInstance().updateCoreByListId(encodeListId,name,request);
    }

    public static Map<String, String> selectNavByListId(String encodeListId, String name) {
        List<Map<String, String>> list =selectNavByListId(encodeListId);
        for (int i = 0; i < list.size(); i++) {
            if (StringUtils.equalsIgnoreCase(list.get(i).get(JsoupUtil.ITEM_NAME), name)) {
                return list.get(i);
            }
        }
        return Collections.emptyMap();
    }

    public static Integer updateNavByListId(String encodeListId, String name, Map<String, String> request) throws IOException {

       return ListDao.getInstance().updateNavByListId(encodeListId,name,request);
    }

    public static void updateListNavSort(String encodeListId, String addSearch) {
          ListDao.getInstance().updateListNavSort(encodeListId,addSearch);
    }

    public static Integer deleteNavByListId(String encodeListId, String name) throws IOException {

        return ListDao.getInstance().deleteNavByListId(encodeListId,name);

    }

    public static void updateListTableButtonSort(String encodeListId, String addSearch) {
        ListDao.getInstance().updateListTableButtonSort(encodeListId,addSearch);
    }

    public static Map<String, String> selectTableButtonByListId(String encodeListId, String name) {
        List<Map<String, String>> list = selectTableButtonByListId(encodeListId);
        for (int i = 0; i < list.size(); i++) {
            if (StringUtils.equalsIgnoreCase(list.get(i).get(JsoupUtil.ITEM_NAME), name)) {
                return list.get(i);
            }
        }
        return Collections.emptyMap();
    }

    //通过name查找
    public static Integer updateTableButtonByListId(String encodeListId, String name, Map<String, String> request) throws IOException {

         return ListDao.getInstance().updateTableButtonByListId(encodeListId,name,request);
    }

    //通过name查找
    public static Integer deleteTableButtonByListId(String encodeListId, String name) throws IOException {



        return ListDao.getInstance().deleteTableButtonByListId(  encodeListId,   name) ;
    }


    public static void updateListRowButtonSort(String encodeListId, String rowButtonNames) {
          ListDao.getInstance().updateListRowButtonSort(  encodeListId,   rowButtonNames) ;

    }

    public static Map<String, String> selectRowButtonByListId(String encodeListId, String name) {
        List<Map<String, String>> list = selectRowButtonByListId(encodeListId);
        for (int i = 0; i < list.size(); i++) {
            if (StringUtils.equalsIgnoreCase(list.get(i).get(JsoupUtil.ITEM_NAME), name)) {
                return list.get(i);
            }
        }
        return Collections.emptyMap();
    }

    //通过name查找
    public static Integer updateRowButtonByListId(String encodeListId, String name, Map<String, String> request) throws IOException {
        String pre = request.get("pre");

      //  updateListRowButtonSort(encodeListId, org.apache.commons.lang.StringUtils.join(itemSortList, ","));
        return ListDao.getInstance().updateRowButtonByListId(  encodeListId,   name,  request) ;
    }


    public static Integer deleteFormItemByItemNameAndId(String encodeFormId, String name) throws IOException {
        return FormDao.getInstance().deleteFormItemByItemNameAndId(encodeFormId,name);
    }

    //通过name查找
    public static Integer deleteRowButtonByListId(String encodeListId, String name) throws IOException {
        return ListDao.getInstance().deleteRowButtonByListId(encodeListId,name);
    }

    public static Integer updateFormCoreByFormId(String formCode, Map<String, String> request) throws IOException {


        return FormDao.getInstance().updateFormCoreByFormId(  formCode,   request);
    }


    public static void updateFormItemByFormIdAndName(String encodeFormId, String name, Map<String, String> request) throws IOException {
        FormDao.getInstance().updateFormItemByFormIdAndName(encodeFormId,name,request);
    }


    public static void clear() {
         ListDao.getInstance().clear();
         FormDao.getInstance().clear();
    }


    public static void updateFormItemSort(String encodeFormId, String json) throws IOException {
         FormDao.getInstance().updateFormItemSort(encodeFormId,json);
    }

}
