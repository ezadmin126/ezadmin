package top.ezadmin.web.filters;


import top.ezadmin.common.utils.JSONUtils;
import top.ezadmin.common.utils.StringUtils;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.EzClientBootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class EzClientServletFilter implements Filter {
    private static final Logger LOGGE = LoggerFactory.getLogger(EzClientServletFilter.class);


    private Map<String, DataSource> appDatasource = new HashMap<String, DataSource>();


    EzClientBootstrap ezBootstrap;

    public void setEzBootstrap(EzClientBootstrap bootstrap) {
        if(ezBootstrap==null){
            ezBootstrap = bootstrap;
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
       // LOGGE.info("start init EzAdminFilter" + filterConfig.getInitParameterNames());
        if (ezBootstrap == null) {
            ezBootstrap = EzClientBootstrap.instance();
            String configJson=filterConfig.getInitParameter("configJson");
            Map<String, Object> configMap=JSONUtils.parseObjectMap(configJson);
            ezBootstrap.setConfig(configMap);

            ezBootstrap.setAppName(filterConfig.getInitParameter("appName"));
            ezBootstrap.setSqlCache(StringUtils.toBoolean(filterConfig.getInitParameter("cacheFlag")));
            if(StringUtils.isNotBlank(filterConfig.getInitParameter("uploadUrl"))){
                ezBootstrap.setUploadUrl(filterConfig.getInitParameter("uploadUrl"));
            }
            if(StringUtils.isNotBlank(filterConfig.getInitParameter("downloadUrl"))){
                ezBootstrap.setDownloadUrl(filterConfig.getInitParameter("downloadUrl"));
            }
            ezBootstrap.setLogType(filterConfig.getInitParameter("logType"));
            ezBootstrap.setRegionUrl(filterConfig.getInitParameter("regionUrl"));
            ezBootstrap.setCategoryUrl(filterConfig.getInitParameter("categoryUrl"));
            ezBootstrap.setOrgUrl(filterConfig.getInitParameter("orgUrl"));
            ezBootstrap.setSystemName(filterConfig.getInitParameter("systemName"));
            ezBootstrap.setNavUrl(filterConfig.getInitParameter("navUrl"));
            ezBootstrap.setLogoUrl(filterConfig.getInitParameter("logoUrl"));
            ezBootstrap.setSearchUrl(filterConfig.getInitParameter("searchUrl"));
            ezBootstrap.setAppendJs(filterConfig.getInitParameter("appendJs"));
            ezBootstrap.setIndexUrl(filterConfig.getInitParameter("indexUrl"));
            ezBootstrap.setSignoutUrl(filterConfig.getInitParameter("signoutUrl"));
            ezBootstrap.setMessageUrl(filterConfig.getInitParameter("messageUrl"));
            ezBootstrap.setChatUrl(filterConfig.getInitParameter("chatUrl"));
            ezBootstrap.setAdminStyle(filterConfig.getInitParameter("adminStyle"));
            for (Map.Entry<String, DataSource> entry:appDatasource.entrySet()){
                ezBootstrap.addBizDataSource(entry.getKey(), entry.getValue());
            }
            try {
                ezBootstrap.init();
            } catch (Exception throwables) {
                throw new ServletException(throwables);
            }
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        try {
            if(StringUtils.isBlank(MDC.get("traceId"))){
                MDC.put("traceId",System.nanoTime()+"");
            }
            ezBootstrap.doFilter(httpServletRequest, httpServletResponse,filterChain);
        } catch (Exception e) {
            LOGGE.error("", e);
            httpServletResponse.getWriter().println(error500+MDC.get("traceId")+end500);
        } finally {
            MDC.clear();
            Utils.clearLog();
        }
    }

    @Override
    public void destroy() {

    }
    public static String error500="<!DOCTYPE html>\n" +
            "<html lang=\"zh-CN\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
            "    <title>500 - 服务器错误</title>\n" +
            "    <style>\n" +
            "        body {\n" +
            "            font-family: Arial, sans-serif;\n" +
            "            background-color: #f8f9fa;\n" +
            "            color: #343a40;\n" +
            "            display: flex;\n" +
            "            justify-content: center;\n" +
            "            align-items: center;\n" +
            "            height: 100vh;\n" +
            "            margin: 0;\n" +
            "        }\n" +
            "        .container {\n" +
            "            text-align: center;\n" +
            "            max-width: 600px;\n" +
            "            padding: 20px;\n" +
            "            background-color: #fff;\n" +
            "            border-radius: 8px;\n" +
            "            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);\n" +
            "        }\n" +
            "        h1 {\n" +
            "            font-size: 48px;\n" +
            "            margin: 0 0 20px;\n" +
            "            color: #dc3545;\n" +
            "        }\n" +
            "        p {\n" +
            "            font-size: 18px;\n" +
            "            margin: 0 0 20px;\n" +
            "        }\n" +
            "        a {\n" +
            "            color: #007bff;\n" +
            "            text-decoration: none;\n" +
            "        }\n" +
            "        a:hover {\n" +
            "            text-decoration: underline;\n" +
            "        }\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <div class=\"container\">\n" +
            "        <h1>500</h1>\n" +
            "        <p>抱歉，服务器内部发生了错误。</p>\n" +
            "        <p>错误码：";
    private String end500="</p>\n" +
            "        <p>您可以尝试刷新页面，或者 <a target='blank' href=\"/\">返回首页</a>。</p>\n" +
            "    </div>\n" +
            "</body>\n" +
            "</html>    ";


    public void addAppDatasource(String key, DataSource appDatasource) {
        this.appDatasource.put(key, appDatasource);
    }


}
