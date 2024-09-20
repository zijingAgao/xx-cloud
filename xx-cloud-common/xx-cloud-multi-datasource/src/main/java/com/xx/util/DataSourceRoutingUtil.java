package com.xx.util;

import com.xx.datasource.DynamicDataSourceContextHolder;

/**
 * 线程中切换数据源
 *
 * @author Agao
 * @date 2024/9/20 14:44
 */
public class DataSourceRoutingUtil {

  /**
   * 切换数据源
   *
   * @param dataSource 数据源名称
   */
  public static void setDataSource(String dataSource) {
    DynamicDataSourceContextHolder.push(dataSource);
  }
}
