package com.xx.config;

import com.xx.datasource.DynamicDataSource;
import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * 多数据源自动装配
 *
 * @author Agao
 * @date 2024/9/20 10:59
 */
@Configuration
@AutoConfigureOrder(Integer.MIN_VALUE)
@EnableAutoConfiguration
@EnableConfigurationProperties(MultiDataSourceProperties.class)
//@AutoConfigureBefore(DataSourceAutoConfiguration.class)
@ComponentScan({"com.xx.config"})
public class MultiDataSourceConfiguration {

  /** 没有配置多个数据源时，使用默认数据源配置 */
  @Autowired(required = false)
  private DataSourceProperties defaultDataSourceProperties;

  /**
   * 加载动态数据源 连接池选择顺序：
   * 每个数据源的type（连接池）为最高优先级，其次为 MultiDataSourceProperties.poolClassName ，最后为默认的HikariDataSource
   *
   * @param multiProperties
   * @return
   * @throws ClassNotFoundException
   */
  @Bean
  public DynamicDataSource dynamicDataSource(MultiDataSourceProperties multiProperties)
      throws ClassNotFoundException {
    DynamicDataSource dynamicDataSource = new DynamicDataSource();
    Map<Object, Object> dataSourceHashMap = new HashMap<>(10);

    String poolClassName = multiProperties.getPoolClassName();
    Map<String, DataSourceProperties> dynamicDataSourceMap = multiProperties.getMulti();
    // 没配置则使用默认的 HikariDataSource
    if (!StringUtils.hasText(poolClassName)) {
      poolClassName = HikariDataSource.class.getName();
    }
    Class<DataSource> dataSourceClass = (Class<DataSource>) Class.forName(poolClassName);

    // 加载默认数据源
    if (defaultDataSourceProperties != null) {
      DataSource dataSource = initializeDataSource(dataSourceClass, defaultDataSourceProperties);
      dynamicDataSource.setDefaultTargetDataSource(dataSource);
    }

    // 加载多个数据源
    dynamicDataSourceMap.forEach(
        (db, properties) -> {
          DataSource dataSource = initializeDataSource(dataSourceClass, properties);
          dataSourceHashMap.put(db, dataSource);
        });

    dynamicDataSource.setTargetDataSources(dataSourceHashMap);
    return dynamicDataSource;
  }

  /**
   * 加载数据源
   *
   * @param defaultType 默认连接池类型
   * @param properties 配置
   * @return
   */
  private DataSource initializeDataSource(
      Class<? extends DataSource> defaultType, DataSourceProperties properties) {
    Class<? extends DataSource> dataSourceType = properties.getType();
    Class<? extends DataSource> type = dataSourceType == null ? defaultType : dataSourceType;
    return properties.initializeDataSourceBuilder().type(type).build();
  }
}
