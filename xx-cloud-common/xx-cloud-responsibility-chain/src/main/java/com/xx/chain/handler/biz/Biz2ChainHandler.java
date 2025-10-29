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
public class Biz2ChainHandler implements AbstractBizChainHandler<BizCommand>{


    @Override
    public void handler(BizCommand reqParam) {
        log.info("execute Biz2ChainHandler");
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
