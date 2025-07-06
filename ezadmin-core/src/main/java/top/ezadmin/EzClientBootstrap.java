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

/**
 * EzAdmin客户端启动引导类
 * 负责整个EzAdmin系统的初始化和配置管理
 */
public class EzClientBootstrap {

    /** 日志记录器 */
    public static final Logger log = LoggerFactory.getLogger(EzClientBootstrap.class);

   
    
    
    /** SQL缓存开关，控制是否启用SQL查询缓存 */
    private boolean sqlCache = false;
    
    /** 列表配置资源列表，存储所有列表页面的配置信息 */
    private List<Config> listConfigResources;
    
    /** 表单配置资源列表，存储所有表单页面的配置信息 */
    private List<Config> formConfigResources;
    
    /** 插件表单配置资源列表，存储插件相关的表单配置 */
    private List<Config> pluginsFormConfigResources;
    
    /** 插件列表配置资源列表，存储插件相关的列表配置 */
    private List<Config> pluginsListConfigResources;
    
    /** 插件详情配置资源列表，存储插件相关的详情页面配置 */
    private List<Config> pluginsDetailConfigResources;
    
    /** 清除缓存URL，用于清除系统缓存的接口地址 */
    private String clearUrl;

    /** 数据源映射表，key为数据源名称，value为对应的数据源对象 */
    private Map<String, EzSqlogDataSource> queryDatasourceMap = new HashMap<>();
    
    /** 默认数据库键名，系统默认数据源的标识 */
    public final static String DEFAULT_DB_KEY="System";
    
    /** 应用名称，默认为"ez" */
    private String appName = "ez";
    
    /** 管理界面样式，默认为"layui"，支持自定义UI框架 */
    private String adminStyle="layui";
    
    /** 日志类型，用于控制日志输出级别和格式 */
    private String logType = "1000";
    
    
    
    /** 前缀URL，系统所有接口的统一前缀路径 */
    private String prefixUrl="/topezadmin";
    
   

    /** 导出类名，指定数据导出功能的实现类 */
    private String exportClass="top.ezadmin.plugins.export.CSVExport";
    
    /** 缓存类名，指定缓存功能的实现类 */
    private String cacheClass="top.ezadmin.plugins.cache.CaffeineCache";
    
    /** 刷新类名，指定数据刷新功能的实现类 */
    private String refreshClass="top.ezadmin.plugins.refresh.DefaultRefresh";

    /** 节假日配置，JSON格式的节假日数据，用于日期计算 */
    private String holiday="[[],[]]";

    /** 文件上传URL，文件上传功能的接口地址 */
    private String uploadUrl = "/topezadmin/form/upload.html";
    
    /** 文件下载URL，文件下载功能的接口地址 */
    private String downloadUrl = "/topezadmin/form/download.html?fileId=";

    /** 单例实例，EzClientBootstrap的全局唯一实例 */
    private static EzClientBootstrap bootstrap = new EzClientBootstrap();
    
    /** 缓存对象，系统缓存功能的实现实例 */
    private EzCache ezCache;
    
    /** 导出对象，数据导出功能的实现实例 */
    private EzExport ezExport;
    
    /** 刷新对象，数据刷新功能的实现实例 */
    private EzRefresh ezRefresh;
    
    /** 布局类型，页面布局方式，如"container"或"fluid" */
    private String layout;//container fluid

    /** 默认数据源，系统默认使用的数据源对象 */
    private  DataSource ezDataSource;

    /** 
     * 过滤器链，用于处理HTTP请求的过滤器
     * 1.TraceLogFilter 打印sql
     * 2.ControllerFilter 根据url前缀选择跳转到哪个controller
     * 3.NotFoundFilter 404页面 @deprecated 
     */
    private static Filter filter;

    /** 私有构造函数，确保单例模式 */
    private EzClientBootstrap() {
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
                filter.next(new ControllerFilter()) ;
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


    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws Exception {
        try {
            request.setCharacterEncoding("UTF-8");
            response.setCharacterEncoding("UTF-8");
            filter.doFilter(request, response,filterChain);
        } catch (Exception e) {
            log.error("", e);
        }
    }

 /**
     * 用于admin框架的几个参数，只用列表无需关注  start----------------
     */
/** 地区URL，用于地区数据查询的接口地址 */
private String regionUrl;
    
/** 分类URL，用于分类数据查询的接口地址 */
private String categoryUrl;

/** 组织URL，用于组织数据查询的接口地址 */
private String orgUrl;

/** 系统名称，显示在管理界面的系统标题 */
private String systemName;

/** Logo图片URL，系统Logo的访问地址 */
private String logoUrl;

/** 系统配置映射表，存储各种系统级别的配置参数 */
private Map<String,Object> config=new HashMap<>(8);

/** 导航URL，系统导航菜单的接口地址 */
private String navUrl;

/** 追加JavaScript代码，用于在页面中注入自定义JS脚本 */
private String appendJs;

/** 搜索URL，全局搜索功能的接口地址 */
private String searchUrl;

/** 首页URL，系统首页的访问地址 */
private String indexUrl;
 /** 消息URL，系统消息通知的接口地址 */
 private String messageUrl;
    
 /** 聊天URL，系统聊天功能的接口地址 */
 private String chatUrl;
 
 /** 退出登录URL，用户退出登录的接口地址 */
 private String signoutUrl;
 
 /** 文件上传路径，系统文件上传的本地存储目录 */
 private String uploadPath="/data/ezadmin/upload/";

 /** OSS端点地址，阿里云对象存储服务的访问端点 */
 private String ossEndpoint;

 /** OSS访问密钥ID，阿里云对象存储服务的访问密钥 */
 private String ossAccessKeyId;
 
 /** OSS访问密钥Secret，阿里云对象存储服务的访问密钥 */
 private String ossAccessKeySecret;

     
    /**
     * 用于admin框架的几个参数，只用列表无需配置  end----------------
     */
    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public boolean isSqlCache() {
        return sqlCache;
    }

    /** 系统启动状态标识，用于防止重复初始化 */
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

    public String getPrefixUrl() {
        return prefixUrl;
    }

    public void setPrefixUrl(String prefixUrl) {
        this.prefixUrl = prefixUrl;
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

    public void setSqlCache(boolean aBoolean) {
        this.sqlCache=aBoolean;
    }
}
