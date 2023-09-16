package com.ezadmin.biz.model;

import com.ezadmin.common.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EzModel {
    private Map<String,Object> config=new HashMap<>(0);
    private Map<String,Object> param=new HashMap<>(0);
    private Map<String,String> session=new HashMap<>(0);

    //联合搜索 只需要name,alias,  就行了 oper默认使用like
    private List<EzModel> children=new ArrayList<>();

    public EzModel config(String key,Object value){
        config.put(key,value);
        return this;
    }
    public Map<String,Object> config(){
        return config;
    }
    public void setConfig(Map<String,Object> c){
config=c;
    }
    public String getConfig(String key){
        return Utils.trimNull(config.get(key));
    }
    public String getParam(String key){
        return Utils.trimNull(param.get(key));
    }
    public EzModel param(String key,Object value){
        param.put(key,value);
        return this;
    }
    public Map<String,Object> param(){
        return param;
    }
    public void setpParam(Map<String,Object> request){
          param=request;
    }


    public List<EzModel> getChildren() {
        return children;
    }

    public void setChildren(List<EzModel> children) {
        this.children = children;
    }

    public Map<String, String> getSession() {
        return session;
    }

    public void setSession(Map<String, String> session) {
        this.session = session;
    }
}
