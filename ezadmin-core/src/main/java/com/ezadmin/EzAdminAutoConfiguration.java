package com.ezadmin;


import com.ezadmin.common.utils.JSONUtils;
import com.ezadmin.common.utils.StringUtils;
import com.ezadmin.plugins.cache.Callback;
import com.ezadmin.plugins.express.AbstractOperator;
import com.ezadmin.plugins.express.executor.EzExpressExecutor;
import com.ezadmin.web.SpringBeanOperator;
import com.ezadmin.web.SpringContextHolder;


import com.ezadmin.web.filters.EzAdminBootFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Configuration
@ConditionalOnClass(EzAdminAutoConfiguration.class)
@EnableConfigurationProperties({EzAdminProperties.class})
public class EzAdminAutoConfiguration implements ApplicationContextAware {
    Logger logger = LoggerFactory.getLogger(EzAdminAutoConfiguration.class);

    @Autowired
    EzAdminProperties properties;

    @Bean
    public EzBootstrap ezBootstrap() {
        EzBootstrap ezBootstrap = EzBootstrap.instance();
        String configJson=properties.getConfigJson();
        Map<String, Object> configMap= JSONUtils.parseObjectMap(configJson);
        ezBootstrap.setConfig(configMap);
        ezBootstrap.setAppName(properties.getAppName());
        ezBootstrap.setSqlCache(StringUtils.toBoolean(properties.isCacheFlag()+""));
        if(StringUtils.isNotBlank(properties.getUploadUrl())){
            ezBootstrap.setUploadUrl(properties.getUploadUrl());
        }
        if(StringUtils.isNotBlank(properties.getDownloadUrl())){
            ezBootstrap.setDownloadUrl(properties.getDownloadUrl());
        }
        ezBootstrap.setLogType(properties.getLogType());
        ezBootstrap.setRegionUrl(properties.getRegionUrl());
        ezBootstrap.setCategoryUrl(properties.getCategoryUrl());
        ezBootstrap.setOrgUrl(properties.getOrgUrl());
        ezBootstrap.setSystemName(properties.getSystemName());
        ezBootstrap.setNavUrl(properties.getNavUrl());
        ezBootstrap.setLogoUrl(properties.getLogoUrl());
        ezBootstrap.setSearchUrl(properties.getSearchUrl());
        ezBootstrap.setAppendJs(properties.getAppendJs());
        ezBootstrap.setIndexUrl(properties.getIndexUrl());
        ezBootstrap.setSignoutUrl(properties.getSignoutUrl());
        ezBootstrap.setMessageUrl(properties.getMessageUrl());

        ezBootstrap.setChatUrl(properties.getChatUrl());
        if(StringUtils.isNotBlank(properties.getUploadPath())){
            ezBootstrap.setUploadPath(properties.getUploadPath());
        }

        ezBootstrap.setChatUrl(properties.getChatUrl());
        ezBootstrap.setAdminStyle(properties.getAdminStyle());

        if(StringUtils.isNotBlank(properties.getEditLocation())){
            ezBootstrap.setEditLocation(properties.getEditLocation());
            properties.setListResourceLocation(properties.getListResourceLocation()+";"+properties.getEditLocation()+File.separator+"list");
            properties.setFormResourceLocation(properties.getFormResourceLocation()+";"+properties.getEditLocation()+File.separator+"form");
        }

        if (StringUtils.isNotBlank(properties.getExportClass())) {
            ezBootstrap.setExportClass(properties.getExportClass());
        }
        if (StringUtils.isNotBlank(properties.getHoliday())) {
            ezBootstrap.setHoliday(properties.getHoliday());
        }
        if (StringUtils.isBlank(properties.getDatasourceBeanNames())) {
            properties.setDatasourceBeanNames("dataSource,"+properties.getDatasourceBeanNames());//springboot 默认值
        }

        String beanNames[] = org.apache.commons.lang.StringUtils.split(properties.getDatasourceBeanNames(), ",");
        for (int i = 0; i < beanNames.length; i++) {
            DataSource dataSource = (DataSource) applicationContext.getBean(beanNames[i]);
            if (dataSource == null) {
                logger.error(" can not find datasource with bean name :" + beanNames[i]);
                throw new IllegalArgumentException(" can not find datasource with bean name :" + beanNames[i]);
            }
            ezBootstrap.addBizDataSource(beanNames[i], dataSource);


        }
//        try {
//            if (properties.getEzDataSource() == null) {
//                properties.setEzDataSource((DataSource) applicationContext.getBean("dataSource"));
//            }
//        }catch (Exception e){}
//        ezBootstrap.addOriginDataSource(properties.getEzDataSource());

        try {
            logger.info(" ezadmin 开始初始化列表");
            ezBootstrap.setListConfigResources(ConfigUtils.loadFiles(properties.getListResourceLocation()));
            logger.info(" ezadmin 开始初始化列表插件{}");
            ezBootstrap.setPluginsListConfigResources(ConfigUtils.loadFiles(properties.getPluginsListResourceLocation()));
            logger.info(" ezadmin 开始初始化表单");
            ezBootstrap.setFormConfigResources(ConfigUtils.loadFiles(properties.getFormResourceLocation()));
            logger.info(" ezadmin 开始初始化表单插件");
            ezBootstrap.setPluginsFormConfigResources(ConfigUtils.loadFiles(properties.getPluginsFormResourceLocation()));
            ezBootstrap.setPluginsDetailConfigResources(ConfigUtils.loadFiles(properties.getPluginsDetailResourceLocation()));
            ezBootstrap.init();
            ezBootstrap.setReload(new Callback() {
                @Override
                public Object call(String key) {
                    try {
                        logger.info(" ezadmin 开始初始化列表");
                        EzBootstrap.instance().setListConfigResources(ConfigUtils.loadFiles(properties.getListResourceLocation()));
                        logger.info(" ezadmin 开始初始化列表插件");
                        EzBootstrap.instance().setPluginsListConfigResources(ConfigUtils.loadFiles(properties.getPluginsListResourceLocation()));
                        logger.info(" ezadmin 开始初始化表单");
                        EzBootstrap.instance().setFormConfigResources(ConfigUtils.loadFiles(properties.getFormResourceLocation()));
                        logger.info(" ezadmin 开始初始化表单插件");
                        EzBootstrap.instance().setPluginsFormConfigResources(ConfigUtils.loadFiles(properties.getPluginsFormResourceLocation()));
                        logger.info(" ezadmin 开始初始化详情页插件");
                        EzBootstrap.instance().setPluginsDetailConfigResources(ConfigUtils.loadFiles(properties.getPluginsDetailResourceLocation()));
                    }catch (Exception e){
                        //
                    }
                    return null;
                }
            });
        } catch (Exception throwables) {
            logger.error(" ezadmin 初始化异常", throwables);
             throw new RuntimeException(throwables);
        }
        return ezBootstrap;
    }



    @Bean
    public FilterRegistrationBean  filterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();

        EzAdminBootFilter customURLFilter = new EzAdminBootFilter();
        customURLFilter.setEzBootstrap(ezBootstrap());
        registrationBean.setFilter(customURLFilter);
        registrationBean.addUrlPatterns("/ezadmin/*");
        registrationBean.setOrder(66);

        return registrationBean;
    }

    private ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        SpringContextHolder.init(applicationContext);
        EzExpressExecutor.initSpringFunction(new SpringBeanOperator());
    }
}