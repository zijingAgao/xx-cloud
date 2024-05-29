package com.xx.feign;

import com.xx.ro.PreOrderRo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 订单服务openFeign客户端
 *
 * @author Agao
 * @date 2024/3/7 16:17
 */
@FeignClient("xx-cloud-order")
public interface OrderClient {
  @PostMapping("/api/pre/order")
  void preOrder(@RequestBody PreOrderRo ro);
}
