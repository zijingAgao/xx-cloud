package com.xx.chain.handler.biz;

import com.xx.chain.enums.BizChainMark;
import com.xx.chain.handler.AbstractChainHandler;
import com.xx.chain.pojo.BizCommand;

/**
 * @author Agao
 * @date 2025/10/29 15:53
 */
public interface AbstractBizChainHandler<T extends BizCommand> extends AbstractChainHandler<BizCommand> {

    @Override
    default String mark() {
        return BizChainMark.BIZ_TEXT.getMark();
    }
}
