package com.xx.config;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EasyExcelThreadPoolExecutor {

  @Bean(name = "easyExcelThreadPool")
  public ThreadPoolExecutor easyExcelThreadPool() {
    return new ThreadPoolExecutor(
        10,
        20,
        60L,
        TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(10000),
        new ThreadPoolExecutor.CallerRunsPolicy());
  }
}
