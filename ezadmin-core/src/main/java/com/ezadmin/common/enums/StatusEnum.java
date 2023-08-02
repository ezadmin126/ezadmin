package com.ezadmin.common.enums;

import com.ezadmin.common.utils.NumberUtils;

public enum StatusEnum {
    ACTIVE(1),DISABLE(0);
    int status=1;
    private StatusEnum(int i){
        status=i;
    }
    public static boolean isActive(Object i){
       return i==null?false: NumberUtils.toInt(i+"")==1;
    }

}
