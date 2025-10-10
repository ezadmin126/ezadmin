package top.ezadmin.spring;

import top.ezadmin.plugins.ImportOperator;
import top.ezadmin.plugins.SpringBeanOperator;
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
import top.ezadmin.common.utils.StringUtils;
import top.ezadmin.plugins.express.executor.EzExpressExecutor;

@Configuration
@ConditionalOnClass(EzClientAutoConfiguration.class)
@EnableConfigurationProperties({EzClientProperties.class})
public class EzClientAutoConfiguration implements ApplicationContextAware {
    Logger logger = LoggerFactory.getLogger(EzClientAutoConfiguration.class);

    @Autowired
    EzClientProperties ezClientProperties;

    @Bean
    public FilterRegistrationBean ezClientFilterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        EzClientServletFilter ezClientServletFilter = new  EzClientServletFilter();
        ezClientServletFilter.setEzClientProperties(ezClientProperties);
        registrationBean.setFilter(ezClientServletFilter);
        registrationBean.getUrlPatterns().add("/topezadmin/*");
        try {
            //自定义URL前缀
            if (StringUtils.isNotBlank(ezClientProperties.getPrefixUrl())) {
                registrationBean.getUrlPatterns().add("/ezadmin/*");
                registrationBean.getUrlPatterns().add(ezClientProperties.getPrefixUrl() + "/*");
            }
        } catch (Exception e) {
            logger.error("初始化ezClientServletFilter异常", e);
        }
        registrationBean.setOrder(65);
        return registrationBean;
    }

    private ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        SpringContextHolder.init(applicationContext);
        EzExpressExecutor.addExtendFunction("spring",new SpringBeanOperator());
        EzExpressExecutor.addExtendFunction("imports",new ImportOperator());
    }
}