package top.ezadmin.controller;


import top.ezadmin.EzClientBootstrap;
import top.ezadmin.common.annotation.EzMapping;
import top.ezadmin.common.constants.SessionConstants;
import top.ezadmin.common.utils.StringUtils;
import top.ezadmin.common.utils.ThymeleafUtils;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.dao.FormDao;
import top.ezadmin.dao.ListDao;
import top.ezadmin.dao.model.Info;
import top.ezadmin.dao.model.InitVO;
import top.ezadmin.web.EzResult;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@EzMapping("/topezadmin/")
public class IndexController extends BaseController {

    EzClientBootstrap bootstrap= EzClientBootstrap.instance();
    @EzMapping("index.html")
    public String index(HttpServletRequest request, HttpServletResponse response)   {
        Map<String,Object> config=bootstrap.getConfig();
        if(config!=null){
            for(Map.Entry<String,Object> entry:config.entrySet()){
                request.setAttribute(entry.getKey() ,entry.getValue());
            }
        }
        request.setAttribute("indexConfigJson",config);
        request.setAttribute("logo",bootstrap.getLogoUrl());
        request.setAttribute("systemName",bootstrap.getSystemName());
        request.setAttribute("navUrl",bootstrap.getNavUrl());
        request.setAttribute("appendJs",bootstrap.getAppendJs());
        request.setAttribute("searchUrl",bootstrap.getSearchUrl());
        request.setAttribute("indexUrl",bootstrap.getIndexUrl());
        request.setAttribute("signoutUrl",bootstrap.getSignoutUrl());
        request.setAttribute("messageUrl",bootstrap.getMessageUrl());
        request.setAttribute("chatUrl",bootstrap.getChatUrl());
        request.setAttribute("clearUrl",bootstrap.getClearUrl());
        request.setAttribute("holiday",bootstrap.getHoliday());
        request.setAttribute("appName",bootstrap.getAppName());
        request.setAttribute("bootstrap",bootstrap);
        String username = Utils.trimNull(request.getSession().getAttribute(SessionConstants.EZ_SESSION_USER_NAME_KEY));
        request.setAttribute("EZ_SESSION_USER_NAME_KEY",username);
        Cookie[] cookie=request.getCookies();
        if(cookie!=null){
            for (int i = 0; i < cookie.length; i++) {
                if(cookie[i].getName().equals("layui-theme-mode-prefer-dark")){
                    try {
                        request.setAttribute("darkTheme",cookie[i].getValue());
                    } catch (Exception e) {

                    }
                }
            }
        }
        return bootstrap.getAdminStyle()+"/index";
    }

    @EzMapping("clear.html")
    public EzResult clear(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ListDao.getInstance().clear();
        FormDao.getInstance().clear();
        EzClientBootstrap.instance().getCache().clear();
        ThymeleafUtils.clear();
        return EzResult.instance().msg("0","清理缓存成功");
    }
    @EzMapping("clearCache.html")
    public EzResult clearCache(HttpServletRequest request, HttpServletResponse response) throws Exception {
        EzClientBootstrap.instance().getCache().clear();
        return EzResult.instance().msg("0","清理缓存成功");
    }

    @EzMapping("navs.html")
    public void navs(HttpServletRequest request, HttpServletResponse response) throws Exception {
        InitVO vo = new InitVO();
        List<Info> toproot = new ArrayList<>();
        List<Info> root = new ArrayList<>();
        Info listW = new Info();
        listW.setTitle("接入指南");
        listW.setId("1");
        listW.setPid("0");
        listW.setHref("/welcome.html");
        root.add(listW);

        Info listN = new Info();
        listN.setTitle("列表管理");
        listN.setId("1");
        listN.setPid("0");
        listN.setHref("/topezadmin/list/list-listall?perPageInt=20");
        root.add(listN);

        Info listF = new Info();
        listF.setTitle("表单管理");
        listF.setId("2");
        listF.setPid("0");
        listF.setHref("/topezadmin/list/list-formall?perPageInt=20");
        root.add(listF);
//        Info list2 = new Info();
//        list2.setTitle("表单管理");
//        list2.setId("3");
//        list2.setPid("0");
//        list2.setHref("/topezadmin/listEdit/list-pluginmanage?perPageInt=20");
//        root.add(list2);

        Info TOP = new Info();
        TOP.setTitle("系统管理");
        TOP.setChild(root);
        toproot.add(TOP);
        vo.homeInfo(EzClientBootstrap.instance().getSystemName(), "").
                logoInfo("", "/topezadmin/index.html", EzClientBootstrap.instance().getConfig().get("logo") + "")
                .setMenuInfo(toproot);
        EzResult.instance().msg("0", "ok")
                .data(vo).printJSONUtils(response);
    }


    @EzMapping("dark.html")
    public void dark(HttpServletRequest request,  HttpServletResponse response) throws IOException {
        if(StringUtils.equals("add",request.getParameter("type"))){
            Cookie cookie=new Cookie("layui-theme-mode-prefer-dark", "dark");
            cookie.setDomain(request.getServerName());
            cookie.setPath("/");
            cookie.setMaxAge(31536000);
            response.addCookie(cookie);
        }else{
            Cookie cookie=new Cookie("layui-theme-mode-prefer-dark", "1");
            cookie.setDomain(request.getServerName());
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
        EzResult.instance().msg("0", "ok").data("vo").printJSONUtils(response);
    }


    @EzMapping("dev.html")
    public String dev(HttpServletRequest request,  HttpServletResponse response)throws IOException  {
        return "redirect:/topezadmin/list/list-listall";
    }
}
