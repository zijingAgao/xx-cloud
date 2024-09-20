package com.xx.config;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Agao
 * @date 2024/9/20 11:02
 */
@Data
@ConfigurationProperties(prefix = "spring.datasource.dynamic")
public class MultiDataSourceProperties {

  /** 使用连接池类型 type：com.alibaba.druid.pool.DruidDataSource ；com.zaxxer.hikari.HikariDataSource */
  private String poolClassName;

  /** 多数据源 */
  private Map<String, DataSourceProperties> multi = new HashMap<>();
}
