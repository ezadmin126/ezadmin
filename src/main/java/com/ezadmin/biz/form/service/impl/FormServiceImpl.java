package com.ezadmin.biz.form.service.impl;

import com.ezadmin.biz.dao.FormDao;
import com.ezadmin.biz.form.service.FormService;
import com.ezadmin.biz.list.emmber.list.DefaultEzList;
import com.ezadmin.biz.list.emmber.list.EzList;
import com.ezadmin.biz.list.service.ListService;
import com.ezadmin.biz.model.ItemInitData;
import com.ezadmin.common.annotation.EzCacheAnnotation;
import com.ezadmin.common.annotation.EzConfig;
import com.ezadmin.common.enums.ItemDataSourceType;
import com.ezadmin.common.enums.ParamNameEnum;
import com.ezadmin.common.utils.*;
import com.ezadmin.plugins.express.executor.DefaultExpressExecutor;
import com.ezadmin.plugins.parser.MapParser;
import com.ezadmin.web.Config;
import com.ezadmin.EzBootstrap;
import com.ezadmin.biz.base.service.CoreService;
import com.ezadmin.biz.dao.Dao;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.Context;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormServiceImpl implements FormService {
Logger logger= LoggerFactory.getLogger(FormServiceImpl.class);
    @EzConfig
    Config config;
    Dao dao=Dao.getInstance();
    EzBootstrap bootstrap=EzBootstrap.instance();
    CoreService coreService = EzProxy.singleInstance(CoreService.class);
    ListService listService = EzProxy.singleInstance(ListService.class);
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
        try {
            Map<String, String> form = JsoupConfigHolder.selectFormById(encodeId);
            if (Utils.isNotEmpty(form)) {
                return form;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return Collections.emptyMap();
    }

    @Override
    @EzCacheAnnotation
    public String selectAllFormById(String encodeId) throws Exception {
        try {
            return JSONUtils.toJSONString(FormDao.getInstance().selectAllFormById(encodeId));
        }catch (Exception e){
            logger.error(""+encodeId,e);
            throw e;
        }
    }
    @EzCacheAnnotation
    public  Map<String, Object> selectAllFormMapById(String encodeId) throws Exception {
        try {
            return  FormDao.getInstance().selectAllFormById(encodeId);
        }catch (Exception e){
            logger.error(""+encodeId,e);
           return new HashMap<>();
        }
    }

    @Override
    public void fillFormById(Map<String, Object> form, Map<String, Object> request, Map<String, String> session) throws Exception {
        DataSource dataSource=null;
        Object core=form.get("core");
        if(core!=null){
            dataSource= EzBootstrap.instance().getDataSourceByKey(((Map<String, Object>)form.get("core")).get(JsoupUtil.DATASOURCE));
        }else{
            form.put("core",new HashMap<>());
        }
        Map<String, Object> initMap= Collections.unmodifiableMap(initMap(request,form,dataSource));

        fillItem(request,initMap,form,session,dataSource );
    }
    private void fillItem( Map<String,Object> requestParamMap, Map<String, Object> initItemMap,Map<String, Object> form
            ,Map<String, String> sessionParamMap , DataSource dataSource ) {
        Map<String,Map<String,Object>> validRuleMap=new HashMap<>();
        Map<String,Map<String,Object>> validMsgMap=new HashMap<>();

        List<Map<String,Object>> cardList=(List<Map<String,Object>>)form.get("cards");

        if(Utils.isNotEmpty(cardList)){
            for (int i = 0; i < cardList.size(); i++) {
                List<Map<String,Object>> items=(List<Map<String,Object>>)cardList.get(i).get("items");
                if(Utils.isNotEmpty(items)) {
                    for (int j = 0; j < items.size(); j++) {
                        Map<String, Object> item = items.get(j);
                        String item_name = getStringValue(item, JsoupUtil.ITEM_NAME);

                        String item_url = getStringValue(item, JsoupUtil.URL);
                        updateValidate(item, validRuleMap, validMsgMap);


                        item.put(ParamNameEnum.itemParamValue.getName(), Utils.trimNull(initItemMap.get(item_name)));
                        item.put(ParamNameEnum.itemParamValueStart.getName(), Utils.trimNull(initItemMap.get(item_name + "_START")));
                        item.put(ParamNameEnum.itemParamValueEnd.getName(), Utils.trimNull(initItemMap.get(item_name + "_END")));

                        item.put("value", Utils.trimNull(initItemMap.get(item_name)));
                        item.put("value_start", Utils.trimNull(initItemMap.get(item_name + "_START")));
                        item.put("value_end", Utils.trimNull(initItemMap.get(item_name + "_END")));

                        Context context = new Context();
                        context.setVariable("uploadUrl", EzBootstrap.instance().getUploadUrl());
                        context.setVariable("downloadUrl", EzBootstrap.instance().getDownloadUrl());

                        context.setVariable("contextName", "");
                        for (Map.Entry<String, Object> entry : item.entrySet()) {
                            context.setVariable(entry.getKey(), entry.getValue());
                        }

                        String itemUrl = Utils.trimNull(item_url);
                        if (StringUtils.isNotBlank(itemUrl)) {
                            String url = MapParser.parseDefaultEmpty(itemUrl, initItemMap).getResult();
                            context.setVariable("item_url", url);
                            context.setVariable(JsoupUtil.URL, url);
                        }
                        String dataConf = getStringValue(item, JsoupUtil.DATA);
                        String datatype = getStringValue(item, JsoupUtil.DATATYPE);
                        String datasource = getStringValue(item, JsoupUtil.DATASOURCE);
                        if (StringUtils.isNotBlank(dataConf)) {
                            if (ItemDataSourceType.isEzList(datatype)) {
                                //获取
                                EzList list = new DefaultEzList(dataConf, dataSource, requestParamMap, sessionParamMap);
                                list.renderHtml();
                                context.setVariable("list", list.getEzListDto());
                            } else {
                                try {
                                    DataSource temp = dataSource;
                                    if (StringUtils.isNotBlank(datasource) && EzBootstrap.instance().getDataSourceByKey(datasource) != null) {
                                        temp = EzBootstrap.instance().getDataSourceByKey(datasource);
                                    }
                                    Map nm = new HashMap();
                                    nm.putAll(requestParamMap);
                                    nm.putAll(sessionParamMap);
                                    nm.putAll(initItemMap);
                                    ItemInitData selectItems = listService.getSelectItems(temp, dataConf, datatype,
                                            nm);
                                    context.setVariable("items", selectItems.getItems());
                                    context.setVariable("data", dataConf);
                                    context.setVariable("list", selectItems.getEzList());
                                    context.setVariable("childItems", selectItems.getChildItems());
                                    context.setVariable("itemsJson", JSONUtils.toJSONString(selectItems.getItems()));
                                } catch (Exception e) {
                                    logger.error("", e);
                                    if (Utils.getLog() != null) {
                                        Utils.addLog("error getFormId=" + ",get search select error,fillSearchHtml=" + item, ExceptionUtils.getFullStackTrace(e));
                                    }
                                }
                            }
                        }
                        try {
                            String template = Utils.trimNull(listService.getDbTemplateByCode(Utils.trimNull(item.get("type")),
                                    0, Utils.trimEmptyDefault(requestParamMap.get(JsoupUtil.PLUGIN_FOLD), "form")).get("PLUGIN_BODY"));

                            String html = ThymeleafUtils.processString(template, context);


                            item.put("html", html);

                        } catch (Exception e) {
                            logger.error("" + item.get(JsoupUtil.PLUGIN), e);
                        }

                    }
                }
            }

            Map<String,Object> core=(Map<String,Object>)form.get("core");

            if(Utils.isNotEmpty(validRuleMap)){
                core.put("validaterules",JSONUtils.toJSONString(validRuleMap));
            }
            if(Utils.isNotEmpty(validMsgMap)){
                core.put("validatemessages",JSONUtils.toJSONString(validMsgMap));
            }
        }
    }

    public Map<String, Object> initMap( Map<String,Object> requestParamMap,Map<String, Object> form,DataSource datasource){
        String ID=Utils.getStringByObject(requestParamMap,"ID");
        if(StringUtils.isJsBlank(ID)){
            ID=Utils.getStringByObject(requestParamMap,"INIT_ID");
        }
        if(StringUtils.isJsBlank(ID)){
            ID=Utils.getStringByObject(requestParamMap,"id");
        }
        Map<String, Object> initItemMap=new HashMap<>() ;
        initItemMap.putAll(requestParamMap);
        if ( StringUtils.isNotJsBlank(ID)) {
            try {
                DefaultExpressExecutor expressExecutor=DefaultExpressExecutor.createInstance();
                expressExecutor.datasource(datasource);
                if(form.containsKey("core")){
                    expressExecutor.express(Utils.trimNull(((Map<String, Object>)form.get("core")).get(JsoupUtil.INIT_EXPRESS)));
                }
                expressExecutor.addParam(requestParamMap);
                expressExecutor.addRequestParam( requestParamMap);
                Map<String, Object> resultMap=expressExecutor.executeAndReturnMap();
                for (Map.Entry<String, Object> entry:resultMap.entrySet()){
                    if(StringUtils.isNotBlank(Utils.trimNull(entry.getValue()))){
                        initItemMap.put(entry.getKey(),Utils.trimNull(entry.getValue()));
                    }
                }
            } catch (Exception e) {
                logger.error("",e);
                Utils.addLog(ID,e);
            }
        }else{
            ID="";
        }
        return initItemMap;
    }

    private String getStringValue(Map<String, Object> map,String key){
        return Utils.trimNull(map.get(key));
    }
    private void updateValidate(Map<String, Object> item,Map<String,Map<String,Object>> validRuleMap,
                                Map<String,Map<String,Object>> validMsgMap) {
        String itemName=getStringValue(item,"item_name") ;
        String rule=getStringValue(item,"valid_rule") ;
        String message=getStringValue(item,"valid_msg") ;
        String lay_verify=getStringValue(item,"lay_verify") ;
        try {
            if (StringUtils.isNotBlank(rule)) {
                Map<String,Object> cof=JSONUtils.parseObjectMap(rule);
                if(StringUtils.toBoolean(Utils.trimNull(cof.get("required")))){
                    item.put("redRequired","1");
                }
                validRuleMap.put(itemName,cof) ;
            }
            if(StringUtils.contains(lay_verify,"required")){
                item.put("redRequired","1");
            }
            if (StringUtils.isNotBlank(message)) {
                validMsgMap.put(itemName,JSONUtils.parseObjectMap(message)) ;
            }
        }catch (Exception e){
            Utils.addLog("解析校验json失败",e);
        }
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
