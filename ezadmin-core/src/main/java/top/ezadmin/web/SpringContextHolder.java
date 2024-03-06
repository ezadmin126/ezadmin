package top.ezadmin.web;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringContextHolder implements ApplicationContextAware {
    private static ApplicationContext applicationContext = null;
    public static void init(ApplicationContext applicationContext1){
          applicationContext=applicationContext1;
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextHolder.applicationContext = applicationContext;
    }
    public static <T> T getBean(String name) {
        return (T) applicationContext.getBean(name);
    }
    public static <T> T getBean(Class<T> requiredType) {
        return applicationContext.getBean(requiredType);
    }
}
