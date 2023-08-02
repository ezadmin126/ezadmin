package com.ezadmin.biz.emmber;

import java.util.ArrayList;
import java.util.List;

/**
 *
 **/
public class ListRow {
    private List<String> tds=new ArrayList<>();
    private Integer id;
    private Integer pid;

    public String getTreeClass(){
        String c="";
        if(id!=null){
            c=c+"   treegrid-"+id;
        }
        if(pid!=null&&pid!=0){
            c=c+" treegrid-parent-"+pid;
        }
        return c;
    }

    public List<String> getTds() {
        return tds;
    }

    public void setTds(List<String> tds) {
        this.tds = tds;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public void addTd(String html) {
        tds.add(html);
    }

    public void addAllTd(List<String> tds1) {
        tds.addAll(tds1);
    }
}
