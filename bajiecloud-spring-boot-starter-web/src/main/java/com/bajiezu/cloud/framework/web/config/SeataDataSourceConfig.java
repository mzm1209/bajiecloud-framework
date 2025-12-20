package com.bajiezu.cloud.framework.web.config;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.seata.rm.datasource.DataSourceProxy;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

//@Configuration
public class SeataDataSourceConfig {

  /**
   * 创建 Druid 数据源
   */
  @Bean
  @ConfigurationProperties(prefix = "spring.datasource")
  public DataSource druidDataSource() {
    return new HikariDataSource();
  }

  /**
   * 创建 Seata 的 DataSourceProxy，代理原始数据源
   *
   * @Primary 注解确保在存在多个 DataSource Bean 时，优先使用这个被代理的数据源
   */
  @Primary
  @Bean("dataSourceProxy")
  public DataSourceProxy dataSourceProxy(DataSource druidDataSource) {
    return new DataSourceProxy(druidDataSource);
  }

  /**
   * 配置 MyBatis 的 SqlSessionFactory，使用 Seata 的 DataSourceProxy
   */
  @Bean
  public SqlSessionFactory sqlSessionFactory(
      @Qualifier("dataSourceProxy") DataSourceProxy dataSourceProxy) throws Exception {
    SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
    factoryBean.setDataSource(dataSourceProxy); // 使用代理后的数据源
    factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(
        "classpath*:mapper/*.xml")); // 指定 mapper XML 文件位置
    // 可以添加其他 MyBatis 配置...
    return factoryBean.getObject();
  }
}