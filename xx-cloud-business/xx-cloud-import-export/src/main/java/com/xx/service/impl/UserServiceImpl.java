package com.xx.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xx.listener.UserImportListener;
import com.xx.mapper.UserMapper;
import com.xx.pojo.User;
import com.xx.service.UserService;
import com.xx.util.ExportUtils;
import com.xx.vo.UserVo;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Agao
 * @date 2024/9/24 16:05
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
  private final UserImportListener userImportListener;

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
}
