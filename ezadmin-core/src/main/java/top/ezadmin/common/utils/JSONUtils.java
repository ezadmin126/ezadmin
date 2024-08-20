package top.ezadmin.common.utils;



import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;



public class JSONUtils {
    static Gson DEFAULT = new Gson();
   static Logger logger= LoggerFactory.getLogger(JSONUtils.class);
    public static String toJSONString(Object obj) {
        return DEFAULT.toJson(obj);
    }


    public static <T> T parseObject(String json, Class<T> clazz) {

        return DEFAULT.fromJson(json, clazz);
    }

    public static Map<String, String> parseMap(String json ) {
        Type mapType = new TypeToken<Map<String, String>>(){}.getType();
        return DEFAULT.fromJson(json, mapType);
    }
    public static Map<String, Object> parseObjectMap(String json ) {
        return DEFAULT.fromJson(json, new TypeToken<Map<String, Object>>() {}.getType());
    }

    public static  List<Map<String, Object>> parseListMapString(String json) {
        try {
            return DEFAULT.fromJson(json, new TypeToken<List<Map<String, Object>>>() {
            }.getType());
        }catch (Exception e){
            logger.error(json,e);
            throw e;
        }
    }


}
