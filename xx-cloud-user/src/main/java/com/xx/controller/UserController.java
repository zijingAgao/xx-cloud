package com.xx.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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

  @ApiOperation(value = "获取用户信息")
  @GetMapping("/{id}")
  public String findOne(@PathVariable String id) {
    return id;
  }
}
