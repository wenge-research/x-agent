package com.wg.appframe.yayi.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 调用雅意问答接口的配置
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Yayi30BConfig extends YayiConfigSupper {

    private static final long serialVersionUID = -6071095343924945576L;

    private String uri;
    private Boolean doSample;
    private Float temperature;
    private Float presencePenalty;
    private Float frequencyPenalty;
    private Integer maxNewTokens;
    private Float topP;
    private Integer topK;
    private Integer n;
    private String model;
    private Integer bestOf;
    private Integer retryTimes;
    private Integer retryInterval;
    private Float repetition_penalty;
    private Float encoder_repetition_penalty;
}
