package com.ezadmin.common.enums.template;

public enum TemplateEnum {

    PAGE("page","分页"),
    ROWBUTTON("button-single","行按钮"),
    FIRSTCOL("firstcol-numbers","FIRSTCOL"),
    TEXTSEARCH("input-text","TEXTSEARCH"),
    ;
    String code,  name;
    private TemplateEnum(String code,String name){
        this.code=code;
        this.name=name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
