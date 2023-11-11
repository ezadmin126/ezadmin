/**
 * Copyright
 */

package com.ezadmin;

import com.ezadmin.dao.FormDao;
import com.ezadmin.dao.ListDao;
import com.ezadmin.common.enums.ContentTypeEnum;
import com.ezadmin.common.utils.*;
import com.ezadmin.dao.PluginsDao;
import com.ezadmin.plugins.cache.Callback;
import com.ezadmin.plugins.cache.EzCache;
import com.ezadmin.plugins.export.EzExport;
import com.ezadmin.plugins.sqlog.EzSqlogDataSource;
import com.ezadmin.web.Config;
import com.ezadmin.web.filters.ez.ControllerFilter;
import com.ezadmin.web.filters.ez.Filter;
import com.ezadmin.web.filters.ez.NotFoundFilter;
import com.ezadmin.web.filters.ez.TraceLogFilter;

 import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.File;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import static java.nio.file.StandardWatchEventKinds.*;
///ezlist/
public class EzBootstrap {

    public static final Logger log = LoggerFactory.getLogger(EzBootstrap.class);


    private boolean templateCache = false;
    private boolean sqlCache = false;
    private List<Config> listConfigResources;
    private List<Config> formConfigResources;
    private List<Config> pluginsFormConfigResources;
    private List<Config> pluginsListConfigResources;
    private List<Config> pluginsDetailConfigResources;

   private Callback reload;
    private String clearUrl;

    private EzSqlogDataSource datasource;

    private EzCache cache;

    private Map<String, EzSqlogDataSource> queryDatasourceMap = new HashMap<>();
    public final static String DEFAULT_DB_KEY="System";
    private String appName = "ez";
    private String adminStyle="layui";
    private String logType = "1000";
    private String regionUrl,categoryUrl,orgUrl;
    private String editLocation;
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

    private String exportClass="com.ezadmin.plugins.export.CSVExport";
    private String cacheClass="com.ezadmin.plugins.cache.CaffeineCache";

    private EzExport ezExport;

    private String holiday="[[],[]]";

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

   // Config config = new Config();

    //参考这个controller 覆盖
    private String uploadUrl = "/ezadmin/form/upload.html";
    private String downloadUrl = "/ezadmin/form/download.html?fileId=";



   // public Config getConfig() {
   //     return config;
   // }


    private static EzBootstrap bootstrap = new EzBootstrap();

    private EzBootstrap() {
    }

