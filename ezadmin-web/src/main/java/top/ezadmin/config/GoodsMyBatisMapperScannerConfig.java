package top.ezadmin.config;

import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter(GoodsSessionFactoryConfig.class)
public class GoodsMyBatisMapperScannerConfig {
 @Bean(name="scann1")
 public MapperScannerConfigurer mapperScannerConfigurer() {
     MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
     mapperScannerConfigurer.setSqlSessionFactoryBeanName("goodsSqlSessionFactory");
     mapperScannerConfigurer.setBasePackage("top.ezadmin.damain.mapper");
    return mapperScannerConfigurer;
  }

} 