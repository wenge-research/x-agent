package com.wenge.model.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "decision-one")
@Data
public class DecisionOneConfig {

    private String host;

    /**
     * 初始化推演会话
     */
    private String initApi;

    /**
     * 获取场景
     */
    private String scenariosApi;

    /**
     * 运行推演（按轮次）
     */
    private String nextRoundApi;

    /**
     * 获取推演计划
     */
    private String plans;

    /**
     * 推演会话websocket
     */
    private String websockt;

    /**
     * 导出推演会话
     */
    private String exportApi;

    /**
     * 获取推演会话状态
     */
    private String statusApi;

    /**
     * 导出推演会话
     */
    private String downloadFIleApi;

    /**
     * 获取推演的agents
     */
    private String agentsApi;

    /**
     * 获取推演环境
     */
    private String environmentApi;

    /**
     * 获取推演的interactions
     */
    private String interactionsApi;

    /**
     * 获取推演的actions
     */
    private String actionsApi;

    /**
     * 获取推演的决策
     */
    private String decisionsApi;

    /**
     * 获取推演的决策报告
     */
    private String reportApi;

    /**
     * 报告服务
     */
    private String reportBaseApi;

    /**
     * 总结推演
     */
    private String summaryApi;
}
