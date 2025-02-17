package com.xx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xx.pojo.User;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Agao
 * @date 2024/9/24 11:33
 */
public interface UserService extends IService<User> {

  void exportUser(HttpServletResponse resp);

  void importUser(MultipartFile file);

  /**
   * 多线程批量导出
   *
   */
  void batchExportUserSheets();  /**

   * 多线程批量导出
   *
   */
  void batchExportUserSheet();
}
