package com.wenge.model.utils;

import cn.hutool.core.thread.ExecutorBuilder;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wenge.model.dto.result.ScreenshotResult;
import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;
import org.dromara.easyes.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Component
public class ScreenshotUtils {

    @Value("${screenshot.uploadUrl:}")
    private String uploadUrl;

    @Value("${screenshot.api:}")
    private String screenshotApi;

    public ScreenshotResult screenshot(String taskId, List<String> urls) {
        if (Collections.isEmpty(urls)) {
            log.warn("URL列表为空");
            return new ScreenshotResult();
        }

        ThreadPoolExecutor executor = ExecutorBuilder.create().setCorePoolSize(4).setMaxPoolSize(16).setKeepAliveTime(30000).setWorkQueue(new LinkedBlockingQueue<>(64)).build();

        Vector<ScreenshotResult.Detail> detailList = new Vector<>();
        for (String url : urls) {
            executor.execute(() -> {
                JSONObject request = new JSONObject();
                request.set("url", url);
                request.set("uploadUrl", uploadUrl);
                try {
                    log.info("请求截图接口:{},参数：{}", screenshotApi, request);
                    String post = HttpUtil.createPost(screenshotApi)
                            .setConnectionTimeout(1000 * 60)
                            .setReadTimeout(1000 * 60 * 10)
                            .timeout(1000 * 60 * 10)
                            .body(JSONUtil.toJsonStr(request))
                            .execute()
                            .body();
                    log.info("返回截图接口:{}", post);
                    if (StringUtils.isNotBlank(post) && JSONUtil.isTypeJSONObject(post)) {
                        JSONObject entries = JSONUtil.parseObj(post);
                        if ("success".equals(entries.getStr("status"))) {
                            ScreenshotResult.Detail bean = entries.toBean(ScreenshotResult.Detail.class);
                            detailList.add(bean);
                        }
                    }
                } catch (Exception e) {
                    log.error("请求失败 URL:{} 错误:{}", url, e.getMessage());
                }
            });
        }

        long maxTime = 1000 * 60 * 10;
        long start = System.currentTimeMillis();
        try {
            while (true) {
                if (executor.getActiveCount() == 0
                        && executor.getQueue().isEmpty()
                        && executor.getCompletedTaskCount() >= urls.size()) {
                    break;
                }
                // 超时退出
                if (System.currentTimeMillis() - start > maxTime) {
                    executor.shutdownNow();
                    break;
                }
                Thread.sleep(100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        executor.shutdown();

        ScreenshotResult result = new ScreenshotResult();
        result.setDetailList(detailList);
        result.setTaskId(taskId);
        return result;

    }

}
