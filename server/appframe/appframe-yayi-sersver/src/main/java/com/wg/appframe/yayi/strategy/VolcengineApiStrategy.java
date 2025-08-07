package com.wg.appframe.yayi.strategy;

import com.wg.appframe.yayi.param.VolcengineParam;
import com.wg.appframe.yayi.result.VolcengineResult;

public interface VolcengineApiStrategy {

    default VolcengineResult run(VolcengineParam volcengineParam) {
        return new VolcengineResult();
    }

}
