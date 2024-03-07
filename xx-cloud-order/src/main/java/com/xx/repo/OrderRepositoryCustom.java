package com.xx.repo;

import com.xx.entity.Order;
import com.xx.ro.OrderRo;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author Agao
 * @date 2024/3/7 15:56
 */
public interface OrderRepositoryCustom {
  // 条件查询
  List<Order> findByCondition(OrderRo ro);

  // 分页条件查询
  Page<Order> pageFindByCondition(OrderRo ro);
}
