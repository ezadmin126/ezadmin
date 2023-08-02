package com.ezadmin.biz.emmber.form;

import com.ezadmin.common.utils.NumberUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @title: EzFormNavbarVO
 * @Author EzAdmin
 * @Date: 2022/3/5 15:23
 */
public class EzFormNavbarVO {

    private String itemUrl;

    private String openType;

    private String windowName;
    private String selectExpress;
    private boolean select;

    private Integer itemId;

    private Integer formId;

    private String itemLabel;

    private String itemName;
    private String html;
    private Integer itemSort;

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getFormId() {
        return formId;
    }

    public void setFormId(Integer formId) {
        this.formId = formId;
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

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public Integer getItemSort() {
        return itemSort;
    }

    public void setItemSort(Integer itemSort) {
        this.itemSort = itemSort;
    }

    public Map<String,String> toMap(){
        Map<String,String> map=new HashMap<>();
        map.put("FORM_ID",getFormId()+"");
        map.put("ITEM_NAME",getItemName());
        map.put("ITEM_URL",getItemUrl());
        map.put("ITEM_LABEL",getItemLabel());
        map.put("OPEN_TYPE",getOpenType());
        map.put("SELECT_EXPRESS",getSelectExpress());
        map.put("ITEM_SORT", NumberUtils.toInt(getItemSort()+"")+"");
        return map;
    }
    public static EzFormNavbarVO mapTo(Map<String,String> item){
        EzFormNavbarVO vo=new EzFormNavbarVO();
        vo.setFormId(NumberUtils.toInt(item.get("FORM_ID")));
        vo.setItemName(item.get("ITEM_NAME"));
        vo.setItemUrl(item.get("ITEM_URL"));
        vo.setItemLabel(item.get("ITEM_LABEL"));
        vo.setOpenType(item.get("OPEN_TYPE"));
        vo.setSelectExpress(item.get("SELECT_EXPRESS"));
        vo.setItemSort(NumberUtils.toInt(item.get("ITEM_SORT")));
        return vo;
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
