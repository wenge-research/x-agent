package com.wenge.task.scheduled;

import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时汇总知识库数据数据
 */
// @RestController
// @Api(tags = "知识库数据汇总")
// @RequestMapping("/analysis/task")
@Slf4j
@Component
public class KnowledgeDataAnalysisScheduled {

    @Value("${task.agentx.host}")
    private String agentxHost;

    // @RequestMapping("/summaryData")
    @Scheduled(cron = "${task.analysis.cron}")
    public void summaryData() {
        String url = agentxHost + "/agentTask/analysis/task/summaryData";
        log.info("开始执行知识库数据汇总任务");
        String s = HttpUtil.get(url);
        log.info("结束执行知识库数据汇总任务{}", s);
    }
}
