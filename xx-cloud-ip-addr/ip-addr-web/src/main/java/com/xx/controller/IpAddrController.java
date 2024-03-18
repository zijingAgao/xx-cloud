package com.xx.controller;

import com.xx.service.IpAddrService;
import com.xx.vo.IpAddrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Agao
 * @date 2024/3/18 9:24
 */
@RestController
public class IpAddrController {
  @Autowired private IpAddrService ipAddrService;

  @GetMapping("/api/ipaddr/{ip}")
  public IpAddrVo ipAddr(@PathVariable("ip") String ip) {
    if (!StringUtils.hasText(ip)) {
      return null;
    }
    return ipAddrService.ipAddr(ip);
  }
}
