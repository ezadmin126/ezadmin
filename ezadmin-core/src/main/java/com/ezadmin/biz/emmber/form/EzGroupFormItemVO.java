package com.ezadmin.biz.emmber.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class EzGroupFormItemVO   {
    private List<Map<String,Object>> list=new ArrayList<>();
    private String name="";
    private String type="block";




    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Map<String, Object>> getList() {
        return list;
    }

    public void setList(List<Map<String, Object>> list) {
        this.list = list;
    }
}
