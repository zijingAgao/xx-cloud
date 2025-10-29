package com.xx.resp;

import lombok.Data;

/**
 * @author Agao
 * @date 2025/10/29 16:09
 */
@Data
public class XPage {
    private Integer page;
    private Integer size;
    private Long total;
}
