package top.ezadmin;


import top.ezadmin.common.utils.JSONUtils;
import top.ezadmin.common.utils.StringUtils;
import top.ezadmin.plugins.express.executor.EzExpressExecutor;
import top.ezadmin.web.SpringBeanOperator;
import top.ezadmin.web.SpringContextHolder;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Map;


@Configuration
@ConditionalOnClass(EzClientAutoConfiguration.class)
@EnableConfigurationProperties({EzClientProperties.class})
public class EzClientAutoConfiguration implements ApplicationContextAware {
    Logger logger = LoggerFactory.getLogger(EzClientAutoConfiguration.class);

    @Autowired
    EzClientProperties ezClientProperties;

    @Bean
    public EzClientBootstrap ezClientBootstrap() {
        EzClientBootstrap ezBootstrap = EzClientBootstrap.instance();
        String configJson= ezClientProperties.getConfigJson();
        Map<String, Object> configMap= JSONUtils.parseObjectMap(configJson);
        ezBootstrap.setConfig(configMap);
        ezBootstrap.setAppName(ezClientProperties.getAppName());
        ezBootstrap.setSqlCache(StringUtils.toBoolean(ezClientProperties.isCacheFlag()+""));

        ezBootstrap.setUploadUrl(ezClientProperties.getUploadUrl());
        ezBootstrap.setDownloadUrl(ezClientProperties.getDownloadUrl());

        ezBootstrap.setLogType(ezClientProperties.getLogType());
        ezBootstrap.setRegionUrl(ezClientProperties.getRegionUrl());
        ezBootstrap.setCategoryUrl(ezClientProperties.getCategoryUrl());
        ezBootstrap.setOrgUrl(ezClientProperties.getOrgUrl());
        ezBootstrap.setSystemName(ezClientProperties.getSystemName());
        ezBootstrap.setNavUrl(ezClientProperties.getNavUrl());
        ezBootstrap.setLogoUrl(ezClientProperties.getLogoUrl());
        ezBootstrap.setSearchUrl(ezClientProperties.getSearchUrl());
        ezBootstrap.setAppendJs(ezClientProperties.getAppendJs());
        ezBootstrap.setIndexUrl(ezClientProperties.getIndexUrl());
        ezBootstrap.setSignoutUrl(ezClientProperties.getSignoutUrl());
        ezBootstrap.setMessageUrl(ezClientProperties.getMessageUrl());
        ezBootstrap.setClearUrl(ezClientProperties.getClearUrl());
        ezBootstrap.setPrefixUrl(ezClientProperties.getPrefixUrl());
        ezBootstrap.setChatUrl(ezClientProperties.getChatUrl());
        if(StringUtils.isNotBlank(ezClientProperties.getUploadPath())){
            ezBootstrap.setUploadPath(ezClientProperties.getUploadPath());
        }

        ezBootstrap.setChatUrl(ezClientProperties.getChatUrl());
        ezBootstrap.setAdminStyle(ezClientProperties.getAdminStyle());
        //布局 默认container
        ezBootstrap.setLayout(ezClientProperties.getLayout());

        if (StringUtils.isNotBlank(ezClientProperties.getExportClass())) {
            ezBootstrap.setExportClass(ezClientProperties.getExportClass());
        }
        if (StringUtils.isNotBlank(ezClientProperties.getHoliday())) {
            ezBootstrap.setHoliday(ezClientProperties.getHoliday());
        }
        if (StringUtils.isNotBlank(ezClientProperties.getRefreshClass())) {
            ezBootstrap.setRefreshClass(ezClientProperties.getRefreshClass());
        }
        if (StringUtils.isBlank(ezClientProperties.getDatasourceBeanNames())) {
            ezClientProperties.setDatasourceBeanNames("dataSource,"+ ezClientProperties.getDatasourceBeanNames());//springboot 默认值
        }
        String beanNames[] = org.apache.commons.lang.StringUtils.split(ezClientProperties.getDatasourceBeanNames(), ",");
        for (int i = 0; i < beanNames.length; i++) {
            DataSource dataSource = (DataSource) applicationContext.getBean(beanNames[i]);
            if (dataSource == null) {
                logger.error(" can not find datasource with bean name :" + beanNames[i]);
                throw new IllegalArgumentException(" can not find datasource with bean name :" + beanNames[i]);
            }
            ezBootstrap.addBizDataSource(beanNames[i], dataSource);
        }
        try {
           // logger.info("   开始初始化列表"+ ezClientProperties.getListResourceLocation());
            ezBootstrap.setListConfigResources(ConfigUtils.loadFiles(ezClientProperties.getListResourceLocation()+";"+listResourceLocation));
          //  logger.info("   开始初始化列表插件{}"+ ezClientProperties.getPluginsListResourceLocation());
            ezBootstrap.setPluginsListConfigResources(ConfigUtils.loadFiles(ezClientProperties.getPluginsListResourceLocation()+";"+pluginsListResourceLocation));
          //  logger.info("   开始初始化表单"+ ezClientProperties.getFormResourceLocation());
            ezBootstrap.setFormConfigResources(ConfigUtils.loadFiles(ezClientProperties.getFormResourceLocation()+";"+formResourceLocation));
          //  logger.info("   开始初始化表单插件"+ ezClientProperties.getPluginsFormResourceLocation());
            ezBootstrap.setPluginsFormConfigResources(ConfigUtils.loadFiles(ezClientProperties.getPluginsFormResourceLocation()+";"+pluginsFormResourceLocation));
            ezBootstrap.setPluginsDetailConfigResources(ConfigUtils.loadFiles(ezClientProperties.getPluginsDetailResourceLocation()+";"+pluginsDetailResourceLocation));
            ezBootstrap.init();

        } catch (Exception throwables) {
            logger.error("   初始化异常", throwables);
             throw new RuntimeException(throwables);
        }
        return ezBootstrap;
    }
    private String listResourceLocation="classpath*:/topezadmin/config/layui/list/**/*.html";
    private String formResourceLocation="classpath*:/topezadmin/config/layui/form/**/*.html";
    private String pluginsFormResourceLocation="classpath*:/topezadmin/config/layui/plugins/form/**/*.html";
    private String pluginsListResourceLocation="classpath*:/topezadmin/config/layui/plugins/list/**/*.html";
    private String pluginsDetailResourceLocation="classpath*:/topezadmin/config/layui/plugins/detail/**/*.html";


    @Bean
    public FilterRegistrationBean  ezClientFilterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        top.ezadmin.web.filters.EzClientServletFilter customURLFilter = new top.ezadmin.web.filters.EzClientServletFilter();
        customURLFilter.setEzBootstrap(ezClientBootstrap());
        registrationBean.setFilter(customURLFilter);
        registrationBean.getUrlPatterns().add("/topezadmin/*");
        try {
            if (!StringUtils.startsWith(ezClientProperties.getIndexUrl(), "/ezadmin") && StringUtils.isNotBlank(ezClientProperties.getIndexUrl())) {
                registrationBean.getUrlPatterns().add("/ezadmin/*");
                registrationBean.getUrlPatterns().add("/ezcloud/*");
                registrationBean.getUrlPatterns().add(ezClientProperties.getIndexUrl().substring(0, ezClientProperties.getIndexUrl().indexOf("/", 1)) + "/*");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        registrationBean.setOrder(65);
        return registrationBean;
    }

    private ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        SpringContextHolder.init(applicationContext);
        EzExpressExecutor.initSpringFunction(new SpringBeanOperator());
    }
}