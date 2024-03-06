package top.ezadmin.common.annotation;


import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EzMapping {
    String name() default "";

    String  value() default "/";

}