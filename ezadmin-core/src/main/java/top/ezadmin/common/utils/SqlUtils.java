package top.ezadmin.common.utils;

public class SqlUtils {
    public static String alias(String alias,String name){
        if(StringUtils.isBlank(alias)){
            return name;
        }
        if(StringUtils.contains(alias,".")){
            return alias;
        }
        return alias+"."+name;
    }
}
