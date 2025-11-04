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
     * 防重复提交锁的过期时间，第一次请求之后的5秒内，相同的请求将被视为重复并被阻止 只对PARAM类型生效
     *
     * @return 重复请求的过期时间 单位 毫秒
     */
    long lockExpire() default 2000L;

    /**
     * 重复提交提示信息
     *
     * @return message
     */
    String message() default "您操作太快，请稍后再试";
}
