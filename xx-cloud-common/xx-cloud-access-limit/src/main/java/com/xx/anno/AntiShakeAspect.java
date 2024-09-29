package com.xx.anno;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.springframework.stereotype.Component;

/**
 * 防抖切面
 *
 * @author Agao
 * @date 2024/7/2 0:03
 */
@Slf4j
@Component
public class AntiShakeAspect {
  private ThreadLocal<Long> lastInvokeTime = new ThreadLocal<>();

  @Around("@annotation(antiShake)")
  public Object aroundAdvice(ProceedingJoinPoint joinPoint, AntiShake antiShake) throws Throwable {
    long currentTime = System.currentTimeMillis();
    long lastTime = lastInvokeTime.get() != null ? lastInvokeTime.get() : 0;
    if (currentTime - lastTime < antiShake.value()) {
      // 如果距离上次调用时间小于指定的防抖时间，则直接返回，不执行方法
      return null; // 或者根据业务需要返回特定值
    }
    lastInvokeTime.set(currentTime);
    return joinPoint.proceed();
  }
}
