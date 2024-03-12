package com.xx.feign;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author Agao
 * @date 2024/3/12 20:56
 */
@FeignClient("xx-cloud-user")
public interface UserClient {

}
