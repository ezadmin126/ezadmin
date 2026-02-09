package top.ezadmin;

import top.ezadmin.common.utils.EzJson;
import top.ezadmin.common.utils.EzJsonImpl;
import top.ezadmin.plugins.cache.CaffeineCache;
import top.ezadmin.plugins.cache.EzCache;
import top.ezadmin.plugins.export.CSVExport;
import top.ezadmin.plugins.export.EzExport;
import top.ezadmin.plugins.refresh.DefaultRefresh;
import top.ezadmin.plugins.refresh.EzRefresh;
import top.ezadmin.plugins.sqlparser.EzSqlParser;
import top.ezadmin.plugins.templates.EzTemplate;
import top.ezadmin.plugins.templates.ThymeleafEzTemplate;
import top.ezadmin.web.Config;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

 
public class EzBootstrapConfig {
    /**
     * 导出类名，指定数据导出功能的实现类
     */
    private String exportClass = "top.ezadmin.plugins.export.CSVExport";

    private EzExport ezExport=new CSVExport();

    /**
     * 缓存类名，指定缓存功能的实现类
     */
    private String cacheClass = "top.ezadmin.plugins.cache.CaffeineCache";
    private EzCache ezCache=new CaffeineCache();

    /**
     * 刷新类名，指定数据刷新功能的实现类
     */
    private String refreshClass = "top.ezadmin.plugins.refresh.DefaultRefresh";
    private EzRefresh ezRefresh=new DefaultRefresh();
    /**
     * 前缀URL，系统所有接口的统一前缀路径
     */
    private String prefixUrl = "/topezadmin";

    private Map<String,javax.sql.DataSource> datasourceMap = new HashMap<>();

    private EzTemplate ezTemplate=new ThymeleafEzTemplate();

    private EzJson ezJson;

    private EzSqlParser ezSqlParser;


    /**
     * SQL缓存开关，控制是否启用SQL查询缓存
     */
    private boolean sqlCache = true;
    /**
     * 高级搜索是否开启
     */
    private boolean customSearchOpen = true;
    /**
     * fluid 宽屏
     * container 窄屏
     */
    private String layout="fluid";


    private String adminStyle="layui";

    private String adminIndexHtml="index";//默认index

    /**
     * 列表配置资源列表，存储所有列表页面的配置信息
     */
    private List<Config> listConfigResources=new ArrayList<>();

    /**
     * 表单配置资源列表，存储所有表单页面的配置信息
     */
    private List<Config> formConfigResources=new ArrayList<>();

    /**
     * 插件表单配置资源列表，存储插件相关的表单配置
     */
    private List<Config> pluginsFormConfigResources=new ArrayList<>();

    /**
     * 插件列表配置资源列表，存储插件相关的列表配置
     */
    private List<Config> pluginsListConfigResources=new ArrayList<>();

    /**
     *  兼容首页start
     */
    private String systemName = "System";
    /**
     * logo
     */
    private String logoUrl = "/static/logo.png";
    /**
     * 菜单url
     */
    private String navUrl = "/topezadmin/navs.html";
    /**
     * 首页加入的js
     */
    private String appendJs;
    /**
     * 页头搜索url
     */
    private String searchUrl;
    /**
     * 页头首页url
     */
    private String indexUrl;
    /**
     * 页头登出url
     */
    private String signoutUrl = "/signout";
    /**
     * 页头消息url
     */
    private String messageUrl;
    /**
     * 页头聊天url
     */
    private String chatUrl;
    /**
     * 页头清理缓存url
     */
    private String clearUrl;

    private String appName;
    private boolean menuChildOpen;
    /**
     * 上传url
     */
    private String uploadUrl;
    /**
     * 下载url
     */
    private String downloadUrl;
    /**
     * 本地上传时的路径
     */
    private String uploadPath = "/data/upload/ezadmin";
    /**
     * 自定义配置
     */
    private String configJson = "{\"favicon\":\"/static/favicon.ico\",\"logo\":\"/static/logo.png\"}";

    private Map<String,Object> config=new HashMap<>();

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getNavUrl() {
        return navUrl;
    }

    public void setNavUrl(String navUrl) {
        this.navUrl = navUrl;
    }

    public String getAppendJs() {
        return appendJs;
    }

    public void setAppendJs(String appendJs) {
        this.appendJs = appendJs;
    }

    public String getSearchUrl() {
        return searchUrl;
    }

    public void setSearchUrl(String searchUrl) {
        this.searchUrl = searchUrl;
    }

    public String getIndexUrl() {
        return indexUrl;
    }

    public void setIndexUrl(String indexUrl) {
        this.indexUrl = indexUrl;
    }

