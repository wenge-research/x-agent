package com.wg.appframe.yayi.config;

import lombok.Data;

import java.io.Serializable;

/**
 * kimi对话接口
 */
@Data
public class KimiCompletionsConfig implements Serializable {

    private static final long serialVersionUID = -5484401606256100342L;

    private String uri;
    private String model;
    private Integer maxTokens;
    private Float temperature;
    private Float topP;
    private Integer n;
    private Float presencePenalty;
    private Float frequencyPenalty;
    private String stop;
    private Boolean stream;
    private Integer retryTimes;
    private Integer retryInterval;
    private String responseFormat;

}
