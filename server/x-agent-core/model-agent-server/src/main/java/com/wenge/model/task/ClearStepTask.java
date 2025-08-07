package com.wenge.model.task;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.wenge.model.entity.DialogueStep;
import com.wenge.model.entity.LlmConversation;
import com.wenge.model.mapper.es.DialogueStepMapper;
import com.wenge.model.mapper.es.LlmConversationMapper;
import com.wenge.model.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/step")
@Slf4j
public class ClearStepTask {

    @Autowired
    private DialogueStepMapper stepMapper;

    @Autowired
    private LlmConversationMapper llmConversationMapper;

    @Value("${task.stepclear.enable}")
    private boolean enable;

    /**
     * 清理步骤数据
     * 每隔10分钟清理一次
     */
    @RequestMapping("/clear")
    // @Scheduled(cron = "${task.stepclear.cron}")
    public void clearStep() {
        if (!enable) {
            return;
        }
        log.info("开始清理step数据");
        String lastTime = LocalDateTimeUtil.format(LocalDateTimeUtil.now().minusMonths(3), DateUtil.PATTERN_1);
        LambdaEsQueryWrapper<DialogueStep> wrapper = EsWrappers.lambdaQuery(DialogueStep.class)
                .lt("createTime.keyword", lastTime);
        stepMapper.delete(wrapper);
        log.info("清理step数据结束");
    }

    /**
     * 清理llm会话的数据
     * 每隔10分钟清理一次
     */
    @RequestMapping("/clearerMessage")
    // @Scheduled(cron = "${task.stepclear.cron}")
    public void clearerMessage() {
        if (!enable) {
            return;
        }
        log.info("开始清理clearerMessage数据");
        String lastTime = LocalDateTimeUtil.format(LocalDateTimeUtil.now().minusMonths(1), DateUtil.PATTERN_1);
        LambdaEsQueryWrapper<LlmConversation> wrapper = EsWrappers.lambdaQuery(LlmConversation.class)
                .lt("createTime.keyword", lastTime);
        llmConversationMapper.delete(wrapper);
        log.info("清理clearerMessage数据结束");
    }
}
