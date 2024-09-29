package com.xx.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Agao
 * @date 2024/9/29 13:35
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "netty")
public class NettyProperties {

  /** 服务器地址 */
  private String host;

  /** 服务器端口 */
  private Integer port;


}
