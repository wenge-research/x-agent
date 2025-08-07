package com.wg.appframe.yayi.config;

import lombok.Data;

import java.io.Serializable;

/**
 * kimi对话接口
 */
@Data
public class SiliconFlowCompletionsConfig implements Serializable {

    private static final long serialVersionUID = 5263757567843210321L;

    private String uri;
    private Integer retryTimes;
    private Integer retryInterval;
    private String model;
    private Integer maxTokens;
    private String stop;
    private Float temperature;
    private Float topP;
    private Float topK;
    private Float frequencyPenalty;
    private Integer n;
    private String responseFormat;
    private Float presencePenalty;
}
