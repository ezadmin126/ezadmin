package top.ezadmin.web.filters.ez;

import top.ezadmin.common.utils.StringUtils;
import top.ezadmin.common.utils.ThymeleafUtils;
import top.ezadmin.common.utils.Utils;
import org.thymeleaf.context.WebContext;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TraceLogFilter  extends Filter {

    String include="(/(ezadmin|topezadmin)/(list|form|listEdit|formEdit))/(trace-).*";

    public TraceLogFilter(){
        setInclude(include);
        super.initFilterBean();
    }


    public void doFilter(HttpServletRequest request , HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        boolean include=include(request.getRequestURI());
        long start=System.currentTimeMillis();
        try {
            if (include||
                    StringUtils.equals(request.getParameter("trace"),"1")
                    || StringUtils.equals(request.getParameter("trace"),"1")) {
                Utils.initLog();
            }
             getNext().doFilter(request,response,filterChain) ;

        }finally {
            if (include||
                    StringUtils.equals(request.getParameter("trace"),"1")
                    || StringUtils.equals(request.getParameter("trace"),"1")) {
                Utils.addLog("cost:"+(System.currentTimeMillis()-start));
            }
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
