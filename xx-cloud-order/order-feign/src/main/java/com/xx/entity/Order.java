package com.xx.entity;

import com.xx.base.BaseEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Agao
 * @date 2024/3/2 14:37
 */
@Data
@Document
@EqualsAndHashCode(callSuper = true)
public class Order extends BaseEntity implements Serializable {
  @Id
  private String id;
  @ApiModelProperty("用户id")
  private String uid;
  @ApiModelProperty("订单编号")
  private String orderNo;
  @ApiModelProperty("支付价格")
  private BigDecimal price;
  @ApiModelProperty("订单状态")
  private Integer status;
  @ApiModelProperty("支付时间")
  private Long payDate;
}
