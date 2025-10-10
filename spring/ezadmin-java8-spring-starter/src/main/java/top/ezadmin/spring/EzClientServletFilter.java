package top.ezadmin.spring;


import top.ezadmin.EzBootstrap;
import top.ezadmin.EzBootstrapConfig;
import top.ezadmin.common.constants.SessionConstants;
import top.ezadmin.common.enums.DefaultParamEnum;
import top.ezadmin.common.utils.BeanTools;
import top.ezadmin.common.utils.JSONUtils;
import top.ezadmin.common.utils.StringUtils;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.plugins.cache.EzCache;
import top.ezadmin.plugins.export.EzExport;
import top.ezadmin.plugins.refresh.EzRefresh;
import top.ezadmin.web.EzResult;
import top.ezadmin.web.RequestContext;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;

public class EzClientServletFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(EzClientServletFilter.class);


    private Map<String, DataSource> appDatasource = new HashMap<String, DataSource>();

    private EzClientProperties ezClientProperties;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        EzBootstrapConfig ezBootstrapConfig = new EzBootstrapConfig();
        try {
            if (StringUtils.isNotBlank(ezClientProperties.getExportClass())) {
                ezBootstrapConfig.setExportClass(ezClientProperties.getExportClass());
                ezBootstrapConfig.setEzExport((EzExport) BeanTools.applicationInstance(ezBootstrapConfig.getExportClass()));
            }
        } catch (Exception e) {
            logger.error("实例化自定义导出插件异常", e);
        }
        try {
            if (StringUtils.isNotBlank(ezClientProperties.getCacheClass())) {
                ezBootstrapConfig.setCacheClass(ezClientProperties.getCacheClass());
                ezBootstrapConfig.setEzCache((EzCache) BeanTools.applicationInstance(ezBootstrapConfig.getCacheClass()));
            }
        } catch (Exception e) {
            logger.error("实例化自定义缓存插件异常", e);
        }
        try {
            if (StringUtils.isNotBlank(ezClientProperties.getRefreshClass())) {
                ezBootstrapConfig.setRefreshClass(ezClientProperties.getRefreshClass());
                ezBootstrapConfig.setEzRefresh((EzRefresh) BeanTools.applicationInstance(ezBootstrapConfig.getRefreshClass()));

            }
        } catch (Exception e) {
            logger.error("实例化自定义刷新插件异常", e);
        }
        ezBootstrapConfig.setPrefixUrl(ezClientProperties.getPrefixUrl());
        ezBootstrapConfig.setSqlCache(ezClientProperties.isCacheFlag());
        ezBootstrapConfig.setLayout(ezClientProperties.getLayout());
        ezBootstrapConfig.setAdminStyle(ezClientProperties.getAdminStyle());


        ezBootstrapConfig.setSystemName(ezClientProperties.getSystemName());
        ezBootstrapConfig.setLogoUrl(ezClientProperties.getLogoUrl());
        ezBootstrapConfig.setNavUrl(ezClientProperties.getNavUrl());
        ezBootstrapConfig.setAppendJs(ezClientProperties.getAppendJs());
        ezBootstrapConfig.setIndexUrl(ezClientProperties.getIndexUrl());
        ezBootstrapConfig.setSignoutUrl(ezClientProperties.getSignoutUrl());
        ezBootstrapConfig.setMessageUrl(ezClientProperties.getMessageUrl());
        ezBootstrapConfig.setChatUrl(ezClientProperties.getChatUrl());
        ezBootstrapConfig.setClearUrl(ezClientProperties.getClearUrl());
        ezBootstrapConfig.setAppName(ezClientProperties.getAppName());

        ezBootstrapConfig.setUploadUrl(ezClientProperties.getUploadUrl());
        ezBootstrapConfig.setDownloadUrl(ezClientProperties.getDownloadUrl());
        ezBootstrapConfig.setUploadPath(ezClientProperties.getUploadPath());


        String configJson = ezClientProperties.getConfigJson();
        Map<String, Object> configMap = JSONUtils.parseObjectMap(configJson);
        ezBootstrapConfig.setConfig(configMap);


        String beanNames[] = org.apache.commons.lang.StringUtils.split(ezClientProperties.getDatasourceBeanNames(), ",");
        //注入数据源
        for (int i = 0; i < beanNames.length; i++) {
            DataSource dataSource = (DataSource) SpringContextHolder.getBean(beanNames[i]);
            if (dataSource == null) {
                logger.error(" can not find datasource with bean name :" + beanNames[i]);
                throw new IllegalArgumentException(" can not find datasource with bean name :" + beanNames[i]);
            }
            if(i==0){
                ezBootstrapConfig.getDatasourceMap().put("defaultDataSource", dataSource);
            }
            ezBootstrapConfig.getDatasourceMap().put(beanNames[i].toLowerCase(), dataSource);
        }
        try {
            ezBootstrapConfig.setListConfigResources(ConfigUtils.loadFiles(ezClientProperties.getListResourceLocation()));
            ezBootstrapConfig.setFormConfigResources(ConfigUtils.loadFiles(ezClientProperties.getFormResourceLocation()));
            ezBootstrapConfig.setPluginsFormConfigResources(ConfigUtils.loadFiles(ezClientProperties.getPluginsFormResourceLocation()));
            ezBootstrapConfig.setPluginsListConfigResources(ConfigUtils.loadFiles(ezClientProperties.getPluginsListResourceLocation()));
        }catch (Exception e){
            logger.error("加载配置文件异常 {} {} {} {}",ezClientProperties.getListResourceLocation(),ezClientProperties.getFormResourceLocation()
                    ,ezClientProperties.getPluginsFormResourceLocation(),ezClientProperties.getPluginsListResourceLocation(),e);
        }
        EzBootstrap.getInstance().init(ezBootstrapConfig);
    }
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final UrlPathHelper urlPathHelper = new UrlPathHelper();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        try {
            RequestContext requestContext = new RequestContext();
            requestContext.setRequestParams(requestToMap(httpServletRequest));
            requestContext.setSessionParams(sessionToMap(httpServletRequest.getSession()));

            String requestUrl=urlPathHelper.getRequestUri(httpServletRequest);
            requestContext.setRequestURI(requestUrl);

            if(httpServletRequest.getRequestURI().equals("/topezadmin/dev.html")){
                httpServletResponse.sendRedirect(httpServletRequest.getContextPath()+"/topezadmin/list/listall");
                return;
            }
            requestContext.setServerName(httpServletRequest.getServerName());
            requestContext.setServerPort(httpServletRequest.getServerPort());
            requestContext.setContextPath(httpServletRequest.getContextPath());
            requestContext.setMethod(httpServletRequest.getMethod());
            EzResult result=EzBootstrap.getInstance().generate(requestContext);
            if(result.isSuccess()&&StringUtils.equals(result.getCode(),"HTML")){
                httpServletResponse.setContentType("text/html;charset=UTF-8");
                String html= ((HashMap)result.getData()).get("html").toString();
                IOUtils.copy(IOUtils.toInputStream(html, "UTF-8"),httpServletResponse.getOutputStream());
                return;
            }else if(result.isSuccess()&&StringUtils.equals(result.getCode(),"EXPORT")){
                String fileName=((HashMap)result.getData()).get("fileName").toString();
                String contentType=Utils.trimNullDefault(((HashMap)result.getData()).get("contentType"),"application/octet-stream");
                byte[] data=(byte[]) ((HashMap)result.getData()).get("html");
                httpServletResponse.setContentType(contentType);
                httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes(), "ISO-8859-1"));
                IOUtils.copy(new ByteArrayInputStream(data),httpServletResponse.getOutputStream());
                return;
            }else  if(result.isSuccess()&&StringUtils.equals(result.getCode(),"JSON")){
                httpServletResponse.setContentType("application/json;charset=UTF-8");
                IOUtils.copy(IOUtils.toInputStream(JSONUtils.toJSONString(result.getData()), "UTF-8"),httpServletResponse.getOutputStream());
                return;
            }

        } catch (Exception e) {
            logger.error("", e);
            httpServletResponse.getWriter().println(error500 + MDC.get("traceId") + end500);
        } finally {
            Utils.clearLog();
        }
    }
    //同
    private static Map<String, Object> requestToMap(HttpServletRequest request) {
        Map<String, Object> searchParamsValues = new HashMap<>();
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            String[] v = entry.getValue();
            String k = entry.getKey();
            if (v == null) {
                continue;
            }
            if (v.length == 1) {
                searchParamsValues.put(k, v[0] == null ? null : v[0].trim());
                if (StringUtils.equalsIgnoreCase(DefaultParamEnum.EZ_TODAY_DATE.name(), v[0])) {
                    searchParamsValues.put(k, DefaultParamEnum.getValue(DefaultParamEnum.EZ_TODAY_DATE.name()));
                }
                if (StringUtils.equalsIgnoreCase(DefaultParamEnum.EZ_TODAY_DATE_NORMAL.name(), v[0])) {
                    searchParamsValues.put(k, DefaultParamEnum.getValue(DefaultParamEnum.EZ_TODAY_DATE_NORMAL.name()));
                }
                if (StringUtils.equalsIgnoreCase(DefaultParamEnum.EZ_CURRENT_MONTH.name(), v[0])) {
                    searchParamsValues.put(k, DefaultParamEnum.getValue(DefaultParamEnum.EZ_CURRENT_MONTH.name()));
                }
                if (StringUtils.equalsIgnoreCase(SessionConstants.EZ_SESSION_USER_ID_KEY, v[0])) {
                    searchParamsValues.put(k, Utils.trimNull(request.getSession().getAttribute(SessionConstants.EZ_SESSION_USER_ID_KEY)));
                }
                searchParamsValues.put(k + "_ARRAY", v);
            } else if (v.length > 1) {
                searchParamsValues.put(k + "_ARRAY", request.getParameterValues(k));
                searchParamsValues.put(k, JSONUtils.toJSONString(request.getParameterValues(k)));
            }
        }
        searchParamsValues.put("EZ_SESSION_USER_ID_KEY", Utils.trimNull(request.getSession().getAttribute(SessionConstants.EZ_SESSION_USER_ID_KEY)));
        searchParamsValues.put("EZ_SESSION_USER_NAME_KEY", Utils.trimNull(request.getSession().getAttribute(SessionConstants.EZ_SESSION_USER_NAME_KEY)));
        searchParamsValues.put("EZ_CLIENT_IP", IpUtils.getRealIp(request));
        searchParamsValues.put("EZ_REFERER", request.getHeader("referer"));
        searchParamsValues.put("COMPANY_ID", Utils.trimNull(request.getSession().getAttribute(SessionConstants.EZ_SESSION_COMPANY_ID_KEY)));
        return searchParamsValues;
    }

    private Map<String, String> sessionToMap(HttpSession session) {
        Map<String, String> map = new HashMap<>();
        try {
            Object sessionParam = session.getAttribute(SessionConstants.EZ_SESSION_PARAM_KEY);
            if (sessionParam != null && sessionParam instanceof Map) {
                map = (Map<String, String>) sessionParam;
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        map.put("EZ_SESSION_USER_ID_KEY", Utils.trimNull(session.getAttribute(SessionConstants.EZ_SESSION_USER_ID_KEY)));
        map.put("EZ_SESSION_USER_NAME_KEY", Utils.trimNull(session.getAttribute(SessionConstants.EZ_SESSION_USER_NAME_KEY)));
        map.put("EZ_SESSION_COMPANY_ID_KEY", Utils.trimNull(session.getAttribute(SessionConstants.EZ_SESSION_COMPANY_ID_KEY)));
        map.put("COMPANY_ID", Utils.trimNull(session.getAttribute(SessionConstants.EZ_SESSION_COMPANY_ID_KEY)));
        return map;
    }



    @Override
    public void destroy() {

    }

    public static String error500 = "<!DOCTYPE html>\n" +
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
    private String end500 = "</p>\n" +
            "        <p>您可以尝试刷新页面，或者 <a target='blank' href=\"/\">返回首页</a>。</p>\n" +
            "    </div>\n" +
            "</body>\n" +
            "</html>    ";


    public void addAppDatasource(String key, DataSource appDatasource) {
        this.appDatasource.put(key, appDatasource);
    }


    public EzClientProperties getEzClientProperties() {
        return ezClientProperties;
    }

    public void setEzClientProperties(EzClientProperties ezClientProperties) {
        this.ezClientProperties = ezClientProperties;
    }
}
