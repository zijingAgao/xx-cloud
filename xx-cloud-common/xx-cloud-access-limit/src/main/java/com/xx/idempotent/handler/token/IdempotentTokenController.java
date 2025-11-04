package com.xx.idempotent.handler.token;

import com.xx.resp.XR;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Agao
 * @date 2025/11/4 14:55
 */
@RestController
@RequiredArgsConstructor
public class IdempotentTokenController {
    private final IdempotentTokenService idempotentTokenService;

    /**
     * 申请幂等性token
     *
     * @return token
     */
    @PostMapping("/token")
    public XR<String> applyToken() {
        return XR.success(idempotentTokenService.applyToken());
    }
}
