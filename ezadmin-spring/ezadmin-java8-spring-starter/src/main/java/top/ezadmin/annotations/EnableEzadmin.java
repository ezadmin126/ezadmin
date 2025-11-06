package top.ezadmin.annotations;

import org.springframework.context.annotation.Import;
import top.ezadmin.spring.EzClientAutoConfiguration;

import java.lang.annotation.*;

/**
 * 启用 EzAdmin 自动配置的注解
 * 在 Spring Boot 主启动类上添加此注解可自动加载 EzAdmin 配置
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(EzClientAutoConfiguration.class)
public @interface EnableEzadmin {
}