package com.xx.anno;

import java.lang.annotation.*;

/**
 * 防重复提交注解
 *
 * @author Agao
 * @date 2024/7/1 23:22
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RepeatSubmit {

  /**
   * 设置默认的防重提交方式为基于方法参数。
   *
   * @return
   */
  Type type() default Type.PARAM;

  /** 定义了两种防止重复提交的方式， PARAM 表示基于方法参数来防止重复， TOKEN 则可能涉及生成和验证token的机制 */
  enum Type {
    TOKEN,
    PARAM
  }

  /**
   * 防重复提交锁的过期时间，第一次请求之后的5秒内，相同的请求将被视为重复并被阻止
   *
   * @return
   */
  long lockExpire() default 5;

  /** 业务id, 用做key的计算 */
  String businessId() default "";
}
