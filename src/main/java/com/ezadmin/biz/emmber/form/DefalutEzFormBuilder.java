package com.ezadmin.biz.emmber.form;


import com.ezadmin.common.enums.ItemDataSourceType;
import com.ezadmin.common.enums.ParamNameEnum;
import com.ezadmin.common.utils.*;
import com.ezadmin.plugins.parser.MapParser;
import com.ezadmin.EzBootstrap;
import com.ezadmin.biz.base.service.CoreService;
import com.ezadmin.biz.form.service.FormService;
import com.ezadmin.biz.list.emmber.list.DefaultEzList;
import com.ezadmin.biz.list.emmber.list.EzList;
import com.ezadmin.biz.list.service.ListService;
import com.ezadmin.biz.model.ItemInitData;
 import com.ezadmin.plugins.express.executor.DefaultExpressExecutor;


import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.Context;

import javax.sql.DataSource;
import java.util.*;

/**
 *
 **/
public class DefalutEzFormBuilder implements EzFormBuilder {
    Logger logger= LoggerFactory.getLogger(DefalutEzFormBuilder.class);

    EzFormDTO data = new EzFormDTO();


    protected Map<String, Object> requestParamMap = new HashMap<>();
    protected Map<String, String> sessionParamMap=new HashMap<>();

    FormService formService = EzProxy.singleInstance(FormService.class);
     ListService listService = EzProxy.singleInstance(ListService.class);
    protected DataSource dataSource;

    public DefalutEzFormBuilder(DataSource dataSource, Map<String, Object> requestParamMap
    ,Map<String, String> sessionParamMap) {
        this.dataSource = dataSource;
        this.requestParamMap = requestParamMap;
        this.sessionParamMap=sessionParamMap;
        load();
    }


    private void load() {
        EzFormVO ezFormVO = loadForm();
        List<Map<String, String>> formItemList = loadFormItem();
        data.setForm(ezFormVO);
        data.setFormItem(formItemList);
        data.setFormNavbars(loadFormNavbar());

        data.setFormUrl(requestParamMap.get("ContextPath")+"/ezadmin/form/form-" + ezFormVO.getEncodeFormId());
        data.setFormSubmitUrl(requestParamMap.get("ContextPath")+"/ezadmin/form/doSubmit-" + ezFormVO.getEncodeFormId());
        data.set_DATA_ID_NAME(Utils.getStringByObject(requestParamMap,"_DATA_ID_NAME")+"");
        data.setId(Utils.getStringByObject(requestParamMap,"ID"));
        dataSource= EzBootstrap.instance().getDataSourceByKey(ezFormVO.getDataSource());
    }

