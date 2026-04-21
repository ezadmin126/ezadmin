package top.ezadmin.web;

import top.ezadmin.common.utils.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求上下文，用于替代HttpServletRequest
 * 包含请求参数、会话参数等信息
 */
public class RequestContext {

    /**
     * 请求参数Map (对应request.getParameterMap())
     */
    private Map<String, Object> requestParams = new HashMap<>(8);
    ;

    /**
     * 会话参数Map (对应session.getAttribute())
     */
    private Map<String, String> sessionParams = new HashMap<>(8);
    ;

    /**
     * 请求URI
     */
    private String requestURI;


    /**
     * 请求方法 (GET, POST等)
     */
    private String method = "GET";

    /**
     * 请求头信息
     */
    private Map<String, String> headers = new HashMap<>(8);

    /**
     * Cookie信息
     */
    private Map<String, String> cookies = new HashMap<>(8);

    /**
     * 上下文路径
     */
    private String contextPath = "/";

    private String serverName = "localhost";
    private int serverPort = 80;

    public RequestContext() {
    }

    public RequestContext(Map<String, Object> requestParams, Map<String, String> sessionParams) {
        this.requestParams = requestParams;
        this.sessionParams = sessionParams;
    }

    // Getters and Setters
    public Map<String, Object> getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(Map<String, Object> requestParams) {
        this.requestParams = requestParams;
    }

    public Map<String, String> getSessionParams() {
        return sessionParams;
    }

    public void setSessionParams(Map<String, String> sessionParams) {
        this.sessionParams = sessionParams;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public void setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    /**
     * 获取请求参数
     */
    public String getParameter(String name) {
        return requestParams != null ? Utils.trimNull(requestParams.get(name)) : null;
    }

    /**
     * 获取会话参数
     */
    public Object getSessionAttribute(String name) {
        return sessionParams != null ? sessionParams.get(name) : null;
    }

    /**
     * 获取请求头
     */
    public String getHeader(String name) {
        return headers != null ? headers.get(name) : null;
    }

    /**
     * 获取Cookie值
     */
    public String getCookie(String name) {
        return cookies != null ? cookies.get(name) : null;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

}
