package com.xx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * ip寻址服务
 *
 * @author Agao
 * @date 2024/3/18 9:23
 */
@SpringBootApplication
@EnableDiscoveryClient
public class IpAddrApplication {
  public static void main(String[] args) {
    SpringApplication.run(IpAddrApplication.class, args);
  }
}
