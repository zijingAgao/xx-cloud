package com.xx.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.xx.ro.PreOrderRo;
import com.xx.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Agao
 * @date 2024/3/2 14:46
 */
@RestController
@RequestMapping("/api")
public class OrderController {
  @Autowired private OrderService orderService;

  @PostMapping("/pre/order")
  public void preOrder(@RequestBody PreOrderRo ro) throws JsonProcessingException {
    orderService.delayPreOrder(ro);
  }
}
