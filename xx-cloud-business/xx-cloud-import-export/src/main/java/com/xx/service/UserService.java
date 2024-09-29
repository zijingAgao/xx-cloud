package com.xx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xx.pojo.User;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Agao
 * @date 2024/9/24 11:33
 */
public interface UserService extends IService<User> {

  void exportUser(HttpServletResponse resp);

  void importUser(MultipartFile file);
}
