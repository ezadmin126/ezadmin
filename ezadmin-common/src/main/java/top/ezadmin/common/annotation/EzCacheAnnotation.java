package top.ezadmin.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 缓存注解
 * 用于标记需要缓存的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EzCacheAnnotation {

    /**
     * 缓存键名
     */
    String value() default "";

    /**
     * 缓存过期时间（秒）
     */
    int expire() default 3600;

    /**
     * 是否启用缓存
     */
    boolean enabled() default true;

    /**
     * 缓存条件表达式
     */
    String condition() default "";
}
