package top.ezadmin.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.UrlPathHelper;
import top.ezadmin.blog.constants.Nologin;
import top.ezadmin.blog.model.User;
import top.ezadmin.common.utils.JSONUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SuperdoInterceptor implements HandlerInterceptor {

    private final Logger LOG = LoggerFactory.getLogger(SuperdoInterceptor.class);

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {

        if(handler instanceof HandlerMethod){
            HandlerMethod handlerMethod=(HandlerMethod)handler;
            Nologin c= handlerMethod.getBeanType().getClass().getAnnotation(Nologin.class);
            Nologin m= handlerMethod.getMethodAnnotation(Nologin.class);
            if(c!=null||m!=null){
                return true;
            }
        }
        User user = (User) request.getSession().getAttribute("EZ_SESSION_USER_KEY");
//        UrlPathHelper helper = new UrlPathHelper();
//        String realUrl = helper.getRequestUri(request);
//        if(user.getRoleNames().contains("管理员")){
//            return  true;
//        }
//        LOG.info(JSONUtils.toJSONString(user.getResourceIds()));
//        for (int i = 0; i < user.getResourceIds().size(); i++) {
//            if(user.getResourceIds().get(i).startsWith(realUrl)){
//                return true;
//            }
//        }
        if(user!=null){
            return true;
        }
        response.sendRedirect("/error/403.html?inter=1");
        return false;
    }
}
