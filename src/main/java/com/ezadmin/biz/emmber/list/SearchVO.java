package com.ezadmin.biz.emmber.list;

import com.ezadmin.common.enums.DefaultParamEnum;
import com.ezadmin.common.utils.NumberUtils;
import com.ezadmin.common.utils.StringUtils;
import com.ezadmin.common.utils.Utils;
import org.apache.commons.beanutils.BeanMap;

import java.util.HashMap;
import java.util.Map;

public class SearchVO extends  BaseVO{



    private String itemPlaceholder;

    private String initData;

    private String initDataType;

    private String itemSearchOper;

    private String itemJdbcType;

    private String itemAlias;

    private String itemStyle;

    private String itemSearchKey;
    private String itemSearchValue;

    private String itemSearchConcatValue;

    private String itemSearchDateKey;
    private String itemSearchDateValueStart;
    private String itemSearchDateValueEnd;

    private String tableSearchHtml;
    private String validateRules;
    private String validateMessages;

    private String layVerify;



    public Map<String,String> toMap(){
        Map<String,String> map=new HashMap<>();
        map.put("LIST_ID",getListId()+"");
        map.put("ITEM_NAME", StringUtils.upperCase(getItemName()));
        map.put("PLUGIN_CODE",getPluginCode());
        map.put("ITEM_LABEL",getItemLabel());
        map.put("ITEM_PLACEHOLDER",getItemPlaceholder());
        map.put("INIT_DATA",getInitData());
        map.put("INIT_DATA_TYPE",getInitDataType());
        map.put("ITEM_SEARCH_OPERATOR",getItemSearchOper());
        map.put("ITEM_JDBC_TYPE",getItemJdbcType());
        map.put("ITEM_STYLE",getItemStyle());
        map.put("ITEM_ALIAS",getItemAlias());
        map.put("DATASOURCE",getDatasource());
        map.put("VALIDATE_MESSAGES",getValidateMessages()+"");
        map.put("VALIDATE_RULES",getValidateRules()+"");
        return map;
    }
    public  Map toBeanMap(){
        BeanMap beanMap = new BeanMap(this);
        return  new HashMap(beanMap);
    }
    public static SearchVO mapTo(Map<String,String> item){
        SearchVO VO=new SearchVO();
        VO.setListId(NumberUtils.toInt(item.get("LIST_ID")));
        VO.setItemId(NumberUtils.toInt(item.get("ITEM_ID")) );
        VO.setItemName( StringUtils.upperCase(item.get("ITEM_NAME")));
        VO.setPluginCode( item.get("PLUGIN_CODE") );
        VO.setItemLabel(item.get("ITEM_LABEL"));
        VO.setItemPlaceholder(item.get("ITEM_PLACEHOLDER"));
        VO.setInitData(item.get("INIT_DATA"));
        VO.setInitDataType(item.get("INIT_DATA_TYPE"));
        VO.setItemSearchOper(item.get("ITEM_SEARCH_OPERATOR"));
        VO.setItemJdbcType(item.get("ITEM_JDBC_TYPE"));
        VO.setItemStyle(item.get("ITEM_STYLE"));
        VO.setItemAlias(item.get("ITEM_ALIAS"));
        VO.setDatasource(item.get("DATASOURCE"));
        VO.setValidateMessages(item.get("VALIDATE_MESSAGES"));
        VO.setValidateRules(item.get("VALIDATE_RULES"));
        return VO;
    }


    public String getItemSearchConcatValue() {
        return itemSearchConcatValue;
    }

    public void setItemSearchConcatValue(String itemSearchConcatValue) {
        this.itemSearchConcatValue = itemSearchConcatValue;
    }




    public String getItemPlaceholder() {
        return itemPlaceholder;
    }

    public void setItemPlaceholder(String itemPlaceholder) {
        this.itemPlaceholder = itemPlaceholder;
    }

    public String getInitData() {
        return initData;
    }

    public void setInitData(String initData) {
        this.initData = initData;
    }

    public String getInitDataType() {
        return initDataType;
    }

    public void setInitDataType(String initDataType) {
        this.initDataType = initDataType;
    }

    public String getItemSearchOper() {
        return itemSearchOper;
    }

    public void setItemSearchOper(String itemSearchOper) {
        this.itemSearchOper = itemSearchOper;
    }

    public String getItemJdbcType() {
        return itemJdbcType;
    }

