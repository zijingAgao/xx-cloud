package com.xx.config;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author Agao
 * @date 2024/7/1 19:42
 */
@Configuration
public class RedisConfig {
  @Autowired private RedissonClient redissonClient;

  public void lock() {
    RLock lock = redissonClient.getLock("myLock");
    try {
      // 尝试获取锁，最多等待100秒，上锁以后10秒自动解锁
      if (lock.tryLock(100, 10, TimeUnit.SECONDS)) {
        // do something
      }
    } catch (Exception e) {
      //
    } finally {
      lock.unlock();
    }
  }
}
