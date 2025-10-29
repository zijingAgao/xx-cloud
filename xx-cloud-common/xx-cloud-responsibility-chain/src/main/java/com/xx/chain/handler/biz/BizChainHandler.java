package com.xx.chain.handler.biz;

import com.xx.chain.pojo.BizCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Agao
 * @date 2025/10/29 15:46
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BizChainHandler implements AbstractBizChainHandler<BizCommand>{


    @Override
    public void handler(BizCommand reqParam) {
        log.info("execute BizChainHandler");
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
