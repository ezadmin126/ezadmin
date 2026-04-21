package top.ezadmin.spring;


import com.alibaba.fastjson2.JSON;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ezadmin.EzBootstrap;
import top.ezadmin.EzBootstrapConfig;
import top.ezadmin.common.EzAdminRuntimeException;
import top.ezadmin.common.constants.SessionConstants;
import top.ezadmin.common.enums.DefaultParamEnum;
import top.ezadmin.common.utils.BeanTools;
import top.ezadmin.common.utils.JSONUtils;
import top.ezadmin.common.utils.StringUtils;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.plugins.EzFastJson2Impl;
import top.ezadmin.plugins.EzSqlParserImpl;
import top.ezadmin.plugins.cache.EzCache;
import top.ezadmin.plugins.export.EzExport;
import top.ezadmin.plugins.refresh.EzRefresh;
import top.ezadmin.web.EzResult;
import top.ezadmin.web.RequestContext;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class EzClientServletFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(EzClientServletFilter.class);


    private Map<String, DataSource> appDatasource = new HashMap<String, DataSource>();

    private EzClientProperties ezClientProperties;


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
        ezBootstrapConfig.setEzJson(new EzFastJson2Impl());
        ezBootstrapConfig.setEzSqlParser(new EzSqlParserImpl());

        String configJson = ezClientProperties.getConfigJson();
        Map<String, Object> configMap = ezBootstrapConfig.getEzJson().parseObjectMap(configJson);
        ezBootstrapConfig.setConfig(configMap);

        ezBootstrapConfig.setApiKey(ezClientProperties.getApiKey());
        ezBootstrapConfig.setApiUrl(ezClientProperties.getApiUrl());
        ezBootstrapConfig.setModel(ezClientProperties.getModel());
        if (ezClientProperties.getTemperature() != null) {
            ezBootstrapConfig.setTemperature(ezClientProperties.getTemperature());
        }


        String beanNames[] = org.apache.commons.lang.StringUtils.split(ezClientProperties.getDatasourceBeanNames(), ",");
        //注入数据源
        for (int i = 0; i < beanNames.length; i++) {
            DataSource dataSource = (DataSource) SpringContextHolder.getBean(beanNames[i]);
            if (dataSource == null) {
                logger.error(" can not find datasource with bean name :" + beanNames[i]);
                throw new IllegalArgumentException(" can not find datasource with bean name :" + beanNames[i]);
            }
            if (i == 0) {
                ezBootstrapConfig.getDatasourceMap().put("defaultDataSource", dataSource);
            }
            ezBootstrapConfig.getDatasourceMap().put(beanNames[i].toLowerCase(), dataSource);
        }
        try {
            ezBootstrapConfig.setListConfigResources(ConfigUtils.loadFiles(ezClientProperties.getListResourceLocation()));
            ezBootstrapConfig.setFormConfigResources(ConfigUtils.loadFiles(ezClientProperties.getFormResourceLocation()));
            ezBootstrapConfig.setPluginsFormConfigResources(ConfigUtils.loadFiles(ezClientProperties.getPluginsFormResourceLocation()));
            ezBootstrapConfig.setPluginsListConfigResources(ConfigUtils.loadFiles(ezClientProperties.getPluginsListResourceLocation()));
        } catch (Exception e) {
            logger.error("加载配置文件异常 {} {} {} {}", ezClientProperties.getListResourceLocation(), ezClientProperties.getFormResourceLocation()
                    , ezClientProperties.getPluginsFormResourceLocation(), ezClientProperties.getPluginsListResourceLocation(), e);
        }
        EzBootstrap.getInstance().init(ezBootstrapConfig);
    }


    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        try {
            RequestContext requestContext = new RequestContext();
            requestContext.setRequestParams(requestToMap(httpServletRequest));
            requestContext.setSessionParams(sessionToMap(httpServletRequest.getSession()));
            requestContext.setRequestURI(httpServletRequest.getRequestURI());
            if (httpServletRequest.getRequestURI().equals("/topezadmin/dev.html")) {
                httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/topezadmin/list/listall");
                return;
            }
            requestContext.setServerName(httpServletRequest.getServerName());
            requestContext.setServerPort(httpServletRequest.getServerPort());
            requestContext.setContextPath(httpServletRequest.getContextPath());
            requestContext.setMethod(httpServletRequest.getMethod());
            EzResult result = EzBootstrap.getInstance().generate(requestContext);
            if (result.isSuccess() && StringUtils.equals(result.getCode(), "HTML")) {
                httpServletResponse.setContentType("text/html;charset=UTF-8");
                String html = ((HashMap) result.getData()).get("html").toString();
                IOUtils.copy(IOUtils.toInputStream(html, "UTF-8"), httpServletResponse.getOutputStream());
                return;
            } else if (result.isSuccess() && StringUtils.equals(result.getCode(), "EXPORT")) {
                String fileName = ((HashMap) result.getData()).get("fileName").toString();
                String contentType = Utils.trimEmptyDefault(((HashMap) result.getData()).get("contentType"), "application/octet-stream");
                byte[] data = (byte[]) ((HashMap) result.getData()).get("html");
                httpServletResponse.setContentType(contentType);
                httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes(), "ISO-8859-1"));
                IOUtils.copy(new ByteArrayInputStream(data), httpServletResponse.getOutputStream());
                return;
            } else if (StringUtils.equals(result.getCode(), "JSON")) {
                httpServletResponse.setContentType("application/json;charset=UTF-8");
                IOUtils.copy(IOUtils.toInputStream(JSONUtils.toJSONString(result.getData()), "UTF-8"), httpServletResponse.getOutputStream());
            } else if (result.getCode().equals("404")) {
                httpServletResponse.sendRedirect("/404");
            } else if (result.getCode().equals("500")) {
                logger.error(JSON.toJSONString(result));
                throw new EzAdminRuntimeException(result.getMessage());
            } else {
                httpServletResponse.setContentType("application/json;charset=UTF-8");
                IOUtils.copy(IOUtils.toInputStream(JSONUtils.toJSONString(EzResult.instance().message("没有找到渲染器")), "UTF-8"), httpServletResponse.getOutputStream());
            }
        } catch (Exception e) {
            logger.error("", e);
            throw e;
        } finally {
            Utils.clearLog();
        }
    }

    //同
    public static Map<String, Object> requestToMap(HttpServletRequest request) {
        Map<String, Object> searchParamsValues = new HashMap<>();

        String contentType = request.getContentType();
        if (contentType != null && contentType.contains("application/json")) {
            byte[] content = null;
            try {
                content = StringUtils.parseToByte(request.getInputStream());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (content != null && content.length > 0) {
                try {
                    String jsonStr = new String(content, request.getCharacterEncoding());
                    Map<String, Object> jsonParams = JSONUtils.parseObjectMap(jsonStr);
                    if (jsonParams != null) {
                        jsonParams.forEach((k, v) ->
                                searchParamsValues.put(k, transValue(request, v)));
                    }
                } catch (Exception e) {
                    logger.error("解析JSON请求体失败", e);
                }
            }
        }


        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            String[] v = entry.getValue();
            String k = entry.getKey();
            if (v == null) {
                continue;
            }
            if (v.length == 1) {
                searchParamsValues.put(k, transValue(request, v[0]));
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

    private static String transValue(HttpServletRequest request, Object value) {
        String tempValue = "";
        if (value instanceof String[]) {
            if (value != null && ((String[]) value).length == 1) {
                tempValue = ((String[]) value)[0];
            }
        } else {
            tempValue = value.toString();
        }
        if (StringUtils.equalsIgnoreCase(DefaultParamEnum.EZ_TODAY_DATE.name(), tempValue)) {
            return DefaultParamEnum.getValue(DefaultParamEnum.EZ_TODAY_DATE.name());
        } else if (StringUtils.equalsIgnoreCase(DefaultParamEnum.EZ_TODAY_DATE_NORMAL.name(), tempValue)) {
            return DefaultParamEnum.getValue(DefaultParamEnum.EZ_TODAY_DATE_NORMAL.name());
        } else if (StringUtils.equalsIgnoreCase(DefaultParamEnum.EZ_CURRENT_MONTH.name(), tempValue)) {
            return DefaultParamEnum.getValue(DefaultParamEnum.EZ_CURRENT_MONTH.name());
        } else {
            return Utils.trimNull(tempValue);
        }
    }


    private Map<String, String> sessionToMap(HttpSession session) {
        Map<String, String> map = new HashMap();
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


    public void destroy() {

    }

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
