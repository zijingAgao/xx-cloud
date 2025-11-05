package com.xx.idempotent.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Agao
 * @date 2025/11/5 16:46
 */
@Data
@ConfigurationProperties(prefix = "idempotent")
public class IdempotentProperties {

    private Boolean enable = true;

}
