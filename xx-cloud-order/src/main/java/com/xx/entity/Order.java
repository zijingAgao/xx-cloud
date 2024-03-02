package com.xx.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Agao
 * @date 2024/3/2 14:37
 */
@Data
public class Order implements Serializable {
  private String id;
  private BigDecimal price;
  private Integer status;
  private Long createDate;
  private Long payDate;
}
