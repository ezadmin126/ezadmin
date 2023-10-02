package com.ezadmin.controller;


import com.ezadmin.common.annotation.EzMapping;
 import com.ezadmin.common.utils.ThymeleafUtils;
import com.ezadmin.dao.FormDao;
import com.ezadmin.dao.ListDao;
import com.ezadmin.web.EzResult;
import com.ezadmin.EzBootstrap;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@EzMapping("/ezadmin/")
public class IndexController extends BaseController {

     EzBootstrap bootstrap=EzBootstrap.instance();
    @EzMapping("index.html")
    public String index(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        Map<String,Object> config=bootstrap.getConfig();
        if(config!=null){
            for(Map.Entry<String,Object> entry:config.entrySet()){
                request.setAttribute(entry.getKey() ,entry.getValue());
            }
        }
        request.setAttribute("logo",bootstrap.getLogoUrl());
        request.setAttribute("systemName",bootstrap.getSystemName());
        request.setAttribute("navUrl",bootstrap.getNavUrl());
        request.setAttribute("appendJs",bootstrap.getAppendJs());
        request.setAttribute("searchUrl",bootstrap.getSearchUrl());
        request.setAttribute("indexUrl",bootstrap.getIndexUrl());
        request.setAttribute("signoutUrl",bootstrap.getSignoutUrl());
        request.setAttribute("messageUrl",bootstrap.getMessageUrl());
        request.setAttribute("chatUrl",bootstrap.getChatUrl());
        request.setAttribute("holiday",bootstrap.getHoliday());
        request.setAttribute("appName",bootstrap.getAppName());


        request.setAttribute("bootstrap",bootstrap);
        //if(StringUtils.contains(bootstrap.getIndexUrl(),"layui")){
            return bootstrap.getAdminStyle()+"/index/index";
//        }
//        return "layui/index/index";
    }
    @EzMapping("welcome.html")
    public String welcome(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        return "layui/pages/welcome";
    }
    @EzMapping("demo.html")
    public String dev(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        HashMap data=new HashMap<>();
        data.put("page",new HashMap<>());
        request.setAttribute("data",data);
        return "layui/pages/demo";
    }
    @EzMapping("clear.html")
    public EzResult clear(HttpServletRequest request, HttpServletResponse response) throws Exception {

        ListDao.getInstance().clear();
        FormDao.getInstance().clear();
        EzBootstrap.instance().getCache().clear();
            ThymeleafUtils.clear();

        return EzResult.instance().msg("0","OK");
    }
    @EzMapping("search.html")
    public EzResult search(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        EzBootstrap.instance().getCache().clear();
        return EzResult.instance().msg("0","OK");
    }
    @EzMapping("logout.html")
    public String logout(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        Cookie[] cookies=request.getCookies();
        for (int i = 0; i < cookies.length; i++) {
            cookies[i].setMaxAge(0);
            response.addCookie(cookies[i]);
        }

        Enumeration<String> stringEnumeration= request.getSession().getAttributeNames();
        while(stringEnumeration.hasMoreElements()){
            String key=stringEnumeration.nextElement();
            request.getSession().removeAttribute(key);
        }
        return   "index/logout";
    }
}