    public String getSignoutUrl() {
        return signoutUrl;
    }

    public void setSignoutUrl(String signoutUrl) {
        this.signoutUrl = signoutUrl;
    }

    public String getMessageUrl() {
        return messageUrl;
    }

    public void setMessageUrl(String messageUrl) {
        this.messageUrl = messageUrl;
    }

    public String getChatUrl() {
        return chatUrl;
    }

    public void setChatUrl(String chatUrl) {
        this.chatUrl = chatUrl;
    }

    public String getClearUrl() {
        return clearUrl;
    }

    public void setClearUrl(String clearUrl) {
        this.clearUrl = clearUrl;
    }

    /**
     *  兼容首页end
     */





    public String getExportClass() {
        return exportClass;
    }

    public void setExportClass(String exportClass) {
        this.exportClass = exportClass;
    }

    public String getCacheClass() {
        return cacheClass;
    }

    public void setCacheClass(String cacheClass) {
        this.cacheClass = cacheClass;
    }

    public String getRefreshClass() {
        return refreshClass;
    }

    public void setRefreshClass(String refreshClass) {
        this.refreshClass = refreshClass;
    }

    public String getPrefixUrl() {
        return prefixUrl;
    }

    public void setPrefixUrl(String prefixUrl) {
        this.prefixUrl = prefixUrl;
    }

    public Map<String, DataSource> getDatasourceMap() {
        return datasourceMap;
    }

    public void setDatasourceMap(Map<String, DataSource> datasourceMap) {
        this.datasourceMap = datasourceMap;
    }

    public boolean isSqlCache() {
        return sqlCache;
    }

    public void setSqlCache(boolean sqlCache) {
        this.sqlCache = sqlCache;
    }

    public boolean isCustomSearchOpen() {
        return customSearchOpen;
    }

    public void setCustomSearchOpen(boolean customSearchOpen) {
        this.customSearchOpen = customSearchOpen;
    }


    public EzTemplate getEzTemplate() {
        return ezTemplate;
    }

    public void setEzTemplate(EzTemplate ezTemplate) {
        this.ezTemplate = ezTemplate;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public EzExport getEzExport() {
        return ezExport;
    }

    public void setEzExport(EzExport ezExport) {
        this.ezExport = ezExport;
    }

    public EzCache getEzCache() {
        return ezCache;
    }

    public void setEzCache(EzCache ezCache) {
        this.ezCache = ezCache;
    }

    public EzRefresh getEzRefresh() {
        return ezRefresh;
    }

    public void setEzRefresh(EzRefresh ezRefresh) {
        this.ezRefresh = ezRefresh;
    }

    public String getAdminStyle() {
        return adminStyle;
    }

    public void setAdminStyle(String adminStyle) {
        this.adminStyle = adminStyle;
    }

    public List<Config> getListConfigResources() {
        return listConfigResources;
    }

    public void setListConfigResources(List<Config> listConfigResources) {
        this.listConfigResources = listConfigResources;
    }

    public List<Config> getFormConfigResources() {
        return formConfigResources;
    }

    public void setFormConfigResources(List<Config> formConfigResources) {
        this.formConfigResources = formConfigResources;
    }

    public List<Config> getPluginsFormConfigResources() {
        return pluginsFormConfigResources;
    }

    public void setPluginsFormConfigResources(List<Config> pluginsFormConfigResources) {
        this.pluginsFormConfigResources = pluginsFormConfigResources;
    }

    public List<Config> getPluginsListConfigResources() {
        return pluginsListConfigResources;
    }

    public void setPluginsListConfigResources(List<Config> pluginsListConfigResources) {
        this.pluginsListConfigResources = pluginsListConfigResources;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public String getConfigJson() {
        return configJson;
    }

    public void setConfigJson(String configJson) {
        this.configJson = configJson;
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }

    public EzJson getEzJson() {
        return ezJson==null?new EzJsonImpl():ezJson;
    }

    public void setEzJson(EzJson ezJson) {
        this.ezJson = ezJson;
    }

    public EzSqlParser getEzSqlParser() {
        return ezSqlParser;
    }

    public void setEzSqlParser(EzSqlParser ezSqlParser) {
        this.ezSqlParser = ezSqlParser;
    }

    public boolean isMenuChildOpen() {
        return menuChildOpen;
    }

    public void setMenuChildOpen(boolean menuChildOpen) {
        this.menuChildOpen = menuChildOpen;
    }

    public String getAdminIndexHtml() {
        return adminIndexHtml;
    }

    public void setAdminIndexHtml(String adminIndexHtml) {
        this.adminIndexHtml = adminIndexHtml;
    }
}
