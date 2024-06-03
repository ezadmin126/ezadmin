package top.ezadmin.common.enums;

import top.ezadmin.common.utils.EzDateUtils;
import top.ezadmin.common.utils.StringUtils;

public enum DefaultParamEnum {
    EZ_TODAY_DATE,
    EZ_TODAY_DATE_NORMAL,
    EZ_PRE_30_DAY //前30天的00:00:00
    ,EZ_TODAY_END     //今天的23:59:59
    ,EZ_MONTH_START//当月第一天的  00:00:00
    ,EZ_MONTH_END//当月最后一天的23:59:59
    ,EZ_YEAR_START //当年 元旦节
    ;
    public static boolean isdefaultParam(String param){
        if(StringUtils.isBlank(param)||!Character.isLetter(param.charAt(0))){
            return false;
        }
        return true;
    }
    public static String getValue(String param){
        if(StringUtils.isBlank(param)||!Character.isLetter(param.charAt(0))){
            return param;
        }
        for(DefaultParamEnum e:values()){
            if(e.name().equalsIgnoreCase(param)){
                switch (e){
                    case EZ_TODAY_DATE:
                        return   EzDateUtils.todayDate();
                    case EZ_TODAY_DATE_NORMAL:
                        return EzDateUtils.todayDateNormal();
                    case EZ_PRE_30_DAY:
                        return EzDateUtils.preDay(30);
                    case EZ_TODAY_END:
                        return EzDateUtils.afterDay(0);
                    case EZ_MONTH_START:
                        return EzDateUtils.monthStart();
                    case EZ_MONTH_END:
                        return EzDateUtils.monthEnd();
                    case EZ_YEAR_START:
                        return EzDateUtils.yearStart();
                }
            }
        }
        return param;
    }
}
