package com.xx.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Agao
 * @date 2024/6/3 16:45
 */
@Data
@NoArgsConstructor
public class Person {
  private Integer id;
  private String name;

  public Person(Integer id) {
    this.id = id;
  }

  public Person(String name) {
    this.name = name;
  }

  public Person(Integer id, String name) {
    this.id = id;
    this.name = name;
  }
}
