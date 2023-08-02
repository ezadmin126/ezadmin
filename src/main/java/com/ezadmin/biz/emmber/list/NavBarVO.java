package com.ezadmin.biz.emmber.list;

import com.ezadmin.common.utils.NumberUtils;
import org.apache.commons.beanutils.BeanMap;

import java.util.HashMap;
import java.util.Map;

public class NavBarVO extends  BaseVO{



    private String itemUrl;

    private String openType;

    private String windowName;
    private String selectExpress;
    private boolean select;

    public Map<String,String> toMap(){
        Map<String,String> map=new HashMap<>();
        map.put("LIST_ID",getListId()+"");
        map.put("ITEM_NAME",getItemName());
        map.put("ITEM_URL",getItemUrl());
        map.put("PLUGIN_CODE",getPluginCode());
        map.put("ITEM_LABEL",getItemLabel());
        map.put("OPEN_TYPE",getOpenType());
        map.put("SELECT_EXPRESS",getSelectExpress());
        map.put("ITEM_SORT", NumberUtils.toInt(getItemSort()+"")+"");
        return map;
    }
    public static NavBarVO mapTo(Map<String,String> item){
        NavBarVO vo=new NavBarVO();
        vo.setListId(NumberUtils.toInt(item.get("LIST_ID")));
        vo.setItemName(item.get("ITEM_NAME"));
        vo.setItemUrl(item.get("ITEM_URL"));
        vo.setPluginCode( item.get("PLUGIN_CODE") );
        vo.setItemLabel(item.get("ITEM_LABEL"));
        vo.setOpenType(item.get("OPEN_TYPE"));
        vo.setSelectExpress(item.get("SELECT_EXPRESS"));
        vo.setItemSort(NumberUtils.toInt(item.get("ITEM_SORT")));
        return vo;
    }

    public  Map toBeanMap(){
        BeanMap beanMap = new BeanMap(this);
        return  new HashMap(beanMap);
    }
    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
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

    public String getSelectExpress() {
        return selectExpress;
    }

    public void setSelectExpress(String selectExpress) {
        this.selectExpress = selectExpress;
    }
}