package com.xx.controller;

import com.xx.resp.CommResp;
import com.xx.ro.PreOrderRo;
import com.xx.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Agao
 * @date 2024/2/27 15:04
 */
@Api(tags = "用户接口")
@RestController
@RequestMapping("/api/user")
public class UserController {
  @Autowired private UserService userService;

  @ApiOperation(value = "获取用户信息")
  @GetMapping("/{id}")
  public String findOne(@PathVariable("id") String id) {
    return id;
  }

  @ApiOperation(value = "下预定单")
  @PostMapping("/pre/order")
  public CommResp<?> preOrder(@RequestBody PreOrderRo ro) {
    userService.preOrder(ro);
    return CommResp.success();
  }
}
