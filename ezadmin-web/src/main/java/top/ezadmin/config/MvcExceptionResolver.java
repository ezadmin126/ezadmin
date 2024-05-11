package top.ezadmin.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import top.ezadmin.web.EzResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;



@Component
public class MvcExceptionResolver implements HandlerExceptionResolver {
    private Logger logger = LoggerFactory.getLogger(MvcExceptionResolver.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest request,
                                         HttpServletResponse response, Object handler, Exception ex) {

        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        try {
            String errorMsg = "";
            boolean isAjax = "1".equals(request.getParameter("isAjax"));


            logger.error("global error:", ex);


            if (isAjax) {
                EzResult.instance()
                        .msg("-1","server error ").printJSONUtils(response);
                return new ModelAndView();
            } else {


                //否则，  输出错误信息到自定义的500.jsp页面
                ModelAndView mv = new ModelAndView("/error/5xx");
                mv.addObject("errorMsg", errorMsg);
                return mv;
            }
        } catch (IOException e) {
            logger.error("", e);
        }
        return new ModelAndView();

    }



}