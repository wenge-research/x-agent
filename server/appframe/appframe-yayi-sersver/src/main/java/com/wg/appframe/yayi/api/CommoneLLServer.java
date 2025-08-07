package com.wg.appframe.yayi.api;

import com.wg.appframe.yayi.param.CommoneCompletionParam;
import com.wg.appframe.yayi.result.CommoneCompletionResult;
import com.wg.appframe.yayi.strategy.CommoneCompletionStrategy;
import com.wg.appframe.yayi.strategy.SimplyStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@Component
public class CommoneLLServer {

    @Autowired(required = false)
    private CommoneCompletionStrategy commoneCompletionStrategy;

    @Autowired(required = false)
    private SimplyStrategy simplyStrategy;

    /**
     * 调用completions接口
     *
     * @return
     */
    public CommoneCompletionResult generate(CommoneCompletionParam param, Consumer<CommoneCompletionResult> consumer) {
        if (StringUtils.isBlank(param.getStrategy())) {
            log.info("===>CommoneCompletionStrategy strategy is null,common or simply");
            return new CommoneCompletionResult();
        }

        switch (param.getStrategy()) {
            case "common":
                return commoneCompletionStrategy.run(param, consumer);
            case "simply":
                return simplyStrategy.run(param, consumer);
            default:
                log.info("===>CommoneCompletionStrategy strategy is null,common or simply");
                return new CommoneCompletionResult();
        }
    }
}
