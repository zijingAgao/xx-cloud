package com.xx.awdb.entity;

import lombok.Data;

/**
 * awdb 库映射对象
 *
 * @author Agao
 * @date 2023-8-15 15:58
 */
@Data
public class AwdbInfo {
  /** 大洲,值为七大洲和 保留IP */
  private String continent;
  /** 国家编码 */
  private String areacode;
  /** 国家 */
  private String country;
  /** 邮编 */
  private String zipcode;
  /** 时区 */
  private String timezone;
  /** 定位精度 */
  private String accuracy;
  /** 定位方式 */
  private String source;
  /** 省份 */
  private String province;
  /** 城市 */
  private String city;
  /** WGS84 坐标系经度 */
  private Double lngwgs;
  /** WGS84 坐标系纬度 */
  private Double latwgs;
  /** 定位半径 */
  private String radius;
  /** 运营商 */
  private String isp;
  /** 自治域编码 */
  private String asnumber;
  /** 所属机构 */
  private String owner;
  /** 行政区划代码 */
  private String adcode;
}