    public void setItemJdbcType(String itemJdbcType) {
        this.itemJdbcType = itemJdbcType;
    }

    public String getItemAlias() {
        return itemAlias;
    }

    public void setItemAlias(String itemAlias) {
        this.itemAlias = itemAlias;
    }

    public String getItemStyle() {
        return itemStyle;
    }

    public void setItemStyle(String itemStyle) {
        this.itemStyle = itemStyle;
    }

    public String getItemSearchKey() {
        return itemSearchKey;
    }

    public void setItemSearchKey(String itemSearchKey) {
        this.itemSearchKey = itemSearchKey;
    }

    public String getItemSearchValue() {
        return itemSearchValue;
    }

    public void setItemSearchValue(String itemSearchValue) {
        this.itemSearchValue = itemSearchValue;
    }
    public String getItemSearchDateKey() {
        return itemSearchDateKey;
    }

    public void setItemSearchDateKey(String itemSearchDateKey) {
        this.itemSearchDateKey = itemSearchDateKey;
    }

    public String getItemSearchDateValueStart() {
        return itemSearchDateValueStart;
    }

    public void setItemSearchDateValueStart(String itemSearchDateValueStart) {
        this.itemSearchDateValueStart = itemSearchDateValueStart;
    }

    public String getItemSearchDateValueEnd() {
        return itemSearchDateValueEnd;
    }

    public void setItemSearchDateValueEnd(String itemSearchDateValueEnd) {
        this.itemSearchDateValueEnd = itemSearchDateValueEnd;
    }
    @Override
    public String toString() {
        return "SearchVO{" +getPluginCode()+getItemName()+
                "itemPlaceholder='" + itemPlaceholder + '\'' +
                ", initData='" + initData + '\'' +
                ", initDataType='" + initDataType + '\'' +
                ", itemSearchOper='" + itemSearchOper + '\'' +
                ", itemJdbcType='" + itemJdbcType + '\'' +
                ", itemAlias='" + itemAlias + '\'' +
                ", itemStyle='" + itemStyle + '\'' +
                ", itemSearchKey='" + itemSearchKey + '\'' +
                ", itemSearchValue='" + itemSearchValue + '\'' +
                ", itemSearchConcatValue='" + itemSearchConcatValue + '\'' +
                '}';
    }

    public void transParam(Map<String, Object> requestParamMap) {
         setItemParamValue( Utils.getStringByObject(requestParamMap, getItemName()));
         setItemParamOrderValue( Utils.trimNull(Utils.getStringByObject(requestParamMap, getItemName()+"_ORDER")));
         String start= DefaultParamEnum.getValue(Utils.trimNull(Utils.getStringByObject(requestParamMap, getItemName()+"_START")));
         String end=DefaultParamEnum.getValue(Utils.trimNull(Utils.getStringByObject(requestParamMap, getItemName()+"_END")));
         setItemParamValueStart(start );
         setItemParamValueEnd(end );
         setItemSearchKey(Utils.getStringByObject(requestParamMap,"itemSearchKey"));
         setItemSearchValue(Utils.getStringByObject(requestParamMap,"itemSearchValue"));
         setItemSearchConcatValue(Utils.getStringByObject(requestParamMap,"itemSearchConcatValue"));
         setItemSearchDateKey(Utils.getStringByObject(requestParamMap,"itemSearchDateKey"));
         start= DefaultParamEnum.getValue(Utils.trimNull(Utils.getStringByObject(requestParamMap, "itemSearchDateValueStart")));
         end=DefaultParamEnum.getValue(Utils.trimNull(Utils.getStringByObject(requestParamMap, "itemSearchDateValueEnd")));
         setItemSearchDateValueStart(start);
         setItemSearchDateValueEnd(end);
    }

    public String getTableSearchHtml() {
        return tableSearchHtml;
    }

    public void setTableSearchHtml(String tableSearchHtml) {
        this.tableSearchHtml = tableSearchHtml;
    }

    public String getLayVerify() {
        return layVerify;
    }

    public void setLayVerify(String layVerify) {
        this.layVerify = layVerify;
    }

    public String getValidateRules() {
        return validateRules;
    }

    public void setValidateRules(String validateRules) {
        this.validateRules = validateRules;
    }

    public String getValidateMessages() {
        return validateMessages;
    }

    public void setValidateMessages(String validateMessages) {
        this.validateMessages = validateMessages;
    }
}