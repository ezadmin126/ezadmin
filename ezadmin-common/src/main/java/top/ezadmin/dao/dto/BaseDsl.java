package top.ezadmin.dao.dto;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import top.ezadmin.common.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseDsl {

    /**
     * 唯一标识
     */
    private String id;

    /**
     * 名称
     */
    private String name;

    /**
     * 数据源名称
     */
    private String dataSource;
    /**
     * 表格头部追加内容
     */
    private Object appendHead;

    /**
     * 表格底部追加内容
     */
    private Object appendFoot;

    /**
     * 将 DSL 对象转换为 Map
     * 主要用于传递给模板引擎或需要 Map 格式的场景
     * @return Map 格式的配置
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> toMap() {
        String json = JSON.toJSONString(this);
        return JSON.parseObject(json, Map.class);
    }


    /**
     * 递归转换 Map 中的 JSONObject 为 HashMap
     * 解决 Fastjson 反序列化嵌套 Map 时自动转换为 JSONObject 的问题
     *
     * @param obj 待转换的对象
     * @return 转换后的对象
     */
    @SuppressWarnings("unchecked")
    protected static Object convertToHashMap(Object obj) {
        if (obj == null) {
            return null;
        }
        // 如果是 JSONObject，转换为 HashMap
        if (obj instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) obj;
            HashMap<String, Object> map = new HashMap<String, Object>();
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                map.put(entry.getKey(), convertToHashMap(entry.getValue()));
            }
            return map;
        }
        // 如果是 Map，递归转换其值
        if (obj instanceof Map) {
            Map<String, Object> sourceMap = (Map<String, Object>) obj;
            HashMap<String, Object> map = new HashMap<String, Object>();
            for (Map.Entry<String, Object> entry : sourceMap.entrySet()) {
                map.put(entry.getKey(), convertToHashMap(entry.getValue()));
            }
            return map;
        }

        // 如果是 List，递归转换其元素
        if (obj instanceof List) {
            List<?> sourceList = (List<?>) obj;
            List<Object> list = new ArrayList<Object>();
            for (Object item : sourceList) {
                list.add(convertToHashMap(item));
            }
            return list;
        }

        // 其他类型直接返回
        return obj;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }


    public String getAppendHead() {
        //如果是数组，则空格合并
        if (appendHead instanceof List) {
            return StringUtils.join((List<String>) appendHead, " ");
        }
        return (String) appendHead;
    }

    public void setAppendHead(String appendHead) {
        this.appendHead = appendHead;
    }

    public String getAppendFoot() {
        //如果是数组，则空格合并
        if (appendFoot instanceof List) {
            return StringUtils.join((List<String>) appendFoot, " ");
        }
        return (String) appendFoot;
    }

    public void setAppendFoot(String appendFoot) {
        this.appendFoot = appendFoot;
    }
}
