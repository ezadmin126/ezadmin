package top.ezadmin.config;

import com.alibaba.druid.pool.DruidDataSource;

import org.apache.commons.lang.StringUtils;
import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import top.ezadmin.common.utils.JsoupUtil;
import top.ezadmin.plugins.sqlog.EzSqlogDataSource;


import javax.sql.DataSource;

@Configuration
public class DatasourceConfig {
	private Logger logger = LoggerFactory.getLogger(DatasourceConfig.class.getName());
	@Value("${spring.datasource.druid.db1.driver-class-name}")
	private String driver1 ;
	@Value("${spring.datasource.druid.db2.driver-class-name}")
	private String driver2 ;
	@Value("${spring.datasource.druid.db3.driver-class-name}")
	private String driver3 ;

	@Value("${spring.datasource.druid.db1.url}")
	private String db1Url;
	@Value("${spring.datasource.druid.db1.username}")
	private String db1username;
	@Value("${spring.datasource.druid.db1.password}")
	private String db1password;

	@Value("${spring.datasource.druid.db1.maxActive}")
	private int db1maxActive = 20;
	@Value("${spring.datasource.druid.db1.minIdle}")
	private int db1minIdle = 2;
	@Value("${spring.datasource.druid.db1.initialSize}")
	private int db1initialSize = 2;
	@Value("${ezadmin.logType:0000-10000}")
	private String logType;

	private String validationQuery = "select 1";
	private boolean testOnBorrow = true;

	@Value("${h2Server:}")
	private String h2Server;
	@Bean(name="h2server")
	public Server h2server() {
		try {
			org.h2.Driver.load();
			Server server =new Server();
			if(StringUtils.isBlank(h2Server)){
				return server;
			}
			String baseDir= JsoupUtil.projectRootPath().substring(0,JsoupUtil.projectRootPath().indexOf("target"));
			System.out.println("basedir:"+baseDir);
			if(h2Server.endsWith(",")){
				server.runTool((h2Server+ baseDir).split(","));
			}else{
				server.runTool((h2Server).split(","));
			}
			return server;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Bean(name = "dataSource")
	@DependsOn("h2server")
	public DataSource gomanager () {
		DruidDataSource dds=new DruidDataSource();// DruidDataSourceFactory.createDataSource(propsTemp);
		dds.setDriverClassName(driver1);
		dds.setUrl(db1Url);
		dds.setUsername(db1username);
		dds.setPassword(db1password);
		dds.setValidationQuery(validationQuery);
		dds.setMaxActive(db1maxActive);
		dds.setMinIdle(db1minIdle);
		dds.setInitialSize(db1initialSize);
		dds.setTestOnBorrow(testOnBorrow);

		EzSqlogDataSource ezSqlogDataSource=new EzSqlogDataSource();
		ezSqlogDataSource.setRealDataSource(dds);
		ezSqlogDataSource.setLogType(logType);
		//ezSqlogDataSource.setCallback(ezSqlogCallBackERP);
		return ezSqlogDataSource;
	}




	@Value("${spring.datasource.druid.db2.url}")
	private String db2Url;
	@Value("${spring.datasource.druid.db2.username}")
	private String db2username;
	@Value("${spring.datasource.druid.db2.password}")
	private String db2password;

	@Bean(name = "erp-dataSourceTarget")
	@DependsOn("h2server")
	public DataSource erp() {
		DruidDataSource dds=new DruidDataSource();// DruidDataSourceFactory.createDataSource(propsTemp);
		dds.setDriverClassName(driver2);
		dds.setUrl(db2Url);
		dds.setUsername(db2username);
		dds.setPassword(db2password);
		dds.setValidationQuery(validationQuery);
		dds.setMaxActive(db1maxActive);
		dds.setMinIdle(db1minIdle);
		dds.setInitialSize(db1initialSize);
		dds.setTestOnBorrow(testOnBorrow);
		EzSqlogDataSource ezSqlogDataSource=new EzSqlogDataSource();
		ezSqlogDataSource.setRealDataSource(dds);
		ezSqlogDataSource.setLogType(logType);
		//ezSqlogDataSource.setCallback(ezSqlogCallBackERP);
		return ezSqlogDataSource;
	}
	@Value("${spring.datasource.druid.db3.url}")
	private String db3Url;
	@Value("${spring.datasource.druid.db3.username}")
	private String db3username;
	@Value("${spring.datasource.druid.db3.password}")
	private String db3password;

	@Bean(name = "ez-dataSource")
	@DependsOn("h2server")
	public DataSource ez() {
		DruidDataSource dds=new DruidDataSource();// DruidDataSourceFactory.createDataSource(propsTemp);
		dds.setDriverClassName(driver3);
		dds.setUrl(db3Url);
		dds.setUsername(db3username);
		dds.setPassword(db3password);
		dds.setValidationQuery(validationQuery);
		dds.setMaxActive(db1maxActive);
		dds.setMinIdle(db1minIdle);
		dds.setInitialSize(db1initialSize);
		dds.setTestOnBorrow(testOnBorrow);
		EzSqlogDataSource ezSqlogDataSource=new EzSqlogDataSource();
		ezSqlogDataSource.setRealDataSource(dds);
		ezSqlogDataSource.setLogType(logType);
		return ezSqlogDataSource;
	}


}
