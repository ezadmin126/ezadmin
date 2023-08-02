package com.ezadmin.common.enums;

import com.ezadmin.common.utils.NumberUtils;

public enum ItemTypeEnum {
    SEARCH(1, "搜索"), TABLE_BUTTON(2, "表头按钮"),//
    THEAD(3, "表头"), ROW_BUTTON(4, "行按钮");
    Integer type;
    private String desc;

    public Integer getType() {
        return type;
    }

    private ItemTypeEnum(Integer type1, String desc1) {
        this.type = type1;
        this.desc = desc1;
    }

    public static boolean isSearch(Object type) {
        if (type == null) {
            return false;
        }
        if (SEARCH.type.equals(NumberUtils.toInt(type + ""))) {
            return true;
        }
        return false;
    }
    public static boolean isTableButton(Object type) {
        if (type == null) {
            return false;
        }
        if (TABLE_BUTTON.type.equals(NumberUtils.toInt(type + ""))) {
            return true;
        }
        return false;
    }
    public static boolean isThead(Object type) {
        if (type == null) {
            return false;
        }
        if (THEAD.type.equals(NumberUtils.toInt(type + ""))) {
            return true;
        }
        return false;
    }
    public static boolean isRowButton(Object type) {
        if (type == null) {
            return false;
        }
        if (ROW_BUTTON.type.equals(NumberUtils.toInt(type + ""))) {
            return true;
        }
        return false;
    }



    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
