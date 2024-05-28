package com.xx.repo;

import com.xx.entity.WsConnection;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Agao
 * @date 2024/5/28 14:11
 */
public interface WsConnectionRepository
    extends MongoRepository<WsConnection, String>, WsConnectionRepositoryCustom {

  WsConnection findByConnectionIdAndConnectionType(String connectionId, String connectionType);
}
