package com.xx.feign;

import com.xx.vo.IpAddrVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("xx-cloud-ip-addr")
public interface IpAddrService {
  @GetMapping("/api/ipaddr/{ip}")
  IpAddrVo ipAddr(@PathVariable("ip") String ip);
}
