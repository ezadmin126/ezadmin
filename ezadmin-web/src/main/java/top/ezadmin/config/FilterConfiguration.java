package top.ezadmin.config;


import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.mobile.device.DeviceResolverRequestFilter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.unit.DataSize;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.ezadmin.EzClientBootstrap;
import top.ezadmin.EzClientProperties;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class FilterConfiguration implements WebMvcConfigurer {
	@Resource
	DataSource datasource;
	@Resource
	EzClientProperties properties;

	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(this.getClass());
	}

	@Bean
	public Filter utf8(){
		return new CharacterEncodingFilter();
	}

	@Bean
	public MultipartConfigElement multipartConfigElement() {
		MultipartConfigFactory config = new MultipartConfigFactory();
		config.setMaxFileSize(DataSize.ofMegabytes(250));
		config.setMaxRequestSize(DataSize.ofMegabytes(280));
		config.setLocation(properties.getUploadPath()+"/temp");
		return config.createMultipartConfig();
	}

	@Bean
	public PlatformTransactionManager transactionManager1() {
		return new DataSourceTransactionManager(datasource);
	}



	@Bean
	public  UserAccessFilter  uaf(){
		UserAccessFilter uac=new UserAccessFilter();
		List<String> staticUrl = getStaticUrl();

		List<String> exeUrl = new ArrayList<>();
        exeUrl.add("/topezadmin/list/count-**");
        exeUrl.add("/topezadmin/list/api-**");
        exeUrl.add("/topezadmin/list/tree-**");
        exeUrl.add("/topezadmin/list/treedata-**");
        exeUrl.add("/topezadmin/list/trace-**");
        exeUrl.add("/topezadmin/index.html");
        exeUrl.add("/topezadmin/list/selectCols-**");
        exeUrl.add("/topezadmin/list/doOrder-**");
        exeUrl.add("/ezadmin/index.html");
        exeUrl.add("/ezadmin/dark.html");
		exeUrl.add("/");
		exeUrl.add("/blog/**");
		exeUrl.add("**/static/**");
		exeUrl.add("/camunda/**");
        uac.setStaticUrl(staticUrl);
        uac.setExcludeUrl(exeUrl);
        return uac;
    }

	private static List<String> getStaticUrl() {
		List<String> staticUrl=new ArrayList<String>();
		staticUrl.add("/webjars/**");
		staticUrl.add("/api/**");
		staticUrl.add("**/static/**");

		staticUrl.add("/robots.txt");
		staticUrl.add("/favicon.ico");
		staticUrl.add("/ws/**");
		staticUrl.add("/error/**");
		staticUrl.add("/laynavs**");
		staticUrl.add("/login/**");
		staticUrl.add("/core/**");
		staticUrl.add("/message/**");
		staticUrl.add("/system/**");
		staticUrl.add("/sitemap**");
		staticUrl.add("/home/index");

		return staticUrl;
	}

	@Bean
	public FilterRegistrationBean<UserAccessFilter> UserAccessFilter() throws Exception {
		FilterRegistrationBean<UserAccessFilter>  registrationBean = new FilterRegistrationBean<UserAccessFilter>();

 		registrationBean.setFilter(uaf());
		registrationBean.addUrlPatterns("/*");
		registrationBean.setOrder(0);
		return registrationBean;
	}

	@Bean("deviceResolverRequestFilter")
	public FilterRegistrationBean<DeviceResolverRequestFilter> deviceResolverRequestFilter() {
		FilterRegistrationBean<DeviceResolverRequestFilter> registrationBean = new FilterRegistrationBean<DeviceResolverRequestFilter>();
		DeviceResolverRequestFilter customURLFilter = new DeviceResolverRequestFilter( );
		registrationBean.setFilter(customURLFilter);
		registrationBean.addUrlPatterns("/*");
		registrationBean.setOrder(67);
		return registrationBean;
	}

}
