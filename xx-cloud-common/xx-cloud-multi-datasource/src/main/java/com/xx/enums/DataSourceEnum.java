package com.xx.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据源枚举
 *
 * @author Agao
 * @date 2024/9/20 14:53
 */
@Getter
@AllArgsConstructor
public enum DataSourceEnum {
  MASTER("master");

  private final String value;
}
