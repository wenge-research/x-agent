package com.wenge.task;


import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.wenge.task"})
@EnableScheduling
@EnableDiscoveryClient
@EnableEncryptableProperties
@EnableFeignClients
public class TaskServer {
    public static void main(String[] args) {
        SpringApplication.run(TaskServer.class, args);
        System.out.println("============智能体定时任务服务启动成功==================");
    }
}