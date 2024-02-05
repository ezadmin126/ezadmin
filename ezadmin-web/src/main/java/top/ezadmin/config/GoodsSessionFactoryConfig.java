package top.ezadmin.config;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
public class GoodsSessionFactoryConfig {

    /**
     * mybatis 配置路径
     */
    private static String MYBATIS_CONFIG_GOODS = "mybatis-config-goods.xml";

    @Autowired
    @Qualifier("dataSource")
    private DataSource datasource;
    @Bean(name = "goodsSqlSessionFactory")
    public SqlSessionFactoryBean createSqlSessionFactoryBean() throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setConfigLocation(new ClassPathResource(MYBATIS_CONFIG_GOODS));
        sqlSessionFactoryBean.setDataSource(datasource);
        //添加XML目录
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath:top/ezadmin/domain/mapper/*Mapper.xml"));
        return sqlSessionFactoryBean;
    }

}