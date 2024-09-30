package top.ezadmin.controller;


 import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
 import top.ezadmin.blog.model.User;
 import top.ezadmin.common.constants.SessionConstants;

 import javax.servlet.http.HttpServletRequest;

/**
 * : BaseController
 */
public class CustomBaseController {
    protected static   Logger logger= LoggerFactory.getLogger(CustomBaseController.class);
    protected User getSessionUser(){
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        return (User) request.getSession().getAttribute(SessionConstants.EZ_SESSION_USER_KEY);
    }

}
