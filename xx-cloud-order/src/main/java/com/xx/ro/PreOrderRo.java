package com.xx.ro;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Agao
 * @date 2024/3/2 14:48
 */
@Data
public class PreOrderRo {
    private String id;
    private BigDecimal price;
    private Long createDate;
    private Long payDate;
}
