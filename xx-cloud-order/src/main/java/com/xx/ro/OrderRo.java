package com.xx.ro;

import com.xx.base.PageBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

/**
 * @author Agao
 * @date 2024/3/7 15:51
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderRo extends PageBase {
  @Id private String id;

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
