package com.wenge.oauth.component;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.wenge.oauth.entity.UmsOperation;
import com.wenge.oauth.mapper.es.UmsOperationMapper;
import com.wenge.oauth.service.UmsOperationService;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import javax.annotation.PostConstruct;

/**
 * @description: 异步多线程消费日志队列数据写入es
 * @author: caohaifeng
 * @date: 2024/8/23 14:59
 **/
@Component
public class WebLogPostThread implements Runnable{
    private static final Logger LOGGER = LoggerFactory.getLogger(WebLogPostThread.class);

    @Autowired
    private UmsOperationService umsOperationService;

    @PostConstruct
    public void init(){
        for (int i = 0; i < 2; i++) {
            LOGGER.info(">>> 日志提交线程 init : T-" + i);
            new Thread(this).start();
        }
    }

    @Override
    public void run() {
        while(true){
               try {
                   String logLine = WebLogThread.logQueue.poll();
                   if(logLine == null){
                       Thread.sleep(5000);
//                       LOGGER.info("剩余日志队列获取为空，休眠5秒");
                   }else{
                       try {
                           UmsOperation umsOperation = JSON.parseObject(logLine, UmsOperation.class);
                           umsOperationService.addUmsOperationg(umsOperation);
//                           System.out.println("插入日志记录成功");
                       }catch (Exception e){
                           e.printStackTrace();
                           LOGGER.error("日志格式转化异常，请检查日志文件格式！！！");
                       }
//                       LOGGER.info("剩余日志队列size，"+WebLogThread.logQueue.size());
                   }
               } catch (Exception e) {
                   e.printStackTrace();
               }
        }
    }
}
