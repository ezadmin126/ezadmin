package top.ezadmin.common.enums;

import top.ezadmin.common.utils.StringUtils;
import top.ezadmin.common.utils.Utils;

public enum ItemDataSourceType {
    TEXT,JSON,KVSQL,HTTP,JOIN,KVSQLCACHEFOREVER,KVSQLCACHE,HTTPCACHE,QLEXPRESS,KVSQLCACHE5
    ,EZLIST,KVJSONRESULT,CASCADER,KVSQL2TREE,KVSQL2TREECACHE,DATAGROUP
    ;

    public static ItemDataSourceType get(String k){
        for (ItemDataSourceType v:values()){
            if(v.name().equalsIgnoreCase(k)){
                    return v;
            }
        }
        return null;
    }

    public static boolean isText(Object type){
        String tt= Utils.trimNull(type);
        return StringUtils.isBlank(tt)||TEXT.name().equalsIgnoreCase(tt) ;
    }
    public static boolean isJSON(Object type){
        return JSON.name().equalsIgnoreCase(Utils.trimNull(type));
    }
    public static boolean isKVSQL(Object type){
        return KVSQL.name().equalsIgnoreCase(Utils.trimNull(type));
    }
    public static boolean isKVSQLCache(Object type){
        return KVSQLCACHE.name().equalsIgnoreCase(Utils.trimNull(type));
    }
    public static boolean isHTTP(Object type){
        return HTTP.name().equalsIgnoreCase(Utils.trimNull(type));
    }
    public static boolean isHTTPCACHE(Object type){
        return HTTPCACHE.name().equalsIgnoreCase(Utils.trimNull(type));
    }
    public static boolean isJOIN(Object type){
        return JOIN.name().equalsIgnoreCase(Utils.trimNull(type));
    }
    public static boolean isQLEXPRESS(Object type){
        return QLEXPRESS.name().equalsIgnoreCase(Utils.trimNull(type));
    }

    public static boolean isEzList(String initType) {
        return EZLIST.name().equalsIgnoreCase(Utils.trimNull(initType));
    }
    public static boolean isCascader(String initType) {
        return CASCADER.name().equalsIgnoreCase(Utils.trimNull(initType));
    }
    public static boolean isKVJSONRESULT(String initType) {
        return KVJSONRESULT.name().equalsIgnoreCase(Utils.trimNull(initType));
    }
}
