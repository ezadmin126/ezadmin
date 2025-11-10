package top.ezadmin.common.utils;

import java.util.List;
import java.util.Map;

public interface EzJson {

      String toJSONString(Object obj) ;

    <T> List<T> parseArray(String values, Class<T> clazz);

    <T> T parseObject(String json, Class<T> clazz);

    Map<String, String> parseMap(String json);

    Map<String, Object> parseObjectMap(String json);

    List<Map<String, Object>> parseListMapString(String json);
}
