package top.ezadmin.annotations;

import org.springframework.context.annotation.Import;
import top.ezadmin.spring.EzClientAutoConfiguration;
import top.ezadmin.spring.EzadminEnabledMarker;

import java.lang.annotation.*;

/**
 * 启用 EzAdmin 自动配置的注解
 * 在 Spring Boot 主启动类上添加此注解可自动加载 EzAdmin 配置
 *
 * 此注解会导入两个配置类：
 * 1. EzadminEnabledMarker - 标记类，用于标识 EzAdmin 已启用
 * 2. EzClientAutoConfiguration - 主配置类，依赖标记类条件加载
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({EzadminEnabledMarker.class, EzClientAutoConfiguration.class})
public @interface EnableEzadmin {
}