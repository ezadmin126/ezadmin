package com.ezcloud.controller;


import com.ezcloud.EzClientBootstrap;
import com.ezcloud.common.annotation.EzMapping;
import com.ezcloud.common.utils.ThymeleafUtils;
import com.ezcloud.dao.FormDao;
import com.ezcloud.dao.ListDao;
import com.ezcloud.web.EzResult;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@EzMapping("/ezcloud/")
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
        return bootstrap.getAdminStyle()+"/index";
    }

    @EzMapping("clear.html")
    public EzResult clear(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ListDao.getInstance().clear();
        FormDao.getInstance().clear();
        EzClientBootstrap.instance().getCache().clear();
        ThymeleafUtils.clear();
        return EzResult.instance().msg("0","OK");
    }
}
