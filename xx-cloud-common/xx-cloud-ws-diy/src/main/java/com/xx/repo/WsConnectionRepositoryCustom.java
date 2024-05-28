package com.xx.repo;

import org.springframework.stereotype.Repository;

/**
 * @author Agao
 * @date 2024/5/28 14:33
 */
@Repository
public interface WsConnectionRepositoryCustom {
  /**
   * 校验token
   *
   * @param connectionId
   * @return
   */
  boolean validateToken(String connectionId);
}
