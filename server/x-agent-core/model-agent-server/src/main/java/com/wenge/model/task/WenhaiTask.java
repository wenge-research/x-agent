package com.wenge.model.task;

import com.wenge.model.service.WenhaiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 定时拉取舆情
 */
@Slf4j
@Controller
@RequestMapping("/agentTask")
public class WenhaiTask {

    @Autowired
    private WenhaiService longyangService;

    /**
     * 拉取热点定时器
     */
    @RequestMapping("/sentiment")
    // // @Scheduled(cron = "${longyanGpt.sentiment.corn}")
    public void sentiment() {
        longyangService.sentiment();
    }


}
