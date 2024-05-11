package top.ezadmin.dao.model;

import java.util.ArrayList;
import java.util.List;

public class InitVO {

    private Info homeInfo=new Info();
    private Info logoInfo=new Info();
    private List<Info> menuInfo=new ArrayList<>();

    public static InitVO create(){
        return new InitVO() ;
    }
    public InitVO homeInfo(String title,String href){
        homeInfo.setTitle(title);
        homeInfo.setHref(href);
        return this;
    }
    public InitVO logoInfo(String title,String href,String image){
        logoInfo.setTitle(title);
        logoInfo.setHref(href);
        logoInfo.setImage(image);
        return this;
    }






    public Info getHomeInfo() {
        return homeInfo;
    }

    public void setHomeInfo(Info homeInfo) {
        this.homeInfo = homeInfo;
    }

    public Info getLogoInfo() {
        return logoInfo;
    }

    public void setLogoInfo(Info logoInfo) {
        this.logoInfo = logoInfo;
    }

    public List<Info> getMenuInfo() {
        return menuInfo;
    }

    public void setMenuInfo(List<Info> menuInfo) {
        this.menuInfo = menuInfo;
    }

    /**
     * {
     *   "homeInfo": {
     *     "title": "首页",
     *     "href": "page/welcome-1.html?t=1"
     *   },
     *   "logoInfo": {
     *     "title": "LAYUI MINI",
     *     "image": "images/logo.png",
     *     "href": ""
     *   },
     *   "menuInfo": [
     *     {
     *       "title": "常规管理",
     *       "icon": "fa fa-address-book",
     *       "href": "",
     *       "target": "_self",
     *       "child":[...]
     *     },
     *     {
     *       "title": "组件管理",
     *       "icon": "fa fa-lemon-o",
     *       "href": "",
     *       "target": "_self",
     *       "child":[...]
     *     },
     *     {
     *       "title": "其它管理",
     *       "icon": "fa fa-slideshare",
     *       "href": "",
     *       "target": "_self",
     *       "child":[...]
     *     }
     *   ]
     * }
     */


}
