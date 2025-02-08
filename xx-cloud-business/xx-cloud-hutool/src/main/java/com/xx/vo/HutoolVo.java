package com.xx.vo;

import java.util.List;
import lombok.Data;

/**
 * @author Agao
 * @date 2024/10/14 15:32
 */
@Data
public class HutoolVo {
  private Integer id;

  private String name;

  private List<Integer> deptIdList;
}
