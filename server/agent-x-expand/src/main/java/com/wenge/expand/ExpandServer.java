package com.wenge.expand;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.wenge.expand"})
@EnableDiscoveryClient
@EnableEncryptableProperties
@EnableFeignClients
public class ExpandServer {
    public static void main(String[] args) {
        SpringApplication.run(ExpandServer.class, args);
        System.out.println("============智能体扩展模块启动成功==================");
    }
}