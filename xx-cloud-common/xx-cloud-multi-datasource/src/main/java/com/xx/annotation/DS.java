package com.xx.annotation;

import java.lang.annotation.*;

/**
 * 选择数据源注解 可用于方法，类上
 *
 * @author Agao
 * @date 2024/9/20 10:59
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DS {

  /**
   * 数据源名称，默认为master
   *
   * @return
   */
  String value() default "master";
}
