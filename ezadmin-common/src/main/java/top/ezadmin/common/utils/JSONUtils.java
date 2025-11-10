package top.ezadmin.common.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ezadmin.EzBootstrap;

import java.util.List;
import java.util.Map;


public class JSONUtils {
    static Logger logger = LoggerFactory.getLogger(JSONUtils.class);

    public static String toJSONString(Object obj) {
        return EzBootstrap.config().getEzJson().toJSONString(obj);
    }

    public static <T> List<T> parseArray(String values, Class<T> clazz) {
        return EzBootstrap.config().getEzJson().parseArray(values, clazz);
    }

    public static <T> T parseObject(String json, Class<T> clazz) {
        return EzBootstrap.config().getEzJson().parseObject(json, clazz);
    }

    public static Map<String, String> parseMap(String json) {
        return EzBootstrap.config().getEzJson().parseMap(json );
    }

    public static Map<String, Object> parseObjectMap(String json) {
        return EzBootstrap.config().getEzJson().parseObjectMap(json);
    }

    public static List<Map<String, Object>> parseListMapString(String json) {
        try {
            return EzBootstrap.config().getEzJson().parseListMapString(json);
        } catch (Exception e) {
            logger.error(json, e);
            throw e;
        }
    }




}
