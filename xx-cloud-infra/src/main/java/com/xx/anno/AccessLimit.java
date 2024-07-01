package com.xx.anno;

import java.lang.annotation.*;

/**
 * 接口限流
 *
 * @author Agao
 * @date 2024/7/1 22:47
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessLimit {
  /**
   * 设置时间间隔
   *
   * @return
   */
  int second() default 10;

  /**
   * 设置最大请求次数
   *
   * @return
   */
  int maxCount() default 10;

  /**
   * 提示消息
   *
   * @return
   */
  String msg() default "访问过于频繁";
}
