package top.ezadmin.service.impl;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.Context;
import top.ezadmin.EzClientBootstrap;
import top.ezadmin.common.annotation.EzCacheAnnotation;
import top.ezadmin.common.enums.JdbcTypeEnum;
import top.ezadmin.common.enums.ParamNameEnum;
import top.ezadmin.common.utils.*;
import top.ezadmin.dao.Dao;
import top.ezadmin.dao.FormDao;
import top.ezadmin.dao.PluginsDao;
import top.ezadmin.dao.model.ItemInitData;
import top.ezadmin.plugins.express.executor.DefaultExpressExecutor;
import top.ezadmin.plugins.parser.MapParser;
import top.ezadmin.service.FormService;
import top.ezadmin.service.ListService;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormServiceImpl implements FormService {
Logger logger= LoggerFactory.getLogger(FormServiceImpl.class);
     ListService listService = EzProxy.singleInstance(ListService.class);
    @Override
    @EzCacheAnnotation
    public String selectPublishFormById(String encodeId) throws Exception {
        try {
            String sql="select id,EZ_CODE,DATASOURCE,EZ_NAME,EZ_CONFIG from T_EZADMIN_PUBLISH where   EZ_CODE=? and EZ_TYPE=2 " ;
            Map<String, Object> listMap=null;
            try {
                if(!StringUtils.contains("listmanage,formmanage,datamanage",encodeId)){
                    listMap=Dao.getInstance().executeQueryOne(EzClientBootstrap.instance().getEzDataSource(),
                            sql,new Object[]{ encodeId});
                }
            }catch (Exception ee){
            }
            if(Utils.isEmpty(listMap)){
                Map<String, Object> m= FormDao.getInstance().selectAllFormById(encodeId.toLowerCase());
                if(Utils.isEmpty(m)){
                    return null;
                }
                return JSONUtils.toJSONString(m);
            }
            String html=Utils.trimNull(listMap.get("EZ_CONFIG"));
            Map<String,Object>c= FormDao.getInstance().selectAllFormByHtml(html);
            c.put("EZ_CONFIG",html);
            c.put("EZ_CODE",Utils.trimNull(listMap.get("EZ_CODE")));
            c.put("EZ_NAME",Utils.trimNull(listMap.get("EZ_NAME")));
            c.put("DATASOURCE",Utils.trimNull(listMap.get("DATASOURCE")));
            return JSONUtils.toJSONString(c);
        }catch (Exception e){
            throw e;
        }
    }
    @Override
    public void fillFormById(Map<String, Object> form, Map<String, Object> request, Map<String, String> session) throws Exception {
        DataSource dataSource=null;
        if(form==null){
            return;
        }
        Map<String, Object> core=(Map<String, Object>)form.get("core");
        if(core!=null){
            dataSource= EzClientBootstrap.instance().getDataSourceByKey(core.get(JsoupUtil.DATASOURCE));
        }else{
            form.put("core",new HashMap<>());
        }
        //
        Map<String, Object> initMap = Collections.unmodifiableMap(initMap(request, session, form, dataSource));
        try {
            //foot 参数替换
            String foot = Utils.trimNull(core.get(JsoupUtil.APPEND_FOOT));
            if (StringUtils.isNotBlank(foot)) {
                core.put(JsoupUtil.APPEND_FOOT, MapParser.parseDefaultEmpty(foot, initMap).getResult());
            }
        }catch (Exception e){
            Utils.addLog("fillFormById",e);
        }
        try {
            //HEAD 参数替换
            String foot = Utils.trimNull(core.get(JsoupUtil.APPEND_HEAD));
            if (StringUtils.isNotBlank(foot)) {
                core.put(JsoupUtil.APPEND_HEAD, MapParser.parseDefaultEmpty(foot, initMap).getResult());
            }
        }catch (Exception e){
            Utils.addLog("fillFormById",e);
        }
        fillItem(request,initMap,form,session,dataSource );
    }

    private void fillItem( Map<String,Object> requestParamMap, Map<String, Object> initItemMap,Map<String, Object> form
            ,Map<String, String> sessionParamMap , DataSource dataSource ) {
        Map<String,String> validRuleMap=new HashMap<>();
        Map<String,String> validMsgMap=new HashMap<>();

        Map<String,Object> core=(Map<String,Object>)form.get("core");

        List<Map<String,Object>> cardList=(List<Map<String,Object>>)form.get("cards");

        if(Utils.isNotEmpty(cardList)){
            for (int i = 0; i < cardList.size(); i++) {
                List<Map<String,Object>> items=(List<Map<String,Object>>)cardList.get(i).get("items");
                String frameUrl=Utils.trimNull(cardList.get(i).get(JsoupUtil.FRAMEURL));
                String FORM_CARD_BTN_NAME=Utils.trimNull(cardList.get(i).get(JsoupUtil.FORM_CARD_BTN_NAME));
                if (StringUtils.isNotBlank(frameUrl)&&
                        !StringUtils.equals(Utils.trimNull(requestParamMap.get(JsoupUtil.FORM_EDIT_FLAG)),"1")) {//只有不是编辑状态才替换
                    String url = MapParser.parseDefaultEmpty(frameUrl, requestParamMap).getResult();
                    cardList.get(i).put(JsoupUtil.FRAMEURL,url);
                }
                if (StringUtils.isNotBlank(FORM_CARD_BTN_NAME)&&
                        !StringUtils.equals(Utils.trimNull(requestParamMap.get(JsoupUtil.FORM_EDIT_FLAG)),"1")) {//只有不是编辑状态才替换
                    String FORM_CARD_BTN_URL=Utils.trimNull(cardList.get(i).get(JsoupUtil.FORM_CARD_BTN_URL));
                    String url = MapParser.parseDefaultEmpty(FORM_CARD_BTN_URL, requestParamMap).getResult();
                    cardList.get(i).put(JsoupUtil.FORM_CARD_BTN_URL,url);
                }
                if(Utils.isNotEmpty(items)) {
                   // continue;
                    for (int j = 0; j < items.size(); j++) {
                        Map<String, Object> item = items.get(j);
                        String item_name = getStringValue(item, JsoupUtil.ITEM_NAME);
                        String jdbcType = getStringValue(item, JsoupUtil.JDBCTYPE);
                        String defaultValue=getStringValue(item, "value");
                        String defaultValueS=getStringValue(item, "valueStart");
                        String defaultValueE=getStringValue(item, "valueEnd");

                        String item_url = getStringValue(item, JsoupUtil.URL);
                        updateValidate(item, validRuleMap, validMsgMap);

                        item.put(ParamNameEnum.itemParamValue.getName(), Utils.trimNullDefault(initItemMap.get(item_name),defaultValue));
                        item.put(ParamNameEnum.itemParamValueStart.getName(), Utils.trimNullDefault(initItemMap.get(item_name + "_START"),defaultValueS));
                        item.put(ParamNameEnum.itemParamValueEnd.getName(), Utils.trimNullDefault(initItemMap.get(item_name + "_END"),defaultValueE));

                        item.put("value", calulateData(Utils.trimNullDefault(initItemMap.get(item_name),defaultValue),"","",jdbcType));
                        item.put("value_start", calulateData(Utils.trimNullDefault(initItemMap.get(item_name + "_START"),defaultValueS),"","",jdbcType));
                        item.put("value_end",calulateData( Utils.trimNullDefault(initItemMap.get(item_name + "_END"),defaultValueE),"","",jdbcType));
                        Context context = new Context();
                        context.setVariable("uploadUrl", EzClientBootstrap.instance().getUploadUrl());
                        context.setVariable("downloadUrl", EzClientBootstrap.instance().getDownloadUrl());
                        if(StringUtils.equals(Utils.trimNull(requestParamMap.get(JsoupUtil.PLUGIN_FOLD)),"detail")){
                            context.setVariable("disable_flag",true);
                        }
                        context.setVariable("contextName", "");
                        for (Map.Entry<String, Object> entry : item.entrySet()) {
                            if(entry.getKey().equalsIgnoreCase("readonly")||entry.getKey().equalsIgnoreCase("disabled")){
                                context.setVariable(entry.getKey(),entry.getKey());
                            }else{
                                context.setVariable(entry.getKey(), entry.getValue());
                            }
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
                        Map<String, String> plugin = PluginsDao.getInstance().getPlugin(Utils.trimNull(core.get(JsoupUtil.ADMINSTYLE)),Utils.trimEmptyDefault(requestParamMap.get(JsoupUtil.PLUGIN_FOLD), "form"), Utils.trimNull(item.get("type")));
                        //外部list
                        if(StringUtils.equals(Utils.trimNull(item.get("type")),"table")||
                                StringUtils.equals(Utils.trimNull(item.get("type")),"tableview")){
                            try {
                                Map<String, Object> list = new HashMap<>();
                                if(StringUtils.isBlank(dataConf)){
                                    throw new IllegalArgumentException("列表类型，必须配置数据源为列表的编码");
                                }
                                if (StringUtils.isNotBlank(dataConf)) {
                                    list = JSONUtils.parseObjectMap(listService.selectPublishListById(dataConf));
                                }
                                listService.fillListById(list, requestParamMap, sessionParamMap);
                                context.setVariable("subdata", list);
                                String template= Utils.trimNull(plugin.get("PLUGIN_BODY"));
                                String html = ThymeleafUtils.processString(template, context);
                                item.put("html", html);
                            }catch (Exception e){
                                logger.error(""+item,e);
                            }
                            continue;
                        }

                        if (StringUtils.isNotBlank(dataConf)) {
                            try {
                                DataSource temp = dataSource;
                                if (StringUtils.isNotBlank(datasource) && EzClientBootstrap.instance().getDataSourceByKey(datasource) != null) {
                                    temp = EzClientBootstrap.instance().getDataSourceByKey(datasource);
                                }
                                Map nm = new HashMap();
                                nm.putAll(requestParamMap);
                                nm.putAll(sessionParamMap);
                                nm.putAll(initItemMap);
                                ItemInitData selectItems = listService.getSelectItems(temp, dataConf, datatype, nm);

                                String v=Utils.trimNull(initItemMap.get(item_name));
                                String[] vs=StringUtils.split(v, ",");
                                try {
                                    if (Utils.trimNull(item.get("type")).contains("input-checkbox")) {
                                        vs = JSONUtils.parseArray(v, String.class).toArray(new String[0]);
                                    }
                                }catch (Exception e){
                                }
                                for (int i1 = 0; i1 < selectItems.getItems().size(); i1++) {
                                    if(ArrayUtils.contains(vs,selectItems.getItems().get(i1).get("K"))){
                                        selectItems.getItems().get(i1).put("checked", true);
                                    }else{
                                        selectItems.getItems().get(i1).put("checked", false);
                                    }
                                }
                                context.setVariable("items", selectItems.getItems());
                                context.setVariable("data", dataConf);
                                context.setVariable("childItems", selectItems.getChildItems());
                                context.setVariable("itemsJson", JSONUtils.toJSONString(selectItems.getItems()));
                                //计算checkbox显示
                            } catch (Exception e) {
                              Utils.addLog("error getFormId=" + ",get search select error,fillSearchHtml=" + item,e);
                            }
                        }
                        //生成searchTag
                        if(Utils.trimNull(item.get("type")).contains("xmselect")
                        ){
                            Map<String,String> attrMap= (Map<String,String>)item.get("attrMap");
                            attrMap.put("value", StringEscapeUtils.escapeHtml(item.get(ParamNameEnum.itemParamValue.getName())+""));
                            attrMap.put("name",item.get(JsoupUtil.ITEM_NAME)+"");
                            //  attrMap.put("itemsJson",Utils.trimNull(context.getVariable("itemsJson")));
                            attrMap.putIfAbsent("id","ITEM_ID_"+item.get(JsoupUtil.ITEM_NAME)+"");
                            // attrMap.putIfAbsent("lay-affix","clear" );
                            attrMap.putIfAbsent("lay-verify",attrMap.get(JsoupUtil.LAYVERIFY) );
                            attrMap.put("class", (Utils.trimNull(attrMap.get("class")).replace("layui-input","")) );

                            //  attrMap.put("class","layui-input "+attrMap.get("class") );
                            StringBuilder sb=new StringBuilder("<div ");
                            attrMap.forEach((k,v)->{
                                if(StringUtils.equals(k,"data")||StringUtils.isBlank(v)){
                                    ;return;
                                }
                                sb.append(k);
                                sb.append("='");
                                sb.append(v);
                                sb.append("'  ");
                            });
                            sb.append("></div>");
                            context.setVariable("serverDom",sb.toString());
                            //页面不展示data
                            attrMap.remove("data");
                            context.setVariable("attrMap",JSONUtils.toJSONString(attrMap));
                        }
                        else if(Utils.trimNull(item.get("type")).equals("input-text")
                                ||
                                Utils.trimNull(item.get("type")).equals("input-text-group")
                                ||
                                Utils.trimNull(item.get("type")).equalsIgnoreCase("password")
                        ){
                            Map<String,String> attrMap= (Map<String,String>)item.get("attrMap");
                            attrMap.put("value", StringEscapeUtils.escapeHtml(item.get(ParamNameEnum.itemParamValue.getName())+""));
                            attrMap.put("name",item.get(JsoupUtil.ITEM_NAME)+"");
                            //  attrMap.put("itemsJson",Utils.trimNull(context.getVariable("itemsJson")));
                            attrMap.putIfAbsent("id","ITEM_ID_"+item.get(JsoupUtil.ITEM_NAME));
                            if( Utils.trimNull(item.get("type")).equalsIgnoreCase("input-text")){
                                attrMap.put("lay-affix","clear" );
                            }
                            if( Utils.trimNull(item.get("type")).equalsIgnoreCase("password")){
                                attrMap.put("lay-affix","eye" );
                            }
                            attrMap.putIfAbsent("lay-verify",attrMap.get(JsoupUtil.LAYVERIFY) );
                            attrMap.put("class","layui-input "+(Utils.trimNull(attrMap.get("class")).replace("layui-input ","")) );
                            StringBuilder sb=new StringBuilder("<input ");
                            attrMap.forEach((k,v)->{
                                if(k.equalsIgnoreCase("readonly")||k.equalsIgnoreCase("disabled")){
                                    v=k;

                                }
                                if(

                                        StringUtils.equals(k,"data")||StringUtils.isBlank(v)){
                                    ;return;
                                }
                                sb.append(k);
                                sb.append("='");
                                sb.append(v);
                                sb.append("'  ");
                            });
                            sb.append(">");

                            context.setVariable("suffix",attrMap.get("suffix"));
                            context.setVariable("serverDom",sb.toString());
                            context.setVariable("attrMap",JSONUtils.toJSONString(attrMap));
                        }
                        else if(Utils.trimNull(item.get("type")).equals("textarea")
                        ){
                            String value=StringEscapeUtils.escapeHtml(item.get(ParamNameEnum.itemParamValue.getName())+"");
                            Map<String,String> attrMap= (Map<String,String>)item.get("attrMap");
                            attrMap.put("value", StringEscapeUtils.escapeHtml(item.get(ParamNameEnum.itemParamValue.getName())+""));
                            attrMap.put("text", value);
                            attrMap.put("name",item.get(JsoupUtil.ITEM_NAME)+"");
                            //  attrMap.put("itemsJson",Utils.trimNull(context.getVariable("itemsJson")));
                            attrMap.putIfAbsent("id","ITEM_ID_"+item.get(JsoupUtil.ITEM_NAME));
                            attrMap.putIfAbsent("lay-affix","clear" );
                            attrMap.put("class","layui-textarea "+(Utils.trimNull(attrMap.get("class")).replace("layui-textarea ","")) );

                            attrMap.putIfAbsent("lay-verify",attrMap.get(JsoupUtil.LAYVERIFY) );

                            StringBuilder sb=new StringBuilder("<textarea ");
                            attrMap.forEach((k,v)->{
                                if(
                                        StringUtils.equals(k,"data")||
                                                StringUtils.equals(k,JsoupUtil.EZCONFIG)||
                                                StringUtils.equals(k,"attrMap")||
                                                StringUtils.isBlank(v)){
                                    ;return;
                                }
                                sb.append(k);
                                sb.append("='");
                                sb.append(v);
                                sb.append("'  ");
                            });
                            sb.append(">"+value+"</textarea>");
                            context.setVariable("serverDom",sb.toString());
                            context.setVariable("attrMap",JSONUtils.toJSONString(attrMap));
                        }
                        else if(Utils.trimNull(item.get("type")).equals("cascader")
                                ||Utils.trimNull(item.get("type")).equals("cascadersql")){
                            Map<String,String> attrMap= (Map<String,String>)item.get("attrMap");
                            attrMap.put("value", StringEscapeUtils.escapeHtml(item.get(ParamNameEnum.itemParamValue.getName())+""));
                            attrMap.put("name",item.get(JsoupUtil.ITEM_NAME)+"");
                            //  attrMap.put("itemsJson",Utils.trimNull(context.getVariable("itemsJson")));
                            attrMap.putIfAbsent("id","ITEM_ID_"+item.get(JsoupUtil.ITEM_NAME));
                            attrMap.putIfAbsent("lay-affix","clear" );
                            attrMap.putIfAbsent("lay-verify",attrMap.get(JsoupUtil.LAYVERIFY) );
                            attrMap.put("class","layui-input ez-laycascader "+(Utils.trimNull(attrMap.get("class")).replace("ez-laycascader","").replace("layui-input","")) );
                            StringBuilder sb=new StringBuilder("<input ");
                            attrMap.forEach((k,v)->{
                                if(k.equalsIgnoreCase("readonly")||k.equalsIgnoreCase("disabled")){
                                    v=k;

                                }
                                if(StringUtils.equals(k,"type")||
                                        StringUtils.equals(k,"valid_msg")||
                                        StringUtils.equals(k,"valid_rule")||
                                        StringUtils.equals(k,"data")||StringUtils.isBlank(v)){
                                    ;return;
                                }
                                sb.append(k);
                                sb.append("='");
                                sb.append(v);
                                sb.append("'  ");
                            });
                            sb.append(" type='text'>");
                            context.setVariable("serverDom",sb.toString());
                            context.setVariable("attrMap",JSONUtils.toJSONString(attrMap));
                        }

                        try {
                            String template= Utils.trimNull(plugin.get("PLUGIN_BODY"));
                            String html = ThymeleafUtils.processString(template, context);
                            item.put("html", html);
                        } catch (Exception e) {
                            logger.error("" + item.get(JsoupUtil.TYPE), e);
                        }
                    }
                }
            }

            if(Utils.isNotEmpty(validRuleMap)){
                StringBuilder sb=new StringBuilder();
                validRuleMap.forEach((k,v)->{
                    sb.append(",'");
                    sb.append(k);
                    sb.append("':");
                    sb.append(v);

                });
                if(sb.length()>0){
                    sb.setCharAt(0,'{');
                    sb.append("}");
                }else{
                    sb.append("{");
                    sb.append("}");
                }
                core.put("validaterules",sb.toString());
            }
            if(Utils.isNotEmpty(validMsgMap)){
                StringBuilder sb=new StringBuilder();
                validMsgMap.forEach((k,v)->{
                    sb.append(",'");
                    sb.append(k);
                    sb.append("':");
                    sb.append(v);
                });
                if(sb.length()>0){
                    sb.setCharAt(0,'{');
                    sb.append("}");
                }else{
                    sb.append("{");
                    sb.append("}");
                }
                core.put("validatemessages",sb.toString());
            }
        }
    }

    public static void main(String[] args) {
        System.out.println(new StringBuilder("123").substring(0,"123".length()-1));
    }

    public Map<String, Object> initMap( Map<String,Object> requestParamMap,Map<String,String> session,Map<String, Object> form,DataSource datasource){
        String ID=Utils.getStringByObject(requestParamMap,"ID");
        if(StringUtils.isJsBlank(ID)){
            ID=Utils.getStringByObject(requestParamMap,"INIT_ID");
        }
        if(StringUtils.isJsBlank(ID)){
            ID=Utils.getStringByObject(requestParamMap,"id");
        }
        if(StringUtils.isJsBlank(ID)){
            ID="ID";
        }
        Map<String, Object> initItemMap=new HashMap<>() ;
        initItemMap.putAll(requestParamMap);
        if(StringUtils.isNotBlank(ID)) {
            try {
                DefaultExpressExecutor expressExecutor = DefaultExpressExecutor.createInstance();
                expressExecutor.datasource(datasource);
                if (form.containsKey("core")) {
                    expressExecutor.express(Utils.trimNull(((Map<String, Object>) form.get("core")).get(JsoupUtil.INIT_EXPRESS)));
                }
                expressExecutor.addParam(requestParamMap);
                expressExecutor.addRequestParam(requestParamMap);
                expressExecutor.addSessionParam(session);
                Map<String, Object> resultMap = expressExecutor.executeAndReturnMap();
                for (Map.Entry<String, Object> entry : resultMap.entrySet()) {
                    if (StringUtils.isNotBlank(Utils.trimNull(entry.getValue()))) {
                        initItemMap.put(entry.getKey(), Utils.trimNull(entry.getValue()));
                    }
                }
            } catch (Exception e) {
                 Utils.addLog(JSONUtils.toJSONString(form.get("core")),e);
            }
        }
        if(initItemMap.containsKey(ID)&&StringUtils.isBlank(Utils.trimNull(requestParamMap.get(ID)))){
            requestParamMap.put("ID",initItemMap.get("ID"));
        }
        return initItemMap;
    }

    private String getStringValue(Map<String, Object> map,String key){
        return Utils.trimNull(map.get(key));
    }
    private void updateValidate(Map<String, Object> item,Map<String,String> validRuleMap,
                                Map<String,String> validMsgMap) {
        String itemName=getStringValue(item,"item_name") ;
        String rule=getStringValue(item,"valid_rule") ;
        String message=getStringValue(item,"valid_msg") ;
        String lay_verify=getStringValue(item,"lay_verify") ;
        if(StringUtils.contains(lay_verify,"required")||StringUtils.contains(rule,"required")){
            item.put("redRequired","1");
        }
        try {
            if (StringUtils.isNotBlank(rule)) {
                validRuleMap.put(itemName,rule) ;
            }
            if (StringUtils.isNotBlank(message)) {
                validMsgMap.put(itemName,message) ;
            }
        }catch (Exception e){
            Utils.addLog("解析校验json失败",e);
        }
    }
    public Map<String,Object>  selectConfigEditForm(String code  ) throws Exception {
        String sql="select id,EZ_CODE,DATASOURCE,EZ_NAME,EZ_CONFIG from T_EZADMIN_EDIT where   EZ_CODE=? and EZ_TYPE=2 " +
                "";
        Map<String, Object> listMap=Dao.getInstance().executeQueryOne(EzClientBootstrap.instance().getEzDataSource(),
                sql,new Object[]{ code });
        if(Utils.isEmpty(listMap)){
            return listMap;
        }
        String html=Utils.trimNull(listMap.get("EZ_CONFIG"));
        Map<String,Object>c= FormDao.getInstance().selectAllFormByHtml(html);
        c.put("EZ_CONFIG",html);
        c.put("EZ_CODE",Utils.trimNull(listMap.get("EZ_CODE")));
        c.put("EZ_NAME",Utils.trimNull(listMap.get("EZ_NAME")));
        c.put("DATASOURCE",Utils.trimNull(listMap.get("DATASOURCE")));
        return c;
    }
    private String calulateData(String dataInDb, String globalEmptyShow, String columnEmptyShow,String jdbcType) {
        if(StringUtils.isBlank(dataInDb)){
            return Utils.trimEmptyDefault(dataInDb,columnEmptyShow,globalEmptyShow);
        }
        switch (JdbcTypeEnum.get(jdbcType)){
            case NUMBER:
            case NUMBER2:
                BigDecimal result=   new BigDecimal(dataInDb).setScale(2,BigDecimal.ROUND_HALF_UP);
                return result.toString();
            case NUMBER1:
                return  new BigDecimal(dataInDb).setScale(1,BigDecimal.ROUND_HALF_UP).toString();
            case NUMBER3:
                return  new BigDecimal(dataInDb).setScale(3,BigDecimal.ROUND_HALF_UP).toString();
            case NUMBER4:
                return  new BigDecimal(dataInDb).setScale(4,BigDecimal.ROUND_HALF_UP).toString();
            case DATE:
                return  EzDateUtils.toDateFormat(dataInDb);
            case DATETIME:
                return  EzDateUtils.toDateTimeFormat(dataInDb);
            default:
                return dataInDb;
        }
    }

}
