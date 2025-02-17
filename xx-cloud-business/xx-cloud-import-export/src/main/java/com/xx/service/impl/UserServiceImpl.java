package com.xx.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xx.listener.UserImportListener;
import com.xx.mapper.UserMapper;
import com.xx.pojo.DataChunk;
import com.xx.pojo.User;
import com.xx.service.UserService;
import com.xx.util.ExportUtils;
import com.xx.vo.UserVo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Agao
 * @date 2024/9/24 16:05
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
  @Autowired
  private UserImportListener userImportListener;
  @Autowired
  @Qualifier(value = "easyExcelThreadPool")
  private ThreadPoolExecutor easyExcelThreadPool;


  /**
   * 导出用户信息
   *
   * @param resp
   */
  public void exportUser(HttpServletResponse resp) {
    List<UserVo> userVos = list().stream().map(UserVo::convert).collect(Collectors.toList());

    String fileName = "用户信息-" + System.currentTimeMillis();
    String sheetName = "用户表";

    ExportUtils.exportExcel(fileName, sheetName, userVos, UserVo.class, resp);
  }

  /**
   * 导入用户信息
   *
   * @param file
   */
  public void importUser(MultipartFile file) {
    try {
      EasyExcel.read(file.getInputStream(), User.class, userImportListener).sheet().doRead();
    } catch (IOException e) {
      log.warn("文件读取异常", e);
    }
  }
  @Override
  public void batchExportUserSheets() {
    final int threadUserCount = 10000; // 每个线程处理 10,000 条
    final int threadCount = 10; // 10 个线程
    final int threadResolveCount = 1000; // 每次查询 1000 条

    long currentTimeMillis = System.currentTimeMillis();
    log.info("批量导出用户信息开始");
    String filePath = "批量用户信息-" + currentTimeMillis + ".xlsx";

    // 创建 ExcelWriter 统一写入
    try (ExcelWriter excelWriter = EasyExcel.write(filePath, UserVo.class).excelType(ExcelTypeEnum.XLSX).build()) {
      List<Future<?>> futures = new ArrayList<>();

      for (int i = 0; i < threadCount; i++) {
        int startId = i * threadUserCount + 1; // 确保每个线程查询不同的数据范围
        int endId = (i + 1) * threadUserCount;
        int sheetIndex = i + 1;
        String sheetName = "Sheet-" + sheetIndex;

        futures.add(
                easyExcelThreadPool.submit(() -> {
                  try {
                    // 创建 Sheet 并绑定 ExcelWriter
                    WriteSheet writeSheet = EasyExcel.writerSheet(sheetIndex, sheetName).build();

                    for (int row = startId; row <= endId; row += threadResolveCount) {
                      List<UserVo> dataList = queryData(row, threadResolveCount);
                      if (CollectionUtils.isEmpty(dataList)) {
                        break;
                      }
                      // 使用同步锁，保证线程安全写入
                      synchronized (excelWriter) {
                        excelWriter.write(dataList, writeSheet);
                      }
                    }
                  } catch (Exception e) {
                    log.warn("线程 id：{}, 导出异常", Thread.currentThread().getId(), e);
                  }
                }));
      }

      // 等待所有任务完成
      for (Future<?> future : futures) {
        try {
          future.get();
        } catch (InterruptedException | ExecutionException e) {
          log.info("任务执行异常", e);
        }
      }
    } catch (Exception e) {
      log.error("Excel 写入异常", e);
    }

    log.info("批量导出用户信息完成, 耗时：{}ms", System.currentTimeMillis() - currentTimeMillis);
  }

  @Override
  public void batchExportUserSheet()  {
    final int threadUserCount = 10000; // 每个线程处理 10,000 条
    final int threadCount = 10; // 10 个线程
    final int threadResolveCount = 1000; // 每次查询 1000 条

    long currentTimeMillis = System.currentTimeMillis();
    log.info("批量导出用户信息开始");
    String filePath = "批量用户信息-" + currentTimeMillis + ".xlsx";

    BlockingQueue<DataChunk> queue = new LinkedBlockingQueue<>(threadCount * 2);
    CountDownLatch latch = new CountDownLatch(threadCount);
    AtomicInteger nextIndex = new AtomicInteger(0);
    List<DataChunk> buffer = new ArrayList<>();

    // **1. 生产者（多线程查询数据）**
    for (int i = 0; i < threadCount; i++) {
      int startId = i * threadUserCount + 1;  // **确保不同线程查询不同数据**
      int endId = (i + 1) * threadUserCount;
      int threadIndex = i; // **确保 index 正确**

      easyExcelThreadPool.submit(() -> {
        try {
          for (int row = startId; row <= endId; row += threadResolveCount) {
            List<UserVo> dataList = queryData(row, threadResolveCount);

            if (!CollectionUtils.isEmpty(dataList)) {
              dataList.sort(Comparator.comparing(UserVo::getId));
              queue.put(new DataChunk(threadIndex, dataList)); // **index 正确**
              log.info("生产数据存储队列，index：{}", threadIndex);
            }
          }
        } catch (Exception e) {
          log.warn("线程 id：{}, 查询数据异常", Thread.currentThread().getId(), e);
        } finally {
          latch.countDown();
        }
      });
    }

    // **2. 消费者（写入 Excel）**
    try (ExcelWriter excelWriter = EasyExcel.write(filePath, UserVo.class).excelType(ExcelTypeEnum.XLSX).build()) {
      WriteSheet writeSheet = EasyExcel.writerSheet("Sheet1").build();

      while (latch.getCount() > 0 || !queue.isEmpty()) {
        DataChunk chunk = queue.poll(3, TimeUnit.SECONDS);

        if (chunk != null) {
          if (chunk.getIndex() == nextIndex.get()) {
            excelWriter.write(chunk.getData(), writeSheet);
            log.info("消费成功，index：{}", nextIndex.get());
          } else {
            buffer.add(chunk);
            continue;
          }
        }

        Iterator<DataChunk> it = buffer.iterator();
        while (it.hasNext()) {
          DataChunk bufferedChunk = it.next();
          if (bufferedChunk.getIndex() == nextIndex.get()) {
            excelWriter.write(bufferedChunk.getData(), writeSheet);
            log.info("消费成功（从 buffer 取出），index：{}", nextIndex.get());
            it.remove();
          }
        }

        if (queue.isEmpty() && buffer.stream().noneMatch(chunkItem -> chunkItem.getIndex() == nextIndex.get())) {
          nextIndex.incrementAndGet();
        }
      }
    } catch (Exception e) {
      log.error("Excel 写入异常", e);
    }

    log.info("批量导出用户信息完成, 耗时：{}ms", System.currentTimeMillis() - currentTimeMillis);
  }
  /**
   * 查询数据
   *
   * @param startId
   * @param count
   * @return
   */
  private List<UserVo> queryData(long startId, int count) {
    List<User> entities = lambdaQuery()
            .ge(User::getId, startId)
            .lt(User::getId, startId + count)
            .list();
    if (CollectionUtils.isEmpty(entities)) {
      return null;
    }
    return entities.stream().map(UserVo::convert).collect(Collectors.toList());
  }
}
