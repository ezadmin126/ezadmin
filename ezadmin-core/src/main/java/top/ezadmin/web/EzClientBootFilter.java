//package top.ezadmin.web;
//
//
//import top.ezadmin.EzClientBootstrap;
//import top.ezadmin.common.utils.Utils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.slf4j.MDC;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.servlet.*;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//
//
//public class EzClientBootFilter extends OncePerRequestFilter {
//    private static final Logger LOGGE = LoggerFactory.getLogger(EzClientBootFilter.class);
//
////  @Autowired
////  EzClientBootstrap ezBootstrap;
//
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
////        try {
////            MDC.put("tid",System.nanoTime()+"");
////            ezBootstrap.doFilter(request, response,filterChain);
////        } catch (Exception e) {
////            LOGGE.error("", e);
////            response.getWriter().println(e.getMessage());
////        } finally {
////            MDC.clear();
////            Utils.clearLog();
////        }
//    }
//
//
//}
