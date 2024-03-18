package com.xx.service;

import com.xx.awdb.cache.AwdbCache;
import com.xx.awdb.entity.AwdbInfo;
import com.xx.vo.IpAddrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Agao
 * @date 2024/3/18 9:26
 */
@Service
public class IpAddrService {
  @Autowired private AwdbCache awdbCache;

  /**
   * 统一对外暴露的ip寻址接口，返回ip对于的地理位置信息，如果配置了多数据源，需要在此处判断使用的数据源
   *
   * @param ip ipv4 / ipv6
   * @return 该ip的地理位置信息
   */
  public IpAddrVo ipAddr(String ip) {
    // 查询使用的数据源 当前只有awdb
    AwdbInfo awdbInfo = awdbCache.get(ip);
    if (awdbInfo == null) {
      return null;
    }
    IpAddrVo vo = new IpAddrVo();
    BeanUtils.copyProperties(awdbInfo, vo);
    return vo;
  }
}
