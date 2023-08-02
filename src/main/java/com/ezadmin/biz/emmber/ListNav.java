package com.ezadmin.biz.emmber;

public class ListNav {

    private String text;

    private String url;
    private String express;//判断是否是选中状态
    private boolean select;

    public String getText() {


        return text;
    }

    /**
     *
     * @param text 标题
     */
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
