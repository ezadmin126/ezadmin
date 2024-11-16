/**
 * Copyright
 */

package top.ezadmin;

import org.springframework.context.ApplicationContext;
import top.ezadmin.dao.FormDao;
import top.ezadmin.dao.ListDao;
 import top.ezadmin.dao.PluginsDao;
import top.ezadmin.plugins.cache.EzCache;
import top.ezadmin.plugins.export.EzExport;
import top.ezadmin.plugins.refresh.EzRefresh;
import top.ezadmin.plugins.sqlog.EzSqlogDataSource;
import top.ezadmin.web.Config;
import top.ezadmin.web.SpringContextHolder;
import top.ezadmin.web.filters.ez.ControllerFilter;
import top.ezadmin.web.filters.ez.Filter;
import top.ezadmin.web.filters.ez.NotFoundFilter;
import top.ezadmin.web.filters.ez.TraceLogFilter;

 import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ezadmin.common.utils.BeanTools;
import top.ezadmin.common.utils.StringUtils;
import top.ezadmin.common.utils.ThymeleafUtils;
import top.ezadmin.common.utils.Utils;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

///ezlist/
public class EzClientBootstrap {

    public static final Logger log = LoggerFactory.getLogger(EzClientBootstrap.class);
    private boolean sqlCache = false;
    private List<Config> listConfigResources;
    private List<Config> formConfigResources;
    private List<Config> pluginsFormConfigResources;
    private List<Config> pluginsListConfigResources;
    private List<Config> pluginsDetailConfigResources;
    private String clearUrl;


    private Map<String, EzSqlogDataSource> queryDatasourceMap = new HashMap<>();
    public final static String DEFAULT_DB_KEY="System";
    private String appName = "ez";
    private String adminStyle="layui";
    private String logType = "1000";
    private String regionUrl,categoryUrl,orgUrl;

    private String systemName;
    private String logoUrl;
    private Map<String,Object> config=new HashMap<>(8);
    private String navUrl;
    private String appendJs;
    private String searchUrl;
    private String indexUrl;
    private String messageUrl;
    private String chatUrl;
    private String uploadPath="/data/ezadmin/upload/";

    private String ossEndpoint;

    private String ossAccessKeyId;
    private String ossAccessKeySecret;

    private String exportClass="top.ezadmin.plugins.export.CSVExport";
    private String cacheClass="top.ezadmin.plugins.cache.CaffeineCache";
    private String refreshClass="top.ezadmin.plugins.refresh.DefaultRefresh";

    private String holiday="[[],[]]";

    //参考这个controller 覆盖
    private String uploadUrl = "/topezadmin/form/upload.html";
    private String downloadUrl = "/topezadmin/form/download.html?fileId=";

    private static EzClientBootstrap bootstrap = new EzClientBootstrap();
    private EzCache ezCache;
    private EzExport ezExport;
    private EzRefresh ezRefresh;
    private String layout;//container fluid

    private  DataSource ezDataSource;

    private EzClientBootstrap() {
    }

    public String getOssEndpoint() {
        return ossEndpoint;
    }

    public void setOssEndpoint(String ossEndpoint) {
        this.ossEndpoint = ossEndpoint;
    }

    public String getOssAccessKeyId() {
        return ossAccessKeyId;
    }

    public void setOssAccessKeyId(String ossAccessKeyId) {
        this.ossAccessKeyId = ossAccessKeyId;
    }

    public String getOssAccessKeySecret() {
        return ossAccessKeySecret;
    }

