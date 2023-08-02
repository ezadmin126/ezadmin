package com.ezadmin.biz.model;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

public class ListNav {
    private String text;
    private String url;
    private String express;
    private boolean select;

    public static void main(String[] args) {
        List<ListNav> list=new ArrayList<ListNav>();
        ListNav nav1=new ListNav();
        nav1.setSelect(true);
        nav1.setExpress("return USER_NAME='关羽';");
        nav1.setText("关羽");
        nav1.setUrl("www.baidu.com");
        list.add(nav1);
        System.out.println(JSON.toJSONString(list));
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getExpress() {
        return express;
    }

    public void setExpress(String express) {
        this.express = express;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }
}
