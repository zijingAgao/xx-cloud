package com.xx.base;

import lombok.Getter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * @author Agao
 * @date 2024/3/7 15:52
 */
@Getter
public class PageBase {
  private final Integer page = 0;
  private final Integer size = 10;

  public Pageable obtainPageable() {
    return PageRequest.of(page, size);
  }
}
