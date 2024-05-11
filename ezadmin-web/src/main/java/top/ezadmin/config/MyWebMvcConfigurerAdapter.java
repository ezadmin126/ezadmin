package top.ezadmin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.concurrent.TimeUnit;

@Configuration
public class MyWebMvcConfigurerAdapter extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/")
                .setCacheControl( CacheControl.maxAge(10, TimeUnit.DAYS).cachePublic() );
//        registry.addResourceHandler("/common/**")
//                .addResourceLocations("classpath:/static/common/").setCacheControl( CacheControl.maxAge(10, TimeUnit.DAYS).cachePublic() );
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/").setCacheControl(
                        CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic() )
                . setCachePeriod( 3600 * 240 );
    }
}