    public void setOssAccessKeySecret(String ossAccessKeySecret) {
        this.ossAccessKeySecret = ossAccessKeySecret;
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

    public String getSignoutUrl() {
        return signoutUrl;
    }

    public void setSignoutUrl(String signoutUrl) {
        this.signoutUrl = signoutUrl;
    }

    private String signoutUrl;

    public String getIndexUrl() {
        return indexUrl;
    }

    public void setIndexUrl(String indexUrl) {
        this.indexUrl = indexUrl;
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


    public static EzClientBootstrap instance() {
        return bootstrap;
    }
    public Set<String> datasourceKeys(){
            return queryDatasourceMap.keySet();
    }
    public EzSqlogDataSource getDataSourceByKey(Object key){
        if(StringUtils.equals("1",key+"") ){
            key=DEFAULT_DB_KEY;
        }
        return queryDatasourceMap.get(StringUtils.lowerCase(Utils.trimNull(key)));
    }
    public  DataSource getEzDataSource ( ){
        if(ezDataSource!=null){
            return ezDataSource;
        }
        return queryDatasourceMap.get(datasourceKey("dataSource"));
    }
    public  void setEzDataSource (DataSource source ){
        if(source instanceof EzSqlogDataSource){
            ezDataSource =source;
        }else{
            EzSqlogDataSource source2 = new EzSqlogDataSource();
            source2.setRealDataSource(source);
            source2.setLogType(logType);
            ezDataSource=source2;
        }
    }


    public void addBizDataSource(String key, DataSource ezSqlogDataSource){

        EzSqlogDataSource source = new EzSqlogDataSource();
        source.setRealDataSource(ezSqlogDataSource);
        source.setLogType(logType);
        if(StringUtils.equalsIgnoreCase( DEFAULT_DB_KEY,key)){
            throw new IllegalArgumentException( DEFAULT_DB_KEY+" 为系统关键词，不能使用");
        }
        if(ezSqlogDataSource instanceof EzSqlogDataSource){
            queryDatasourceMap.put(datasourceKey( key),(EzSqlogDataSource)ezSqlogDataSource);
        }else{
            queryDatasourceMap.put(datasourceKey( key) ,source);
        }
    }

    private String datasourceKey( String key){
        if(key.indexOf("-")>0){
            return StringUtils.lowerCase( key);
        }
        else{
            return StringUtils.lowerCase( key);
        }
    }

    public void init(    ) throws  Exception {
        try {
            if (start.compareAndSet(false, true)) {
              //  log.info("start init   plugins ");
                PluginsDao.getInstance().init();
             //   log.info("start init   list ");
                ListDao.getInstance().init();
              //  log.info("start init   form ");
                FormDao.getInstance().init();
             //   log.info("end init     ");
                ThymeleafUtils.init(sqlCache);
                filter = new TraceLogFilter();
                filter.next(new ControllerFilter()).next(new NotFoundFilter());
                //初始化导出 工具
                ezExport = (EzExport) BeanTools.applicationInstance(getExportClass());
                ezCache = (EzCache) BeanTools.applicationInstance(getCacheClass());
                ezRefresh=(EzRefresh)  BeanTools.applicationInstance(getRefreshClass());
                getCache().cacheFlag(sqlCache);
                ezRefresh.refreshAll();
            }
        }catch (Exception e){
            log.error("start init    ",e);
        }
    }
    public void initSpring(ApplicationContext applicationContext1){
        SpringContextHolder.init(applicationContext1);
    }

    public EzCache getCache() {
        return getEzCache();
    }

    static Filter filter;


    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws Exception {
        try {
            request.setCharacterEncoding("UTF-8");
            response.setCharacterEncoding("UTF-8");
            filter.doFilter(request, response,filterChain);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public boolean isSqlCache() {
        return sqlCache;
    }

    public void setSqlCache(boolean sqlCache) {
        this.sqlCache = sqlCache;
    }


    public AtomicBoolean start = new AtomicBoolean(false);




    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public String getCategoryUrl() {
        return categoryUrl;
    }

    public void setCategoryUrl(String categoryUrl) {
        this.categoryUrl = categoryUrl;
    }

    public String getRegionUrl() {
        return regionUrl;
    }

    public void setRegionUrl(String regionUrl) {
        this.regionUrl = regionUrl;
    }

    public String getOrgUrl() {
        return orgUrl;
    }

    public void setOrgUrl(String orgUrl) {
        this.orgUrl = orgUrl;
    }

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public String getAdminStyle() {
        return adminStyle;
    }

    public void setAdminStyle(String adminStyle) {
        if(StringUtils.isBlank(adminStyle)){
            return;
        }
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



    public String getClearUrl() {
        return clearUrl;
    }

    public void setClearUrl(String clearUrl) {
        this.clearUrl = clearUrl;
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

    public List<Config> getPluginsDetailConfigResources() {
        return pluginsDetailConfigResources;
    }

    public void setPluginsDetailConfigResources(List<Config> pluginsDetailConfigResources) {
        this.pluginsDetailConfigResources = pluginsDetailConfigResources;
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
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

    public EzExport getExport() {
        return ezExport;
    }

    public void setExport(EzExport ezExport) {
        this.ezExport = ezExport;
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



    public String getCacheClass() {
        return cacheClass;
    }

    public void setCacheClass(String cacheClass) {
        this.cacheClass = cacheClass;
    }

    public EzCache getEzCache() {
        return ezCache;
    }

    public void setEzCache(EzCache ezCache) {
        this.ezCache = ezCache;
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