    public static EzBootstrap instance() {
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
    public EzSqlogDataSource getOriginDataSource ( ){
        return queryDatasourceMap.get("datasource");
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
                log.info("start init ezadmin plugins ");
                PluginsDao.getInstance().init();
                log.info("start init ezadmin list ");
                ListDao.getInstance().init();
                log.info("start init ezadmin form ");
                FormDao.getInstance().init();
                log.info("end init ezadmin   ");

                ThymeleafUtils.init(sqlCache);

                filter = new TraceLogFilter();
                filter.next(new ControllerFilter()).next(new NotFoundFilter());
                //初始化导出 工具
                ezExport = (EzExport) BeanTools.applicationInstance(getExportClass());
                cache = (EzCache) BeanTools.applicationInstance(getCacheClass());
                getCache().cacheFlag(sqlCache);
                if (StringUtils.isNotBlank(getEditLocation())) {
                    //初始化文件监听list
                    new Thread() {
                        public void run() {

                            try {
                                Path dir = Paths.get(getEditLocation() + File.separator + "list");
                                if (Files.notExists(dir)) {
                                    log.warn(dir + " 不存在，无法监听文件更改");
                                    return;
                                }
                                WatchService watcher = FileSystems.getDefault().newWatchService();
                                // 注册要监听的事件
                                dir.register(watcher, ENTRY_CREATE, ENTRY_MODIFY);
                                //二级目录添加监听事件
                                for (File childFold : dir.toFile().listFiles()) {
                                    if (childFold.isDirectory()) {
                                        childFold.toPath().register(watcher, ENTRY_CREATE, ENTRY_MODIFY);
                                    }
                                }
                                while (true) {
                                    try {
                                        WatchKey key = watcher.take();
                                        String currentDir = key.watchable().toString();
                                        for (WatchEvent<?> event : key.pollEvents()) {
                                            WatchEvent.Kind<?> kind = event.kind();
                                            // 处理不同种类的事件
                                            if (kind == ENTRY_CREATE) {
                                                // 处理文件创建事件
                                                if (event.context().toString().endsWith(".html")) {
                                                    ListDao.getInstance().loadListFile(new File(currentDir + File.separator +
                                                            event.context().toString()
                                                    ));
                                                    log.info("文件 " + currentDir + File.separator + event.context().toString() + " 被创建了。");
                                                }
                                            } else if (kind == ENTRY_MODIFY) {
                                                // 处理文件修改事件
                                                if (event.context().toString().endsWith(".html")) {
                                                    ListDao.getInstance().loadListFile(new File(currentDir + File.separator +
                                                            event.context().toString()
                                                    ));
                                                    log.info("文件 " + currentDir + File.separator + event.context().toString() + " 被修改了。");
                                                }
                                            }
                                        }
                                        // 重置 WatchKey
                                        boolean valid = key.reset();
                                        if (!valid) {
                                            break;
                                        }
                                    } catch (Exception e) {
                                        log.error("", e);
                                    }
                                }
                            } catch (Exception e) {
                                log.error("", e);
                            }
                        }
                    }.start();
                    //初始化文件监听form
                    new Thread() {
                        public void run() {

                            try {
                                Path dir = Paths.get(getEditLocation() + File.separator + "form");
                                if (Files.notExists(dir)) {
                                    log.warn(dir + " 不存在，无法监听文件更改");
                                    return;
                                }

                                WatchService watcher = FileSystems.getDefault().newWatchService();
                                // 注册要监听的事件
                                dir.register(watcher, ENTRY_CREATE, ENTRY_MODIFY);
                                //二级目录添加监听事件
                                for (File childFold : dir.toFile().listFiles()) {
                                    if (childFold.isDirectory()) {
                                        childFold.toPath().register(watcher, ENTRY_CREATE, ENTRY_MODIFY);
                                    }
                                }
                                while (true) {
                                    WatchKey key;
                                    try {
                                        // 获取要处理的 WatchKey
                                        key = watcher.take();
                                    } catch (InterruptedException ex) {
                                        return;
                                    }
                                    String currentDir = key.watchable().toString();

                                    for (WatchEvent<?> event : key.pollEvents()) {
                                        WatchEvent.Kind<?> kind = event.kind();
                                        // 处理不同种类的事件
                                        if (kind == ENTRY_CREATE) {
                                            // 处理文件创建事件
                                            if (event.context().toString().endsWith(".html")) {
                                                FormDao.getInstance().loadFormFile(new File(
                                                        currentDir + File.separator +
                                                                event.context().toString()
                                                ));
                                                log.info("文件  " + currentDir + File.separator +
                                                        event.context().toString() + " 被创建了。");
                                            }
                                        } else if (kind == ENTRY_MODIFY) {
                                            // 处理文件修改事件
                                            if (event.context().toString().endsWith(".html")) {
                                                FormDao.getInstance().loadFormFile(new File(
                                                        currentDir + File.separator +
                                                                event.context().toString()
                                                ));
                                                log.info("文件  " + currentDir + File.separator +
                                                        event.context().toString() + " 被修改了。");
                                            }

                                        }
                                    }
                                    // 重置 WatchKey
                                    boolean valid = key.reset();
                                    if (!valid) {
                                        break;
                                    }
                                }
                            } catch (Exception e) {
                                log.error("", e);
                            }
                        }
                    }.start();

                }


            }
        }catch (Exception e){
            //
            log.error("start init ezadmin  ",e);
        }
    }
    static Filter filter;


    public void doFilter(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
//            if(!sqlCache){
//                getReload().call("");
//                MyCache.getInstance().clear();
//            }
            request.setCharacterEncoding("UTF-8");
            String originatingUrl = request.getRequestURI();
            response.setCharacterEncoding("UTF-8");
            response.setContentType(ContentTypeEnum.loadContentTypeByUrl(originatingUrl));

            filter.doFilter(request, response);
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

    public boolean isTemplateCache() {
        return templateCache;
    }

    public void setTemplateCache(boolean templateCache) {
        this.templateCache = templateCache;
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

    public Callback getReload() {
        return reload;
    }

    public void setReload(Callback reload) {
        this.reload = reload;
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

    public String getEditLocation() {
        return editLocation;
    }

    public void setEditLocation(String editLocation) {
        this.editLocation = editLocation;
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

    public EzCache getCache() {
        return cache;
    }

    public void setCache(EzCache cache) {
        this.cache = cache;
    }

    public String getCacheClass() {
        return cacheClass;
    }

    public void setCacheClass(String cacheClass) {
        this.cacheClass = cacheClass;
    }
}
