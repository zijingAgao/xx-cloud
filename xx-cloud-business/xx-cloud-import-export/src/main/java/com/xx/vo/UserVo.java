package com.xx.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.xx.converter.GenderConverter;
import com.xx.pojo.User;
import lombok.Data;

/**
 * @author Agao
 * @date 20210/9/210 11:33
 */
@Data
public class UserVo {
  @ColumnWidth(8)
  @ExcelProperty("id")
  private Integer id;

  @ColumnWidth(10)
  @ExcelProperty("姓名")
  private String username;

  @ColumnWidth(10)
  @ExcelProperty(value = "性别", converter = GenderConverter.class)
  private Integer sex;

  @ColumnWidth(10)
  @ExcelProperty("年龄")
  private Integer age;

  @ColumnWidth(12)
  @ExcelProperty("密码")
  private String password;

  @ColumnWidth(24)
  @ExcelProperty("邮箱")
  private String email;

  @ColumnWidth(20)
  @ExcelProperty("电话")
  private String phone;

  @ColumnWidth(20)
  @ExcelProperty("地址")
  private String address;

  @ColumnWidth(8)
  @ExcelProperty("状态")
  private Integer status;

  @ColumnWidth(8)
  @ExcelProperty("部门")
  private Integer dept;

  @ColumnWidth(12)
  @ExcelProperty("备注")
  private String remark;

  @ColumnWidth(20)
  @ExcelProperty("头像")
  private String avatar;

  public static UserVo convert(User user) {
    UserVo vo = new UserVo();
    vo.setId(user.getId());
    vo.setUsername(user.getUsername());
    vo.setSex(user.getSex());
    vo.setAge(user.getAge());
    vo.setPassword(user.getPassword());
    vo.setEmail(user.getEmail());
    vo.setPhone(user.getPhone());
    vo.setAddress(user.getAddress());
    vo.setStatus(user.getStatus());
    vo.setDept(user.getDept());
    vo.setRemark(user.getRemark());
    vo.setAvatar(user.getAvatar());

    return vo;
  }
}
