package com.xx.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Agao
 * @date 2024/3/12 19:53
 */
@RestController
public class AdminController {

  @GetMapping("/api/test")
  public String test() {
    return "test";
  }
}
