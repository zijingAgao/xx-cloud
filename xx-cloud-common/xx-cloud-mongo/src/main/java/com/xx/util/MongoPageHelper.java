package com.xx.util;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

/**
 * 辅助分页使用，目前按照前后端约定，分页超过最大数时，直接返回到最后一页
 *
 * @author Agao
 * @date 2024/3/12 14:25
 */
@Component
public class MongoPageHelper {

  @Autowired
  private MongoTemplate mongoTemplate;

  public <T> Page<T> page(Criteria criteria, Pageable pageable, Class<T> clazz) {
    return this.page(criteria, null, pageable, clazz);
  }

  public <T> Page<T> page(Criteria criteria, Sort sort, Pageable pageable, Class<T> clazz) {
    Query query = new Query(criteria);
    long total = mongoTemplate.count(query, clazz);
    if (total == 0) {
      return Page.empty(pageable);
    }
    if (pageable.getOffset() > total) {
      int page = (int) (total / pageable.getPageSize());
      pageable = PageRequest.of(page, pageable.getPageSize());
    }

    query.with(pageable);
    if (sort != null) {
      query.with(sort);
    }
    List<T> data = mongoTemplate.find(query, clazz);
    return new PageImpl<>(data, pageable, total);
  }

  /**
   * 聚合分页查询
   *
   * @param criteria 匹配条件
   * @param sort 排序条件
   * @param pageable 分页条件
   * @param clazz 查询实体
   * @param <T>
   * @return
   */
  public <T> Page<T> pageAggregate(Criteria criteria, Sort sort, Pageable pageable, Class<T> clazz) {
    return pageAggregateFields(criteria, sort, pageable, clazz);
  }

  /**
   * 聚合分页查询
   *
   * @param criteria 匹配条件
   * @param sort 排序条件
   * @param pageable 分页条件
   * @param clazz 查询实体
   * @param excludeFields 排除的字段
   * @param <T>
   * @return
   */
  public <T> Page<T> pageAggregateFields(Criteria criteria, Sort sort, Pageable pageable, Class<T> clazz, String... excludeFields) {
    List<AggregationOperation> operations = new ArrayList<>();
    Query query = new Query(criteria);
    long total = mongoTemplate.count(query, clazz);
    if (total == 0) {
      return Page.empty(pageable);
    }
    //1.匹配条件
    operations.add(Aggregation.match(criteria));
    //2.排序条件
    if (sort != null) {
      operations.add(Aggregation.sort(sort));
    }
    //3.分页条件
    int page = pageable.getPageNumber();
    int pageSize = pageable.getPageSize();
    if (pageable.getOffset() > total) {
      page = (int) (total / pageSize);
      pageable = PageRequest.of(page, pageSize);
    }
    operations.add(Aggregation.skip(page * pageSize));
    operations.add(Aggregation.limit(pageSize));
    //4.排除的字段
    if (excludeFields.length != 0) {
      operations.add(Aggregation.project().andExclude(excludeFields));
    }
    //5.允许占用磁盘
    AggregationOptions aggregationOptions = Aggregation.newAggregationOptions().allowDiskUse(true).build();

    TypedAggregation typedAggregation = new TypedAggregation(clazz, operations).withOptions(aggregationOptions);
    AggregationResults<T> output = mongoTemplate.aggregate(typedAggregation, clazz);
    List<T> data = output.getMappedResults();

    return new PageImpl<>(data, pageable, total);
  }

}
