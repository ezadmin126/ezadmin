package top.ezadmin.web;

import org.springframework.context.ApplicationContext;


public class SpringContextHolder   {
    private static ApplicationContext applicationContext = null;
    public static void init(ApplicationContext applicationContext1){
          applicationContext=applicationContext1;
    }
    public static <T> T getBean(String name) {
        return (T) applicationContext.getBean(name);
    }
    public static <T> T getBean(Class<T> requiredType) {
        return applicationContext.getBean(requiredType);
    }
}
