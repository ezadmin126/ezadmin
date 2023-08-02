package com.ezadmin.biz.emmber.list;

/**
 *
 **/
public class BaseVO {

    private String itemParamValue;
    private String itemParamOrderValue;
    private String itemParamValueStart;
    private String itemParamValueEnd;

    private String pluginCode;


    private Integer itemId;

    private Integer listId;

    private String itemLabel;

    private String itemName;
    private String html;
    private Integer itemSort;

    private Integer status;

    private String datasource;

    private String itemUrl;

    private String openType;

    private String windowName="打开Window";
    private String area;

    private String width;
    private String minWidth;

    public String getItemUrl() {
        return itemUrl;
    }

    public void setItemUrl(String itemUrl) {
        this.itemUrl = itemUrl;
    }

    public String getOpenType() {
        return openType;
    }

    public void setOpenType(String openType) {
        this.openType = openType;
    }

    public String getWindowName() {
        return windowName;
    }

    public void setWindowName(String windowName) {
        this.windowName = windowName;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getItemNameEnd() {
        return itemName+"_END";
    }


    public String getItemNameOrder() {
        return itemName+"_ORDER";
    }

    public String getItemNameStart() {
        return itemName+"_START";
    }


    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getListId() {
        return listId;
    }

    public void setListId(Integer listId) {
        this.listId = listId;
    }

    public String getItemLabel() {
        return itemLabel;
    }

    public void setItemLabel(String itemLabel) {
        this.itemLabel = itemLabel;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }


    public String getItemParamValue() {
        return itemParamValue;
    }

    public void setItemParamValue(String itemParamValue) {
        this.itemParamValue = itemParamValue;
    }


    public String getItemParamValueStart() {
        return itemParamValueStart;
    }

    public void setItemParamValueStart(String itemParamValueStart) {
        this.itemParamValueStart = itemParamValueStart;
    }

    public String getItemParamValueEnd() {
        return itemParamValueEnd;
    }

    public void setItemParamValueEnd(String itemParamValueEnd) {
        this.itemParamValueEnd = itemParamValueEnd;
    }

    public Integer getItemSort() {
        return itemSort;
    }

    public void setItemSort(Integer itemSort) {
        this.itemSort = itemSort;
    }


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }


    public String getItemParamOrderValue() {
        return itemParamOrderValue;
    }

    public void setItemParamOrderValue(String itemParamOrderValue) {
        this.itemParamOrderValue = itemParamOrderValue;
    }

    public String getPluginCode() {
        return pluginCode;
    }

    public void setPluginCode(String pluginCode) {
        this.pluginCode = pluginCode;
    }

    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }
}
