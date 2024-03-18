package com.xx.awdb.cache;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.xx.awdb.AwdbReader;
import com.xx.awdb.entity.AwdbInfo;
import com.xx.awdb.impl.AwdbCacheImpl;
import java.io.File;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author Agao
 * @date 2024/3/18 9:59
 */
@Slf4j
@Component
public class AwdbCache {
  @Autowired private ObjectMapper objectMapper;

  @Value("${xx-cloud.ip-addr.awdb.path:}")
  private String AWDB_PATH = "";

  /** awdb文件名字 */
  private static final String AWDB_FILENAME = "IP_city_single_WGS84_en.awdb";

  private final LoadingCache<String, Optional<AwdbInfo>> awdbCache =
      CacheBuilder.newBuilder()
          .maximumSize(2048)
          .expireAfterAccess(48, TimeUnit.HOURS)
          .build(
              new CacheLoader<String, Optional<AwdbInfo>>() {
                @Override
                public Optional<AwdbInfo> load(String ip) throws Exception {
                  String path = AWDB_PATH + File.separator + AWDB_FILENAME;
                  try (AwdbReader reader = new AwdbReader(new File(path), new AwdbCacheImpl())) {
                    JsonNode awdbNode = reader.findIpLocation(ip);
                    return Optional.ofNullable(transform(awdbNode));
                  } catch (Exception e) {
                    log.warn("[AwdbLoadingCache] awdb load exception, key-ip: {}, error: ", ip, e);
                    return Optional.empty();
                  }
                }
              });

  private AwdbInfo transform(JsonNode awdbNode) {
    if (awdbNode.isNull()) {
      return null;
    }
    return objectMapper.convertValue(awdbNode, AwdbInfo.class);
  }

  public AwdbInfo get(String ip) {
    AwdbInfo awdbInfo = null;
    try {
      awdbInfo = awdbCache.get(ip).orElse(null);
    } catch (ExecutionException e) {
      log.warn("[AwdbLoadingCache] awdb load exception, key-ip: {}, error: ", ip, e);
    }
    return awdbInfo;
  }

  public void validate(String ip) {
    if (StringUtils.hasText(ip)) {
      awdbCache.invalidate(ip);
    }
  }

  public void invalidateAll() {
    awdbCache.invalidateAll();
  }
}
