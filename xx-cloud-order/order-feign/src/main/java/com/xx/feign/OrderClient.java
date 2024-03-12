package com.xx.feign;

import com.xx.entity.Order;
import com.xx.ro.OrderRo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 订单服务openFeign客户端
 *
 * @author Agao
 * @date 2024/3/7 16:17
 */
@FeignClient("xx-cloud-order")
public interface OrderClient {

  @GetMapping("/page")
  Page<Order> pageFind(OrderRo ro);
}
