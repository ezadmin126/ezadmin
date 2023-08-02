package com.ezadmin.web;


import com.ezadmin.EzBootstrap;
import com.ezadmin.common.utils.JSONUtils;
import com.ezadmin.common.utils.StringUtils;
import com.ezadmin.common.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Component
public class EzAdminBootFilter extends OncePerRequestFilter {
    private static final Logger LOGGE = LoggerFactory.getLogger(EzAdminBootFilter.class);

  @Autowired
 EzBootstrap ezBootstrap;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            MDC.put("tid",System.nanoTime()+"");
            ezBootstrap.doFilter(request, response);
        } catch (Exception e) {
            LOGGE.error("", e);
            response.getWriter().println(e.getMessage());
        } finally {
            MDC.clear();
            Utils.clearLog();
        }
    }


}
