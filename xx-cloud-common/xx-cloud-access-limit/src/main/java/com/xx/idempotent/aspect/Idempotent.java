package com.xx.idempotent.aspect;

import com.xx.idempotent.enums.IdempotentType;

import java.lang.annotation.*;

/**
 * 幂等性-重复提交注解
 *
 * @author Agao
 * @date 2025/10/29 9:50
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {

    /**
     * 计算幂等性类型
     * 默认PARAM
     *
     * @return 幂等规则类型
     */
    IdempotentType type() default IdempotentType.PARAM;

    /**
     * 重复提交提示信息
     *
     * @return message
     */
    String message() default "您操作太快，请稍后再试";
}
