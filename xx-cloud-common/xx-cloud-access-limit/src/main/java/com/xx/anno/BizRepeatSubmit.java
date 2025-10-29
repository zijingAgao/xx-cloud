package com.xx.anno;

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
public @interface BizRepeatSubmit {


}
