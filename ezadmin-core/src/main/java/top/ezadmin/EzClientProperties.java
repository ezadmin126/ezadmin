package top.ezadmin;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "topezadmin")
public class EzClientProperties {

    private String appName="ez";
    private boolean cacheFlag=true;
    private String uploadUrl;
    private String downloadUrl;
    private String logType="1000-10000";
    private String regionUrl ;
    private String categoryUrl ;
    private String orgUrl ;
    private String datasourceBeanNames="dataSource";
    private String systemName="System";
    private String logoUrl="/static/logo.png" ;
    private String navUrl="/topezadmin/navs.html" ;
    private String appendJs ;
    private String searchUrl ;
    private String indexUrl;
    private String signoutUrl="/signout";
    private String messageUrl;
    private String chatUrl;
    private String adminStyle="layui";
    private String uploadPath="/data/upload/ezadmin";

    private String editLocation;
    private String listResourceLocation="classpath*:/topezadmin/config/layui/list/**/*.html";
    private String formResourceLocation="classpath*:/topezadmin/config/layui/form/**/*.html";
    private String pluginsFormResourceLocation="classpath*:/topezadmin/config/layui/plugins/form/**/*.html";
    private String pluginsListResourceLocation="classpath*:/topezadmin/config/layui/plugins/list/**/*.html";
    private String pluginsDetailResourceLocation="classpath*:/topezadmin/config/layui/plugins/detail/**/*.html";
    private String configJson="{\"favicon\":\"/static/favicon.ico\",\"logo\":\"/static/logo.png\"}";


    private String exportClass;
    private String refreshClass;
    private String holiday;
    private String layout="container";//container fluid

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public boolean isCacheFlag() {
        return cacheFlag;
    }

    public void setCacheFlag(boolean cacheFlag) {
        this.cacheFlag = cacheFlag;
    }

    public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public String getRegionUrl() {
        return regionUrl;
    }

    public void setRegionUrl(String regionUrl) {
        this.regionUrl = regionUrl;
    }

    public String getCategoryUrl() {
        return categoryUrl;
    }

    public void setCategoryUrl(String categoryUrl) {
        this.categoryUrl = categoryUrl;
    }

    public String getOrgUrl() {
        return orgUrl;
    }

    public void setOrgUrl(String orgUrl) {
        this.orgUrl = orgUrl;
    }

    public String getDatasourceBeanNames() {
        return datasourceBeanNames;
    }

    public void setDatasourceBeanNames(String datasourceBeanNames) {
        this.datasourceBeanNames = datasourceBeanNames;
    }

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

    public String getAdminStyle() {
        return adminStyle;
    }

    public void setAdminStyle(String adminStyle) {
        this.adminStyle = adminStyle;
    }

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public String getListResourceLocation() {
        return listResourceLocation;
    }

    public void setListResourceLocation(String listResourceLocation) {
        this.listResourceLocation = listResourceLocation;
    }

    public String getFormResourceLocation() {
        return formResourceLocation;
    }

    public void setFormResourceLocation(String formResourceLocation) {
        this.formResourceLocation = formResourceLocation;
    }

    public String getPluginsFormResourceLocation() {
        return pluginsFormResourceLocation;
    }

    public void setPluginsFormResourceLocation(String pluginsFormResourceLocation) {
        this.pluginsFormResourceLocation = pluginsFormResourceLocation;
    }

    public String getPluginsListResourceLocation() {
        return pluginsListResourceLocation;
    }

    public void setPluginsListResourceLocation(String pluginsListResourceLocation) {
        this.pluginsListResourceLocation = pluginsListResourceLocation;
    }

    public String getPluginsDetailResourceLocation() {
        return pluginsDetailResourceLocation;
    }

    public void setPluginsDetailResourceLocation(String pluginsDetailResourceLocation) {
        this.pluginsDetailResourceLocation = pluginsDetailResourceLocation;
    }

    public String getConfigJson() {
        return configJson;
    }

    public void setConfigJson(String configJson) {
        this.configJson = configJson;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getEditLocation() {
        return editLocation;
    }

    public void setEditLocation(String editLocation) {
        this.editLocation = editLocation;
    }

    public String getExportClass() {
        return exportClass;
    }

    public void setExportClass(String exportClass) {
        this.exportClass = exportClass;
    }

    public String getHoliday() {
        return holiday;
    }

    public void setHoliday(String holiday) {
        this.holiday = holiday;
    }

    public String getRefreshClass() {
        return refreshClass;
    }

    public void setRefreshClass(String refreshClass) {
        this.refreshClass = refreshClass;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }
}