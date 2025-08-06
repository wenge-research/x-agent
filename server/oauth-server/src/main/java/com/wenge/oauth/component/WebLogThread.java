package com.wenge.oauth.component;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @description: 异步读取日志文件 写入队列中
 * @author: caohaifeng
 * @date: 2024/8/23 14:57
 **/
@Component
public class WebLogThread implements Runnable{
    private static final Logger LOGGER = LoggerFactory.getLogger(WebLogThread.class);
    public static Queue<String> logQueue = new LinkedBlockingQueue<String>();

    @PostConstruct
    public void init(){
        LOGGER.info(">>> Log Thread init : T-" + "WebLogThread");
        new Thread(this).start();
    }

    @Override
    public void run() {
        while(true){
            try{
                File dir = new File("operlog");
                if(!dir.exists()){
                    Thread.sleep(1000);
                    continue;
                }
                File[] logFiles = dir.listFiles();
                if(logFiles.length == 1){
                    Thread.sleep(1000);
                }
                for(File log : logFiles){
                    if(log.getName().equals("oper.log")){
                        continue;
                    }else{
                       List<String> lines = FileUtils.readLines(log);
                       for(String li : lines){
                           if(logQueue.size()>1000){
                               Thread.sleep(1000);
                               LOGGER.info("日志队列中缓存超过1000条，休眠1秒，size="+logQueue.size());
                           }
                           logQueue.add(li);
//                           LOGGER.info("日志队列size" + logQueue.size());
                       }
                    }
                    log.delete();
                    LOGGER.info("日志传输完成，删除文件："+log.getName());
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