    EzFormVO loadForm() {
        try {

            Map<String, String> form = formService.selectFormById(Utils.getStringByObject(requestParamMap,"FORM_ID"),
                    Utils.getStringByObject(requestParamMap,"ENCRYPT_FORM_ID"));
            EzFormVO formVo    = (EzFormVO) BeanTools.mapToBean(form, EzFormVO.class);

            formVo.setFormId(form.get("FORM_ID"));
            requestParamMap.put("FORM_ID",formVo.getFormId());

            formVo.setFormName(form.get(JsoupUtil.FORM_NAME));
            formVo.setAppendHead(form.get(JsoupUtil.APPEND_HEAD));
            formVo.setAppendFoot(form.get(JsoupUtil.APPEND_FOOT));
            formVo.setTemplateBodyForm(form.get("TEMPLATE_BODY_FORM"));
            formVo.setGroupData(form.get(JsoupUtil.GROUP_DATA));
            if(StringUtils.isBlank(formVo.getFormId())){
                formVo.setEncodeFormId(Utils.getStringByObject(requestParamMap,"ENCRYPT_FORM_ID"));
            }else{
                formVo.setEncodeFormId(DESUtils.encryptDES(formVo.getFormId()));
            }
            return formVo;
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
        return new EzFormVO();
    }

    List<Map<String, String>> loadFormItem() {
        try {
            List<Map<String, String>> formItems = formService.getItemListByFormId(Utils.getStringByObject(requestParamMap,"FORM_ID"), Utils.getStringByObject(requestParamMap,"ENCRYPT_FORM_ID"));
             return formItems;
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
        return Collections.emptyList();
    }
    List<Map<String, String>> loadFormNavbar() {
        try {
            List<Map<String, String>> formItems = formService.getNavbarListByFormId(Utils.getStringByObject(requestParamMap,"FORM_ID"), Utils.getStringByObject(requestParamMap,"ENCRYPT_FORM_ID"));

            return formItems;
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public void renderHtml() {
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
                        expressExecutor.datasource(
                        EzBootstrap.instance().getDataSourceByKey(data.getForm().getDataSource()))
                        .express(data.getForm().getInitExpress());
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

        if(Utils.isNotEmpty(data.getFormNavbars() )){
            if(Utils.getLog()!=null) {
                Utils.addLog("开始渲染导航" + data.getFormNavbars() .size());
            }
            boolean select=false;

            for (int i = 0; i <  data.getFormNavbars() .size(); i++) {
                Map<String, String> item=data.getFormNavbars() .get(i);
                try {
                    String item_url=getValue(item,"item_url");
                    String url= MapParser.parseDefaultEmpty(item_url, initItemMap).getResult();
                    item.put("item_url",url);
                    item.put(JsoupUtil.URL,url);
                    item.put("select",""+StringUtils.contains(item_url,data.getForm().getEncodeFormId()));
                    if(StringUtils.contains(item_url,data.getForm().getEncodeFormId())){
                        select=true;
                    }
                }catch (Exception e){
                    if(Utils.getLog()!=null) {
                        Utils.addLog("渲染导航错误" ,e);
                    }
                }
            }
            //默认第一个选中
            if(!select){
                data.getFormNavbars().get(0).put("select",""+true);
            }
        }

        Map<String,Map<String,Object>> validRuleMap=new HashMap<>();
        Map<String,Map<String,Object>> validMsgMap=new HashMap<>();

        for (int i = 0; i < data.getFormItem().size(); i++) {

            Map<String,String> item=data.getFormItem().get(i);
            String item_name=getValue(item,JsoupUtil.ITEM_NAME);
            if(StringUtils.isBlank(item_name)){
                item_name=getValue(item,"name");
            }
            String item_url=getValue(item,"item_url");
            updateValidate(item,validRuleMap,validMsgMap);



            item.put(ParamNameEnum.itemParamValue.getName(),Utils.trimNull(initItemMap.get(item_name)));
            item.put(ParamNameEnum.itemParamValueStart.getName(),Utils.trimNull(initItemMap.get(item_name+ "_START")));
            item.put(ParamNameEnum.itemParamValueEnd.getName(),Utils.trimNull(initItemMap.get(item_name+ "_END")));

            item.put("value",Utils.trimNull(initItemMap.get(item_name)));
            item.put("value_start",Utils.trimNull(initItemMap.get(item_name+ "_START")));
            item.put("value_end",Utils.trimNull(initItemMap.get(item_name+ "_END")));

            Context context = new Context();
            context.setVariable("uploadUrl", requestParamMap.get("ContextPath")+EzBootstrap.instance().getUploadUrl());

            if(StringUtils.startsWith(EzBootstrap.instance().getDownloadUrl(),"http")){
                context.setVariable("downloadUrl", EzBootstrap.instance().getDownloadUrl());

            }else{
                context.setVariable("downloadUrl",requestParamMap.get("ContextPath")+EzBootstrap.instance().getDownloadUrl());
            }


            context.setVariable("contextName",requestParamMap.get("ContextPath"));
            for (Map.Entry<String, String> entry:item.entrySet()){
                context.setVariable(entry.getKey()  ,entry.getValue());
            }

            String itemUrl=Utils.trimNull(initItemMap.get(item_url));
            if(StringUtils.isNotBlank(itemUrl)){
                String url= MapParser.parseDefaultEmpty(itemUrl, initItemMap).getResult();
                context.setVariable("item_url",url);
                context.setVariable(JsoupUtil.URL,url);
            }
            String dataConf=getValue(item,JsoupUtil.DATA);
            String datatype=getValue(item,JsoupUtil.DATATYPE);
            String datasource=getValue(item,JsoupUtil.DATASOURCE);
            if (StringUtils.isNotBlank(dataConf)) {
                if(ItemDataSourceType.isEzList(datatype )){
                    //获取
                    EzList list = new DefaultEzList( dataConf, dataSource, requestParamMap,sessionParamMap);
                    list.renderHtml();
                    context.setVariable("data",list.getEzListDto());
                }
                else {
                    try {
                        DataSource temp=dataSource;
                        if(StringUtils.isNotBlank(datasource)&&EzBootstrap.instance().getDataSourceByKey(datasource)!=null){
                            temp=EzBootstrap.instance().getDataSourceByKey(datasource);
                        }
                        Map nm=new HashMap();
                        nm.putAll(requestParamMap);
                        nm.putAll(sessionParamMap);
                        nm.putAll(initItemMap);
                        ItemInitData items = listService.getSelectItems(temp, dataConf, datatype,
                                nm );
                        context.setVariable("items", items.getItems());
                        context.setVariable("data", items.getEzList());
                        context.setVariable("childItems", items.getChildItems());
                        context.setVariable("itemsJson", JSONUtils.toJSONString(items.getItems()));
                    } catch (Exception e) {
                        logger.error("", e);
                        if (Utils.getLog() != null) {
                            Utils.addLog("error getFormId=" + data.getForm().getFormId() + ",get search select error,fillSearchHtml=" + item, ExceptionUtils.getFullStackTrace(e));
                        }
                    }
                }
            }
            try {
                String  template = Utils.trimNull(listService.getDbTemplateByCode(item.get(JsoupUtil.PLUGIN),
                        0,Utils.trimEmptyDefault(requestParamMap.get(JsoupUtil.PLUGIN_FOLD),"form")).get("PLUGIN_BODY"));

                String html = ThymeleafUtils.processString(template, context);


                    item.put("html",html);

            } catch (Exception e) {
               logger.error(""+item.get(JsoupUtil.PLUGIN),e);
            }

        }
        if(Utils.isNotEmpty(validRuleMap)){
            data.setValidateRules(JSONUtils.toJSONString(validRuleMap));
        }
        if(Utils.isNotEmpty(validMsgMap)){
            data.setValidateMessages(JSONUtils.toJSONString(validMsgMap));
        }
        group();

    }

    private String getValue(Map<String, String> initItemMap,String key){
        if(StringUtils.isBlank(Utils.trimNull(initItemMap.get(key)))){
            String value=Utils.trimNull(initItemMap.get(key.toUpperCase()));
            initItemMap.put(key,value);
                return value;
        }
        return Utils.trimNull(initItemMap.get(key));
    }

    private void updateValidate(Map<String, String> item,Map<String,Map<String,Object>> validRuleMap,
                                Map<String,Map<String,Object>> validMsgMap) {
        String itemName=getValue(item,"item_name") ;
        String rule=getValue(item,"valid_rule") ;
        String message=getValue(item,"valid_msg") ;
        String lay_verify=getValue(item,"lay_verify") ;
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

    private void group() {
        List<Map<String,Object>> groupData=new ArrayList<Map<String,Object>>();
        if(StringUtils.isNotBlank(data.getForm().getGroupData())){
            groupData=JSONUtils.parseListMapString(data.getForm().getGroupData());
        }
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("K",JsoupUtil.EZ_DEFAULT_GROUP);//默认为行展示
        map.put("V",JsoupUtil.EZ_DEFAULT_GROUP);//默认为行展示
        map.put("type","block");//默认为行展示
        groupData.add(map);
        List<EzGroupFormItemVO> groupFormItems=new ArrayList<EzGroupFormItemVO>();
        for (int i = 0; i < groupData.size(); i++) {
            EzGroupFormItemVO groupFormItemVO=new EzGroupFormItemVO();
            String name=Utils.trimNull(groupData.get(i).get("K"));
            String type=Utils.trimNull(groupData.get(i).get("type"));
            groupFormItemVO.setName(name);
            groupFormItemVO.setType(type);

             Map<String,Object> tempitem=null;
            for (int j = 0; j < data.getFormItem().size(); j++) {
                Map<String,String> item=data.getFormItem().get(j);
                String group=StringUtils.isBlank(item.get("group_data"))?JsoupUtil.EZ_DEFAULT_GROUP:item.get("group_data");
                if(StringUtils.equalsIgnoreCase(group,Utils.trimNull(groupData.get(i).get("K")))){
                    if(StringUtils.isBlank(item.get("align"))){
                        item.put("align",type)  ;
                    }
                    if(StringUtils.equals(item.get("align"),"block")){
                        if(tempitem!=null){
                            groupFormItemVO.getList().add(new HashMap<String, Object>(tempitem));
                        }
                        tempitem=null;
                        groupFormItemVO.getList().add(new HashMap<String, Object>(item));
                    }else{
                        if(tempitem==null){
                            tempitem=new HashMap<String, Object>();
                            tempitem.put("align","inline");
                            tempitem.put("inlineItems",new ArrayList<>());
                        }
                        ((List)tempitem.get("inlineItems")).add(item);
                    }
                }
            }
            if(tempitem!=null){
                groupFormItemVO.getList().add(new HashMap<>(tempitem));
            }

            groupFormItems.add(groupFormItemVO);
        }
        data.setGroupFormItem(groupFormItems);
    }

    @Override
    public EzFormDTO getData() {
        return data;
    }

}
