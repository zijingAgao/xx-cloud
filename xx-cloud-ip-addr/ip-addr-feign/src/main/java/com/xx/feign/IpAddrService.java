package com.xx.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("xx-cloud-ip-addr")
public interface IpAddrService {

}
