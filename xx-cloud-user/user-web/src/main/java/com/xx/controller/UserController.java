package com.xx.controller;

import com.xx.entity.Order;
import com.xx.feign.OrderClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Agao
 * @date 2024/2/27 15:04
 */
@Api(tags = "用户接口")
@RestController
@RequestMapping("/api/user")
public class UserController {
  @Autowired
  private OrderClient orderClient;

  @ApiOperation(value = "获取用户信息")
  @GetMapping("/{id}")
  public String findOne(@PathVariable("id") String id) {
    return id;
  }

  @ApiOperation(value = "分页查用户订单")
  @GetMapping("/user/order")
  public Page<Order> test() {
    return orderClient.pageFind(null);
  }
}
