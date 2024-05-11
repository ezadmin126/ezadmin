package top.ezadmin.dao.model;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemInitData {
    public ItemInitData items(List<Map<String, Object>> item){
        items=item;
        return this;
    }

    public List<Map<String, Object>> getItems() {
        return items;
    }

    List<Map<String, Object>> items=new ArrayList<>();

    public List<Map<String, Object>> getChildItems() {
        return childItems;
    }

    public ItemInitData childItems(List<Map<String, Object>> childItems) {
        this.childItems = childItems;
        return this;
    }

    List<Map<String, Object>> childItems=new ArrayList<>();

    public Map<String, Object> getOtherParams() {
        return otherParams;
    }

    public void setOtherParams(Map<String, Object> otherParams) {
        this.otherParams = otherParams;
    }

    Map<String,Object> otherParams=new HashMap<>();


}
