package top.ezadmin.controller;


import top.ezadmin.EzBootstrap;
import top.ezadmin.common.EzAdminRuntimeException;
import top.ezadmin.common.constants.SessionConstants;
import top.ezadmin.common.enums.ExceptionCode;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.dao.FormDao;
import top.ezadmin.dao.ListDao;
import top.ezadmin.web.EzResult;
import top.ezadmin.web.RequestContext;

import java.util.HashMap;
import java.util.Map;

public class IndexController extends BaseController {



    public EzResult index(RequestContext requestContext )  {
//        Map<String, Object> config = bootstrap.getConfig();
//        if (config != null) {
//            for (Map.Entry<String, Object> entry : config.entrySet()) {
//                templateParam.put(entry.getKey(), entry.getValue());
//            }
//        }
        Map<String, Object> templateParam=new HashMap<>();
      //  templateParam.put("indexConfigJson", config);
        templateParam.put("logo", EzBootstrap.config().getLogoUrl());
        templateParam.put("systemName", EzBootstrap.config().getSystemName());
        templateParam.put("navUrl", EzBootstrap.config().getNavUrl());
        templateParam.put("appendJs", EzBootstrap.config().getAppendJs());
        templateParam.put("searchUrl", EzBootstrap.config().getSearchUrl());
        templateParam.put("indexUrl", EzBootstrap.config().getIndexUrl());
        templateParam.put("prefixUrl", EzBootstrap.config().getPrefixUrl());
        templateParam.put("signoutUrl", EzBootstrap.config().getSignoutUrl());
        templateParam.put("messageUrl", EzBootstrap.config().getMessageUrl());
        templateParam.put("chatUrl", EzBootstrap.config().getChatUrl());
        templateParam.put("clearUrl", EzBootstrap.config().getClearUrl());
        templateParam.put("appName", EzBootstrap.config().getAppName());
        String username = Utils.trimNull(requestContext.getSessionParams().get(SessionConstants.EZ_SESSION_USER_NAME_KEY));
        templateParam.put("EZ_SESSION_USER_NAME_KEY", username);
        Map<String, String> cookie = requestContext.getCookies();
        if (cookie != null) {
            cookie.forEach((k,v)->{
                if (k .equals("layui-theme-mode-prefer-dark")) {
                    try {
                        templateParam.put("darkTheme", v);
                    } catch (Exception e) {

                    }
                }
                    }
            );
        }
        try {
            return render(EzBootstrap.config().getAdminStyle() + "/index", templateParam);
        }catch (Exception e){
            throw new EzAdminRuntimeException(ExceptionCode.RENDER_ERROR,e);
        }
    }

    public EzResult clear(RequestContext requestContext, String method, String clearUrlCode) throws Exception {
        ListDao.getInstance().clear();
        FormDao.getInstance().clear();
        EzBootstrap.config().getEzCache().clear();
        EzBootstrap.config().getEzTemplate().clearCache();
        return EzResult.instance().msg("0", "清理缓存成功");
    }

    public EzResult clearCache(RequestContext requestContext, String method, String clearCacheUrlCode) throws Exception {
        EzBootstrap.config().getEzCache().clear();
        return EzResult.instance().msg("0", "清理缓存成功");
    }
 


    // public void dark(HttpServletRequest request, HttpServletResponse response) throws IOException {
    //     if (StringUtils.equals("add", request.getParameter("type"))) {
    //         Cookie cookie = new Cookie("layui-theme-mode-prefer-dark", "dark");
    //         cookie.setDomain(request.getServerName());
    //         cookie.setPath("/");
    //         cookie.setMaxAge(31536000);
    //         response.addCookie(cookie);
    //     } else {
    //         Cookie cookie = new Cookie("layui-theme-mode-prefer-dark", "1");
    //         cookie.setDomain(request.getServerName());
    //         cookie.setPath("/");
    //         cookie.setMaxAge(0);
    //         response.addCookie(cookie);
    //     }
    //     EzResult.instance().msg("0", "ok").data("vo").printJSONUtils(response);
    // }


    // public String dev(HttpServletRequest request, HttpServletResponse response) throws IOException {
    //     return "redirect:/topezadmin/list/list-listall";
    // }
}
