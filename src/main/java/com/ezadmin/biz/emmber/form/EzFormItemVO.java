package com.ezadmin.biz.emmber.form;

import com.ezadmin.biz.emmber.list.BaseVO;

import java.util.HashMap;
import java.util.Map;

/**
 *
 **/
public class EzFormItemVO extends BaseVO {

    private Integer itemId;

    private String itemLabel;

    private String itemName;

    private String itemPlaceholder;

    private Integer status;

    private Integer formId;

    private String initData;

    private String itemSearchOper;

    private String itemJdbcType;

    private Integer itemSort;

    private String itemDesc;

    private String itemAlias;

    private String initDataType;

    private String validRule;

    private String validMsg;

    private String layVerify;
    /**
     * @see EzFormItemVO
     */
    private String itemPosition;
    private String itemRequired="0";

    private String itemLayout="layui-input-block";
    private String rightDesc="";

    private String groupData;
    private String groupDataInit;

    private String itemStyle;



    private Map<String,Object> params=new HashMap<>();

    public String getItemMaxUpload() {
        return itemMaxUpload;
    }

    public void setItemMaxUpload(String itemMaxUpload) {
        this.itemMaxUpload = itemMaxUpload;
    }

    private String itemMaxUpload="0-1";



    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
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

    public String getItemPlaceholder() {
        return itemPlaceholder;
    }

    public void setItemPlaceholder(String itemPlaceholder) {
        this.itemPlaceholder = itemPlaceholder;
    }


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getFormId() {
        return formId;
    }

    public void setFormId(Integer formId) {
        this.formId = formId;
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

    public Integer getItemSort() {
        return itemSort;
    }

    public void setItemSort(Integer itemSort) {
        this.itemSort = itemSort;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    public String getItemAlias() {
        return itemAlias;
    }

    public void setItemAlias(String itemAlias) {
        this.itemAlias = itemAlias;
    }


    public String getValidRule() {
        return validRule;
    }

    public void setValidRule(String validRule) {
        this.validRule = validRule;
    }

    public String getValidMsg() {
        return validMsg;
    }

    public void setValidMsg(String validMsg) {
        this.validMsg = validMsg;
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

    public String getItemRequired() {
        return itemRequired;
    }

    public void setItemRequired(String itemRequired) {
        this.itemRequired = itemRequired;
    }

    public String getItemPosition() {
        return itemPosition;
    }

    public void setItemPosition(String itemPosition) {
        this.itemPosition = itemPosition;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public String getItemLayout() {
        return itemLayout;
    }

    public void setItemLayout(String itemLayout) {
        this.itemLayout = itemLayout;
    }

    public String getRightDesc() {
        return rightDesc;
    }

    public void setRightDesc(String rightDesc) {
        this.rightDesc = rightDesc;
    }


    public String getGroupData() {
        return groupData;
    }

    public void setGroupData(String groupData) {
        this.groupData = groupData;
    }

    public String getLayVerify() {
        return layVerify;
    }

    public void setLayVerify(String layVerify) {
        this.layVerify = layVerify;
    }

    @Override
    public String toString() {
        return "EzFormItemVO{" +
                "itemId=" + itemId +
                ", itemLabel='" + itemLabel + '\'' +
                ", itemName='" + itemName + '\'' +
                ", itemPlaceholder='" + itemPlaceholder + '\'' +
                ", status=" + status +
                ", formId=" + formId +
                ", initData='" + initData + '\'' +
                ", itemSearchOper='" + itemSearchOper + '\'' +
                ", itemJdbcType='" + itemJdbcType + '\'' +
                ", itemSort=" + itemSort +
                ", itemDesc='" + itemDesc + '\'' +
                ", itemAlias='" + itemAlias + '\'' +
                ", initDataType='" + initDataType + '\'' +
                ", validRule='" + validRule + '\'' +
                ", validMsg='" + validMsg + '\'' +
                ", layVerify='" + layVerify + '\'' +
                ", itemPosition='" + itemPosition + '\'' +
                ", itemRequired='" + itemRequired + '\'' +
                ", itemLayout='" + itemLayout + '\'' +
                ", rightDesc='" + rightDesc + '\'' +
                ", groupData='" + groupData + '\'' +
                ", params=" + params +
                ", itemMaxUpload='" + itemMaxUpload + '\'' +
                '}';
    }

    public String getGroupDataInit() {
        return groupDataInit;
    }

    public void setGroupDataInit(String groupDataInit) {
        this.groupDataInit = groupDataInit;
    }

    public String getItemStyle() {
        return itemStyle;
    }

    public void setItemStyle(String itemStyle) {
        this.itemStyle = itemStyle;
    }
}
