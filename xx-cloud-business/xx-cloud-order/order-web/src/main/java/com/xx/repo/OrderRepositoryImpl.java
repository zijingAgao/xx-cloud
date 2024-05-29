package com.xx.repo;

import com.xx.entity.Order;
import com.xx.ro.OrderRo;
import com.xx.util.MongoPageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Agao
 * @date 2024/3/7 15:59
 */
@Repository
public class OrderRepositoryImpl implements OrderRepositoryCustom {

  @Autowired private MongoTemplate mongoTemplate;
  @Autowired private MongoPageHelper mongoPageHelper;

  @Override
  public List<Order> findByCondition(OrderRo ro) {
    Criteria criteria = generateCondition(ro);
    return mongoTemplate.find(Query.query(criteria), Order.class);
  }

  @Override
  public Page<Order> pageFindByCondition(OrderRo ro) {
    Criteria criteria = generateCondition(ro);
    return mongoPageHelper.page(criteria, ro.obtainPageable(), Order.class);
  }

  private Criteria generateCondition(OrderRo ro) {
    Criteria criteria = new Criteria();
    if (ro == null) {
      return criteria;
    }
    String id = ro.getId();
    String uid = ro.getUid();
    String orderNo = ro.getOrderNo();
    BigDecimal price = ro.getPrice();
    Integer status = ro.getStatus();
    Long payDate = ro.getPayDate();
    Integer page = ro.getPage();
    Integer size = ro.getSize();

    if (StringUtils.hasText(id)) {
      criteria.and("id").is(id);
    }
    if (StringUtils.hasText(uid)) {
      criteria.and("uid").is(uid);
    }
    if (StringUtils.hasText(orderNo)) {
      criteria.and("orderNo").is(orderNo);
    }
    if (price != null) {
      criteria.and("price").is(price);
    }
    if (status != null) {
      criteria.and("status").is(status);
    }
    return criteria;
  }
}
