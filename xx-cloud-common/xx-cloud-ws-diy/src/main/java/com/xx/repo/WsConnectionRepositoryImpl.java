package com.xx.repo;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * @author Agao
 * @date 2024/5/28 14:33
 */
@RequiredArgsConstructor
public class WsConnectionRepositoryImpl implements WsConnectionRepositoryCustom {
  private final MongoTemplate mongoTemplate;

  @Override
  public boolean validateToken(String connectionId) {
    Criteria criteria = new Criteria();
    // todo: 校验发token
    return true;
  }
}
