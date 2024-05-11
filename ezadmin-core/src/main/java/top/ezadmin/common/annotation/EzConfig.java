package top.ezadmin.common.annotation;


import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EzConfig {
    boolean required() default true;
}