package com.ezcloud.web.filters.ez;

import com.ezcloud.common.utils.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class NotFoundFilter extends Filter {
    @Override
    public void doFilter(HttpServletRequest request , HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
         if(StringUtils.startsWith(request.getRequestURL().toString(),"/ezadmin")){
             filterChain.doFilter(request,response);
         }else{
             System.out.println(request.getRequestURL().toString());
             response.getWriter().println("404");
             response.getWriter().flush();
         }
    }
}
