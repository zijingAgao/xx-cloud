package com.xx.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * @author Agao
 * @date 2024/9/24 11:25
 */
@Data
@TableName("user")
public class User {
  /** 主键 */
  @TableId(type = IdType.AUTO)
  private Integer id;

  /** 姓名 */
  private String username;

  /** 性别 */
  private Integer sex;

  /** 年龄 */
  private Integer age;

  /** 密码 */
  private String password;

  /** 邮箱 */
  private String email;

  /** 电话 */
  private String phone;

  /** 地址 */
  private String address;

  /** 状态 */
  private Integer status;

  /** 部门 */
  private Integer dept;

  /** 备注 */
  private String remark;

  /** 头像 */
  private String avatar;

  /** 创建时间 */
  private LocalDateTime createTime;

  /** 更新时间 */
  private LocalDateTime updateTime;

  /** 创建人 */
  private String createBy;

  /** 更新人 */
  private String updateBy;

  /** 版本号 */
  private Long version;
}
