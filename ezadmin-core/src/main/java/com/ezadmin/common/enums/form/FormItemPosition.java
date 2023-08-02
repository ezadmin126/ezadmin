package com.ezadmin.common.enums.form;

public enum FormItemPosition {
    BLOCK,INLINE_PARENT,INLINE_CHILD;
    public static boolean inlineParent(String po){
        if(INLINE_PARENT.name().equalsIgnoreCase(po)){
            return true;
        }
        return false;
    }
    public static boolean inlineChild(String po){
        if(INLINE_CHILD.name().equalsIgnoreCase(po)){
            return true;
        }
        return false;
    }
}
