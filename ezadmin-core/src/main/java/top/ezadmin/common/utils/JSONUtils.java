package top.ezadmin.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;
import java.util.Map;



public class JSONUtils {
   static  Logger logger= LoggerFactory.getLogger(JSONUtils.class);
    public static String toJSONString(Object obj) {
        return JSON.toJSONString(obj);
    }

    public static <T> List<T> parseArray(String values, Class<T> clazz) {
        return JSON.parseArray(values, clazz);
    }

    public static <T> T parseObject(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

    public static Map<String, String> parseMap(String json ) {
        return JSON.parseObject(json, new TypeReference<Map<String, String>>() {});
    }
    public static Map<String, Object> parseObjectMap(String json ) {
        return JSON.parseObject(json, new TypeReference<Map<String, Object>>() {});
    }

    public static  List<Map<String, Object>> parseListMapString(String json) {
        try {
            return JSON.parseObject(json, new TypeReference<List<Map<String, Object>>>() {
            });
        }catch (Exception e){
            logger.error(json,e);
            throw e;
        }
    }


}
