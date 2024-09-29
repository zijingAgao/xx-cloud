package com.xx.controller;

import com.xx.resp.R;
import com.xx.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 导入控制器
 *
 * @author Agao
 * @date 2024/9/24 11:13
 */
@RestController
@RequiredArgsConstructor
public class ImportController {

  private final UserService userService;

  /**
   * 导入用户
   *
   * @param file
   * @return
   */
  public R<?> importUser(@RequestParam("file") MultipartFile file) {
    userService.importUser(file);
    return R.success();
  }
}
