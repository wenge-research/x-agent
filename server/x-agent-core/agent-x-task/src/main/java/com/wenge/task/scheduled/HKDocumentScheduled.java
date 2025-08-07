package com.wenge.task.scheduled;

import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HKDocumentScheduled {

    @Value("${task.agentx.host}")
    private String agentxHost;

    /**
     * 关芯客服对话功能划分
     */
    // @RequestMapping("/funDivision")
    @Scheduled(cron = "${task.hkDocument.cron}")
    public void funDivision() {
        String url = agentxHost + "/agentTask/hkDocument/hkDocumentAnalysis";
        log.info("HKDocumentScheduled hkDocumentAnalysis start");
        String s = HttpUtil.get(url);
        log.info("HKDocumentScheduled hkDocumentAnalysis end{}", s);
    }
}
