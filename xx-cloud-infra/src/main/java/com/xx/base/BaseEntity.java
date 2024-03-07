package com.xx.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import java.io.Serializable;

/**
 * @author Agao
 * @date 2024/3/7 15:34
 */
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable {
  @Version
  @ApiModelProperty("版本号")
  private Long version;

  @CreatedDate
  @ApiModelProperty("创建时间")
  private Long createDate;

  @LastModifiedDate
  @ApiModelProperty("更新时间")
  private Long updateDate;

  @CreatedBy
  @ApiModelProperty("创建人")
  private String createBy;

  @LastModifiedBy
  @ApiModelProperty("更新人")
  private String updateBy;
}
