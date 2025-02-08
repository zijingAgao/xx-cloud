package com.xx.dto;

import java.util.List;
import lombok.Data;

/**
 * @author Agao
 * @date 2024/10/14 15:31
 */
@Data
public class HutoolDto {
  private Integer id;

  private String name;

  private List<Integer> deptIdList;
}
