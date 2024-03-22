package top.ezadmin.blog.controller;


 import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
 import top.ezadmin.blog.model.User;

 import javax.servlet.http.HttpServletRequest;

/**
 * @title: BaseController
 * @Author Hank.he
 * @Date: 2022/2/12 20:30
 */
public class BaseController {
    protected static   Logger logger= LoggerFactory.getLogger(BaseController.class);
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
