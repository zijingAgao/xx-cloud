package com.xx.repo;

import com.xx.entity.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Agao
 * @date 2024/3/7 15:58
 */
public interface OrderRepository extends MongoRepository<Order, String>, OrderRepositoryCustom {}
