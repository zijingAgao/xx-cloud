package com.xx.controller;

import com.xx.service.HutoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Agao
 * @date 2024/10/14 15:33
 */
@RestController
@RequiredArgsConstructor
public class HutoolController {
    private final HutoolService hutoolService;


}
