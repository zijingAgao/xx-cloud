package com.xx.listener;

import cn.hutool.core.collection.ListUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.xx.pojo.User;
import com.xx.service.UserService;
import com.xx.vo.UserVo;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserImportListener implements ReadListener<UserVo> {
  /*缓存数据*/
  private final CopyOnWriteArrayList<UserVo> cacheList = new CopyOnWriteArrayList<>();
  /*单次触发写入库的数量， 1w条，根据jvm内存评估，避免读取进来发生 OOM */
  private static final int BATCH_COUNT = 10000;
  private final ThreadPoolExecutor threadPoolExecutor;
  private final UserService userService;
  @Override
  public void invoke(UserVo user, AnalysisContext analysisContext) {
    // TODO: 参数校验
    if (user != null) {
      cacheList.add(user);
    }
    if (cacheList.size() >= BATCH_COUNT) {
      log.info("读取数据：{}", cacheList.size());
      saveData();
    }
  }

  /** 采用多线程读取数据 */
  private void saveData() {
    // 4核心线程 每个线程写2500条数据
    List<List<UserVo>> lists = ListUtil.split(cacheList, 2500);
    CountDownLatch countDownLatch = new CountDownLatch(lists.size());
    for (List<UserVo> list : lists) {
      threadPoolExecutor.execute(
          () -> {
            try {
              List<User> userList =
                  list.stream()
                      .map(
                          o -> {
                            User user = new User();
                            BeanUtils.copyProperties(o, user);
                            return user;
                          })
                      .collect(Collectors.toList());
              userService.saveBatch(userList);
            } catch (Exception e) {
              log.error("线程执行失败", e);
            } finally {
              // 执行完一个线程减1,直到执行完
              countDownLatch.countDown();
            }
          });
    }
    // 等待所有线程执行完
    try {
      countDownLatch.await();
    } catch (Exception e) {
      log.error("等待所有线程执行完异常", e);
    }
    // 提前将不再使用的集合清空，释放资源
    cacheList.clear();
    lists.clear();
  }

  /**
   * 所有数据读取完成之后调用
   *
   * @param analysisContext
   */
  @Override
  public void doAfterAllAnalysed(AnalysisContext analysisContext) {
    // 读取剩余数据
    if (!CollectionUtils.isEmpty(cacheList)) {
      log.info("最后写入数据：{}条", cacheList.size());
      saveData();
    }
  }
}
