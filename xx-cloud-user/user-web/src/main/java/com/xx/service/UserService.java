package com.xx.service;

import com.xx.feign.OrderClient;
import com.xx.ro.PreOrderRo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Agao
 * @date 2024/3/13 16:45
 */
@Service
public class UserService {
  @Autowired private OrderClient orderClient;

  public void preOrder(PreOrderRo ro) {
    orderClient.preOrder(ro);
  }
}
