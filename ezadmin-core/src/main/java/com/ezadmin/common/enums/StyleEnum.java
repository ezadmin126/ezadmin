package com.ezadmin.common.enums;

import com.ezadmin.common.utils.IoUtils;

public enum StyleEnum {

    BOOTSTRAP("bootstrap","form","list","tbody"),
    LAYUI("layui","form","list","tbody");
    private String STYLE,FORM_FOLD,LIST_FOLD,TBODY_FOLD;
    private StyleEnum(String fold,String formFold,String listFold,String tbodyFold){
        this.STYLE=fold;
        this.FORM_FOLD=fold+ IoUtils.FILE_SEPARATOR+formFold;
        this.LIST_FOLD=fold+ IoUtils.FILE_SEPARATOR+listFold;
        this.TBODY_FOLD=fold+ IoUtils.FILE_SEPARATOR+tbodyFold;
    }
    public  String formFold(){
        return  FORM_FOLD;
    }
    public  String listFold(){
        return  LIST_FOLD;
    }
    public  String tbodyFold(){
        return  TBODY_FOLD;
    }
    public String styleName(){
return STYLE;
    }

    public static StyleEnum getByStyle(String style){
        for(StyleEnum styleEnum:values()){
           if(styleEnum.STYLE.equalsIgnoreCase(style)){
               return styleEnum;
           }
        }
        return BOOTSTRAP;
    }

}
