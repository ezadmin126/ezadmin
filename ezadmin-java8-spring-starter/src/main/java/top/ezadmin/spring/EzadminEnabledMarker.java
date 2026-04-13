package top.ezadmin.spring;

import org.springframework.context.annotation.Configuration;

/**
 * EzAdmin 启用标记
 * 用于标识已启用 @EnableEzadmin 注解
 * 当使用 @EnableEzadmin 注解时，此配置类会被导入到 Spring 容器中
 * EzClientAutoConfiguration 通过 @ConditionalOnBean 检查此标记类是否存在来决定是否加载
 */
@Configuration
public class EzadminEnabledMarker {
}
