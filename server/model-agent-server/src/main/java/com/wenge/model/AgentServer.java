package com.wenge.model;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.dromara.easyes.starter.register.EsMapperScan;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.wenge.model", "com.wenge.oauth"})
@EnableScheduling
@MapperScan(basePackages = {"com.wenge.model.mapper", "com.wenge.oauth.mapper"})
@EsMapperScan("com.wenge.**.mapper.es")
@EnableDiscoveryClient
@EnableEncryptableProperties
@EnableFeignClients
public class AgentServer {
    public static void main(String[] args) {
        SpringApplication.run(AgentServer.class, args);
        System.out.println("============智能体后台服务启动成功==================");
    }
}