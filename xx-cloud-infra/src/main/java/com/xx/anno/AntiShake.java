package com.xx.anno;

import java.lang.annotation.*;

/**
 * 防抖注解
 *
 * @author Agao
 * @date 2024/7/2 0:02
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AntiShake {
  /**
   * 防抖时间 单位：ms 默认1s
   *
   * @return
   */
  long value() default 1000L;
}
