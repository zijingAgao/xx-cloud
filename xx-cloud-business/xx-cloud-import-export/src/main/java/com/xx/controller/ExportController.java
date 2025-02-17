package com.xx.controller;

import com.xx.service.UserService;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 导出控制器
 *
 * @author Agao
 * @date 2024/9/24 11:13
 */
@RestController
@RequiredArgsConstructor
public class ExportController {

  private final UserService userService;

  @GetMapping("/api/export")
  public void export(HttpServletResponse resp) {
    userService.exportUser(resp);
  }

  @GetMapping("/api/batchExport")
  public void batchExport() {
    userService.batchExportUserSheet();
  }
}
