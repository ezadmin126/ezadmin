package top.ezadmin.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.List;
import java.util.Map;

public class EzJsonImpl implements EzJson {
    @Override
    public String toJSONString(Object obj) {
        return JSON.toJSONString(obj);
    }

    @Override
    public <T> List<T> parseArray(String values, Class<T> clazz) {
        return JSON.parseArray(values, clazz);
    }

    @Override
    public <T> T parseObject(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

    public   Map<String, String> parseMap(String json) {
        return JSON.parseObject(json, new TypeReference<Map<String, String>>() {
        });
    }

    public   Map<String, Object> parseObjectMap(String json) {
        return JSON.parseObject(json, new TypeReference<Map<String, Object>>() {
        });
    }

    public   List<Map<String, Object>> parseListMapString(String json) {
        try {
            return JSON.parseObject(json, new TypeReference<List<Map<String, Object>>>() {
            });
        } catch (Exception e) {
            throw e;
        }
    }
}
