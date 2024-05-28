package com.xx.service;

import com.xx.entity.WsConnection;
import com.xx.pojo.WebSocketSession;
import com.xx.repo.WsConnectionRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * @author Agao
 * @date 2024/5/28 14:14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WsConnectionService {
  private static final String WS_CHILD_CONNECTION_KEY_PREFIX = "ws_child_connection_%s";
  private final WsConnectionRepository wsConnectionRepository;
  private final RedissonClient redissonClient;

  /**
   * 创建ws连接
   *
   * @param wsSession
   */
  public boolean connect(WebSocketSession wsSession) {
    log.info("db-创建ws连接：{}", wsSession);
    try {
      WsConnection wsConnection =
          wsConnectionRepository.findByConnectionIdAndConnectionType(
              wsSession.getConnectionId(), wsSession.getConnectionType());
      if (wsConnection == null) {
        wsConnection = new WsConnection();
        BeanUtils.copyProperties(wsSession, wsConnection);
        wsConnectionRepository.save(wsConnection);
      } else {
        BeanUtils.copyProperties(wsSession, wsConnection);
        wsConnection.setConnected(true);
        wsConnectionRepository.save(wsConnection);
      }
      return true;
    } catch (Exception e) {
      log.error("db-创建ws连接发送了未知异常：{}", wsSession, e);
      return false;
    }
  }

  public void disconnect(WebSocketSession wsSession) {
    log.info("db-断开ws连接：{}", wsSession);
    WsConnection wsConnection =
        wsConnectionRepository.findByConnectionIdAndConnectionType(
            wsSession.getConnectionId(), wsSession.getConnectionType());

    if (wsConnection == null) {
      log.error("未在DB中找到连接信息：{}", wsSession);
      return;
    }
    BeanUtils.copyProperties(wsSession, wsConnection);
    wsConnection.setDisconnectTime(LocalDateTime.now());
    wsConnection.setConnected(false);
    wsConnectionRepository.save(wsConnection);
  }

  public void pong(WebSocketSession wsSession) {
    log.debug("心跳更新：{}", wsSession);
    WsConnection wsConnection =
        wsConnectionRepository.findByConnectionIdAndConnectionType(
            wsSession.getConnectionId(), wsSession.getConnectionType());
    if (wsConnection == null) {
      log.error("未在DB中找到连接信息：{}", wsSession);
      return;
    }
    wsConnection.setConnected(true);
    wsConnection.setLastHeartTime(LocalDateTime.now());
    wsConnectionRepository.save(wsConnection);
  }

  public boolean validateToken(String connectionId) {
    if (!StringUtils.hasText(connectionId)) {
      return false;
    }
    return wsConnectionRepository.validateToken(connectionId);
  }

  public void openChildWs(String businessEvent, String connectionId, String businessId) {
    log.info("创建WS子连接: businessEvent={},connectionId={},businessId={}", businessEvent, connectionId, businessId);
    try {
      String childConnectionKey = String.format(WS_CHILD_CONNECTION_KEY_PREFIX, businessEvent);
      RBucket<Map<String, Map<String, String>>> bucket = redissonClient.getBucket(childConnectionKey);
      Map<String, Map<String, String>> childMap = bucket.get();
      Map<String, String> child = new HashMap<>(4);
      child.put("businessId", businessId);
      child.put("connectionId", connectionId);
      child.put("createTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
      if (childMap == null) {
        childMap = new HashMap<>();
      }
      childMap.put(connectionId, child);
      bucket.set(childMap, 24, TimeUnit.HOURS);
    } catch (Exception e) {
      log.error("创建ws子链接发送异常", e);
    }

  }

  public void closeChildWs(String businessEvent, String connectionId) {
    log.info("关闭ws子连接: businessEvent={},connectionId={}", businessEvent, connectionId);
    try {
      String childConnectionKey = String.format(WS_CHILD_CONNECTION_KEY_PREFIX, businessEvent);
      RBucket<Map<String, Map<String, String>>> bucket = redissonClient.getBucket(childConnectionKey);
      if (bucket.isExists()) {
        Map<String, Map<String, String>> childMap = bucket.get();
        if (!CollectionUtils.isEmpty(childMap)) {
          childMap.remove(connectionId);
        }
        if (CollectionUtils.isEmpty(childMap)) {
          bucket.delete();
        } else {
          bucket.set(childMap, 24, TimeUnit.HOURS);
        }
      }
    } catch (Exception e) {
      log.error("关闭ws子连接发送异常", e);
    }
  }
}
