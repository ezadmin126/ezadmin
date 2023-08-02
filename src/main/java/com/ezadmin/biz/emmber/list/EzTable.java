package com.ezadmin.biz.emmber.list;

import com.ezadmin.biz.model.EzSearchModel;
import com.ezadmin.common.utils.Page;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public  class EzTable {

    private Map<String, Object> requestParamMap=new HashMap<>();
    private Map<String, String> sessionParamMap=new HashMap<>();
    private Map<String, String> core=new HashMap<>();
    private List<EzSearchModel> search=new ArrayList<>();
    private List<Map<String,String>> nav=new ArrayList<>();
    private List<Map<String,String>> col=new ArrayList<>();
    private List<Map<String,String>> tablebtn=new ArrayList<>();
    private List<Map<String,String>> rowbtn=new ArrayList<>();




    private Page pagination;
    private DataSource dataSource;

    public EzTable request(Map<String, Object> request){
        requestParamMap=request;
        return this;
    }
    public EzTable session(Map<String, String> session){
        sessionParamMap=session;
        return this;
    }
    public EzTable db(DataSource db){
        dataSource=db;
        return this;
    }
    public EzTable page(Page page){
        pagination=page;
        return this;
    }

    public EzTable core(Map<String, String> core){
        this.core=core;
        return this;
    }

    public EzTable search(List<EzSearchModel> search) {
        this.search = search;
        return this;
    }

    public EzTable nav(List<Map<String, String>> nav) {
        this.nav = nav;
        return this;
    }

    public EzTable col(List<Map<String, String>> col) {
        this.col = col;
        return this;
    }

    public EzTable tablebtn(List<Map<String, String>> tablebtn) {
        this.tablebtn = tablebtn; return this;
    }

    public EzTable rowbtn(List<Map<String, String>> rowbtn) {
        this.rowbtn = rowbtn; return this;
    }




}
