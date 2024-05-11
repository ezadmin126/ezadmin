package top.ezadmin.web;

import top.ezadmin.common.utils.StringUtils;
import top.ezadmin.common.utils.Utils;

import java.util.List;
import java.util.Map;

public class JsonArrayResult {
    private String code;
    private List<Map<String,Object>> data;

    public Map<String, Map<String, Object>> transToKeyMap(String key){
        return Utils.listToMap(data,key);
    }

    public boolean success(){
        return StringUtils.equals(code,"success")||StringUtils.equals(code,"0");
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Map<String, Object>> getData() {
        return data;
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }
}
