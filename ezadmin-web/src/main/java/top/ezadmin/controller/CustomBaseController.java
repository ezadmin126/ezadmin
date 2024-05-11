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
    protected String priceTextByType(  Integer priceType){
        switch (priceType){
            case 1://不含税运
                return "不含税运" ;
            case 2://含税不含运
                return "含税不含运" ;
            case 3://含税运
                return  "含税运";
            default:break;
        }
        return "";
    }

}
