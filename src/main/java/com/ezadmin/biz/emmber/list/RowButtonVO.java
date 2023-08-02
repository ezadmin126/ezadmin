package com.ezadmin.biz.emmber.list;

import com.ezadmin.common.utils.NumberUtils;
import org.apache.commons.beanutils.BeanMap;

import java.util.HashMap;
import java.util.Map;

public class RowButtonVO extends  BaseVO{


    private String itemUrl;

    private String openType;

    private String windowName;
    private String area;

    private String itemStyle;
    private String ez_callback;


    public static RowButtonVO mapTo(Map<String,String> item){
        RowButtonVO vo=new RowButtonVO();
        vo.setListId(NumberUtils.toInt(item.get("LIST_ID")));
       // vo.setItemId(NumberUtils.toInt(item.get("ITEM_ID")) );
        vo.setItemName(item.get("ITEM_NAME"));
        vo.setItemUrl(item.get("ITEM_URL"));
        vo.setPluginCode( item.get("PLUGIN_CODE") );
        vo.setItemLabel(item.get("ITEM_LABEL"));
        vo.setOpenType(item.get("OPEN_TYPE"));
        vo.setWindowName(item.get("WINDOW_NAME"));
        vo.setItemStyle(item.get("ITEM_STYLE"));
        vo.setArea(item.get("AREA"));

        return vo;
    }
    public Map<String,String> toMap(){
        Map<String,String> map=new HashMap<>();
        map.put("LIST_ID",getListId()+"");
        map.put("ITEM_NAME",getItemName());
        map.put("PLUGIN_CODE",getPluginCode());
        map.put("ITEM_LABEL",getItemLabel());
        map.put("ITEM_URL",getItemUrl());
        map.put("OPEN_TYPE",getOpenType());
        map.put("WINDOW_NAME",getWindowName());
        map.put("ITEM_STYLE",getItemStyle());
        map.put("AREA",getArea());

        return map;
    }

    public  Map toBeanMap(){
        BeanMap beanMap = new BeanMap(this);
        return  new HashMap(beanMap);
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

    public String getWindowName() {
        return windowName;
    }

    public void setWindowName(String windowName) {
        this.windowName = windowName == null ? null : windowName.trim();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getItemStyle() {
        return itemStyle;
    }

    public void setItemStyle(String itemStyle) {
        this.itemStyle = itemStyle;
    }

    public String getEz_callback() {
        return ez_callback;
    }

    public void setEz_callback(String ez_callback) {
        this.ez_callback = ez_callback;
    }
}