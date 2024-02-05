package com.ezcloud.web.filters.ez;

import com.ezcloud.common.utils.StringUtils;
import com.ezcloud.common.utils.ThymeleafUtils;
import com.ezcloud.common.utils.Utils;
import org.thymeleaf.context.WebContext;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TraceLogFilter  extends Filter {

    String include="(/(ezadmin|ezcloud)/(list|form))/(trace-).*";

    public TraceLogFilter(){
        setInclude(include);
        super.initFilterBean();
    }


    public void doFilter(HttpServletRequest request , HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        boolean include=include(request.getRequestURI());

        try {
            if (include|| StringUtils.equals(request.getParameter("debug"),"1")) {
                Utils.initLog();
            }
             getNext().doFilter(request,response,filterChain) ;

        }finally {
            if(Utils.getLog()!=null) {
                WebContext context = new WebContext(request, response, request.getServletContext());
                try {
                    context.setVariable("list",Utils.getLog());
                    response.setHeader("Content-type", "text/html;charset=UTF-8");
                    ThymeleafUtils.process("layui/trace", context, response.getWriter());
                } catch (Exception e) {
                }
                Utils.clearLog();
            }
        }
    }
}
