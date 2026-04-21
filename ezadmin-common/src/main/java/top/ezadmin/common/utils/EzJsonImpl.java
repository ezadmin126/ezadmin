package top.ezadmin.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import java.util.*;

public class EzJsonImpl implements EzJson {
    @Override
    public String toJSONString(Object obj) {
        return JSON.toJSONString(obj);
    }

    @Override
    public String toPrettyJSONString(Object obj) {
        return JSON.toJSONString(obj, true);
    }

    @Override
    public <T> List<T> parseArray(String values, Class<T> clazz) {
        return JSON.parseArray(values, clazz);
    }

    @Override
    public <T> T parseObject(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

    public Map<String, String> parseMap(String json) {
        return JSON.parseObject(json, new TypeReference<Map<String, String>>() {
        });
    }

    public Map<String, Object> parseObjectMap(String json) {
        return JSON.parseObject(json, new TypeReference<Map<String, Object>>() {
        });
    }

    public List<Map<String, Object>> parseListMapString(String json) {
        try {
            return JSON.parseObject(json, new TypeReference<List<Map<String, Object>>>() {
            });
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * 将 JSON 字符串深度转换为 Map<String, Object>，内层对象也转为 Map
     */
    @Override
    public Map<String, Object> deepParseObjectMap(String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        return toDeepMap(jsonObject);
    }

    /**
     * 递归将 JSONObject 转为 Map<String, Object>
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> toDeepMap(JSONObject obj) {
        if (obj == null) return Collections.emptyMap();
        Map<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : obj.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            result.put(key, convertValue(value));
        }
        return result;
    }

    /**
     * 递归处理值：JSONObject → Map, JSONArray → List, 其他保持原样
     */
    @SuppressWarnings("unchecked")
    private Object convertValue(Object value) {
        if (value instanceof JSONObject) {
            return toDeepMap((JSONObject) value);
        } else if (value instanceof JSONArray) {
            return convertArray((JSONArray) value);
        } else {
            return value; // String, Number, Boolean, null 等
        }
    }

    private List<Object> convertArray(JSONArray array) {
        List<Object> list = new ArrayList<>(array.size());
        for (Object item : array) {
            list.add(convertValue(item));
        }
        return list;
    }
}
