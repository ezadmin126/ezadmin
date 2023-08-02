package com.ezadmin.biz.emmber.list;

import com.ezadmin.common.utils.NumberUtils;
import com.ezadmin.common.utils.StringUtils;
import org.apache.commons.beanutils.BeanMap;

import java.util.HashMap;
import java.util.Map;

public class ColumnVO extends  BaseVO{


    private String headPluginCode;

    private String bodyPluginCode;

    private String initData;

    private String initDataType;

    private String itemUrl;

    private String openType;

    private String itemStyle;

    private String itemOrder;

    private String windowName;

    private String itemJdbcType="";
    private String emptyShow;

    private String searchHtml;

    private String footHtml;
    private String editFlag;
    private String editPlugin;
    private String editExpress;
    private String width;
    private String minWidth;

    public Map toMap(){
        Map<String, String> item=new HashMap<>();
        item.put("LIST_ID",getListId()+"");
        item.put("ITEM_NAME", StringUtils.upperCase(getItemName()));
        item.put("ITEM_URL",getItemUrl());
        item.put("HEAD_PLUGIN_CODE",getHeadPluginCode());
        item.put("BODY_PLUGIN_CODE",getBodyPluginCode());
        item.put("ITEM_LABEL",getItemLabel());
        item.put("INIT_DATA",getInitData());
        item.put("INIT_DATA_TYPE",getInitDataType());
        item.put("OPEN_TYPE",getOpenType());
        item.put("ITEM_ORDER",getItemOrder());
        item.put("ITEM_STYLE",getItemStyle());
        item.put("WINDOW_NAME",getWindowName());
        item.put("ITEM_JDBC_TYPE",getItemJdbcType());
        item.put("EMPTY_SHOW",getEmptyShow());
        item.put("DATASOURCE",getDatasource());
        item.put("EDIT_FLAG",getEditFlag());
        item.put("EDIT_EXPRESS",getEditExpress());
        item.put("EDIT_PLUGIN",getEditPlugin());
        item.put("width",getWidth());
        item.put("minWidth",getMinWidth());
        return item;
    }
    public  Map toBeanMap(){
        BeanMap beanMap = new BeanMap(this);
        return  new HashMap(beanMap);
    }

    public static void main(String[] args) {

        long start=System.currentTimeMillis();
        ColumnVO vo=new ColumnVO()  ;
        vo.setSearchHtml("asdfasdfasdfadfasdf");
        vo.setItemUrl("asdfasdf");
        vo.setEditExpress("af");
        vo.setBodyPluginCode("asdf");
        vo.setHtml("asdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdfa");
        vo.setItemId(111);
        vo.setInitData("asdfasdfasdf");
        for (int i = 0; i < 10000; i++) {
                vo.toBeanMap();
        }
        System.out.println(System.currentTimeMillis()-start);

    }

    public static ColumnVO mapTo(Map<String, String> item){
        ColumnVO vo=new ColumnVO();
        vo.setListId(NumberUtils.toInt(item.get("LIST_ID")));
        vo.setItemId(NumberUtils.toInt(item.get("ITEM_ID")) );
        vo.setItemName(StringUtils.upperCase(item.get("ITEM_NAME")));
        vo.setItemUrl(item.get("ITEM_URL"));
        vo.setHeadPluginCode( item.get("HEAD_PLUGIN_CODE"));
        vo.setBodyPluginCode( item.get("BODY_PLUGIN_CODE"));
        vo.setItemLabel(item.get("ITEM_LABEL"));
        vo.setInitData(item.get("INIT_DATA"));
        vo.setInitDataType(item.get("INIT_DATA_TYPE"));
        vo.setOpenType(item.get("OPEN_TYPE"));
        vo.setItemOrder(item.get("ITEM_ORDER"));
        vo.setItemStyle(item.get("ITEM_STYLE"));
        vo.setWindowName(item.get("WINDOW_NAME"));
        vo.setItemJdbcType(item.get("ITEM_JDBC_TYPE"));
        vo.setEmptyShow(item.get("EMPTY_SHOW"));
        vo.setDatasource(item.get("DATASOURCE"));
vo.setEditExpress(item.get("EDIT_EXPRESS"));
vo.setEditFlag(item.get("EDIT_FLAG"));
vo.setEditPlugin(item.get("EDIT_PLUGIN"));
vo.setWidth(item.get("width"));
vo.setMinWidth(item.get("minWidth"));
        return vo;
    }




    public String getItemOrder() {
        return itemOrder;
    }

    public void setItemOrder(String itemOrder) {
        this.itemOrder = itemOrder;
    }



    public String getInitData() {
        return initData;
    }

    public void setInitData(String initData) {
        this.initData = initData == null ? null : initData.trim();
    }

    public String getInitDataType() {
        return initDataType;
    }

    public void setInitDataType(String initDataType) {
        this.initDataType = initDataType == null ? null : initDataType.trim();
    }

    public String getItemUrl() {
        return itemUrl;
    }

    public void setItemUrl(String itemUrl) {
        this.itemUrl = itemUrl == null ? null : itemUrl.trim();
    }

    public String getOpenType() {
        return openType;
    }

    public void setOpenType(String openType) {
        this.openType = openType == null ? null : openType.trim();
    }

    public String getItemStyle() {
        return itemStyle;
    }

    public void setItemStyle(String itemStyle) {
        this.itemStyle = itemStyle == null ? null : itemStyle.trim();
    }

    public String getWindowName() {
        return windowName;
    }

    public void setWindowName(String windowName) {
        this.windowName = windowName;
    }

    public String getHeadPluginCode() {
        return headPluginCode;
    }

    public void setHeadPluginCode(String headPluginCode) {
        this.headPluginCode = headPluginCode;
    }

    public String getBodyPluginCode() {
        return bodyPluginCode;
    }

    public void setBodyPluginCode(String bodyPluginCode) {
        this.bodyPluginCode = bodyPluginCode;
    }

    public String getItemJdbcType() {
        return itemJdbcType;
    }

    public void setItemJdbcType(String itemJdbcType) {
        this.itemJdbcType = itemJdbcType;
    }

    public String getEmptyShow() {
        return emptyShow;
    }

    public void setEmptyShow(String emptyShow) {
        this.emptyShow = emptyShow;
    }

    public String getSearchHtml() {
        return searchHtml;
    }

    public void setSearchHtml(String searchHtml) {
        this.searchHtml = searchHtml;
    }

    public String getEditFlag() {
        return editFlag;
    }

    public void setEditFlag(String editFlag) {
        this.editFlag = editFlag;
    }

    public String getEditExpress() {
        return editExpress;
    }

    public void setEditExpress(String editExpress) {
        this.editExpress = editExpress;
    }

    public String getEditPlugin() {
        return editPlugin;
    }

    public void setEditPlugin(String editPlugin) {
        this.editPlugin = editPlugin;
    }


    public String getFootHtml() {
        return footHtml;
    }

    public void setFootHtml(String footHtml) {
        this.footHtml = footHtml;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getMinWidth() {
        return minWidth;
    }

    public void setMinWidth(String minWidth) {
        this.minWidth = minWidth;
    }
}