package com.wg.appframe.yayi.strategy;

import com.wg.appframe.yayi.entity.YayiMessage;
import com.wg.appframe.yayi.param.YayiParam;
import com.wg.appframe.yayi.result.YayiResult;

import java.util.List;
import java.util.function.Consumer;

public interface YayiApiStrategy {

    default YayiResult run(String content, YayiParam yayiParam) {
        return new YayiResult();
    }

    default YayiResult run(String content, YayiParam yayiParam, Consumer<YayiResult> consumer) {
        return new YayiResult();
    }

    default YayiResult run(List<String> content, YayiParam yayiParam) {
        return new YayiResult();
    }

    default YayiResult run(YayiParam yayiParam) {
        return new YayiResult();
    }

    default YayiResult runMsg(List<YayiMessage> contents, YayiParam yayiParam) {
        return new YayiResult();
    }

}
