package com.xx.controller;

import com.xx.pojo.User;
import com.xx.resp.R;
import com.xx.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Agao
 * @date 2024/9/24 14:00
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/api/user")
    public R<List<User>> listUser() {
        List<User> list =  userService.list();
        return R.success(list);
    }
}
