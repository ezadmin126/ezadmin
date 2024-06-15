package top.ezadmin.controller;


 import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
 import top.ezadmin.blog.model.User;

 import javax.servlet.http.HttpServletRequest;

/**
 * : BaseController
 * @since: 2022/2/12 20:30
 */
public class CustomBaseController {
    protected static   Logger logger= LoggerFactory.getLogger(CustomBaseController.class);
    protected User getSessionUser(){
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        User user = (User) request.getSession().getAttribute("EZ_SESSION_USER_KEY");

        return user;
    }

}
