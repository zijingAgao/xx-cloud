package com.xx.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @author Agao
 * @date 2024/9/20 11:18
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
  /**
   * 获取当前线程需要使用的数据源
   *
   * @return
   */
  @Override
  protected Object determineCurrentLookupKey() {
    return DynamicDataSourceContextHolder.peek();
  }
}
