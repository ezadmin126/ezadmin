package com.ezadmin.web.filters.ez;

import com.ezadmin.common.utils.StringUtils;
import com.ezadmin.common.utils.Utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TraceLogFilter  extends Filter {

    String include="(/ezadmin/(list|form))/(trace-).*";

    public TraceLogFilter(){
        setInclude(include);
        super.initFilterBean();
    }


    public void doFilter(HttpServletRequest request , HttpServletResponse response) throws IOException {
        boolean include=include(request.getRequestURI());

        try {
            if (include|| StringUtils.equals(request.getParameter("debug"),"1")) {
                Utils.initLog();
            }
             getNext().doFilter(request,response) ;

        }finally {
            if(Utils.getLog()!=null) {
                response.getWriter().write("<DIV class='container-fluid'> ");
                for (int i = 0; i < Utils.getLog().size(); i++) {
                    try {
                        response.setCharacterEncoding("utf-8");
                        response.getWriter().write("<pre> " + Utils.getLog().get(i)+"</pre>");
                        response.getWriter().write("<br>=============================================================================<br>");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                response.getWriter().write("</DIV> ");
                Utils.clearLog();
            }
        }
    }
}
