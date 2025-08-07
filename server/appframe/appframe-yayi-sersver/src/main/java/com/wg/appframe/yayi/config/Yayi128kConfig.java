package com.wg.appframe.yayi.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 调用雅意问答接口的配置
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Yayi128kConfig extends YayiConfigSupper {

    private static final long serialVersionUID = 4542691165464493290L;

    private String uri;
    private Boolean doSample;
    private Float temperature;
    private Float presencePenalty;
    private BigDecimal repetitionPenalty;
    private Float frequencyPenalty;
    private Integer maxNewTokens;
    private Float topP;
    private Integer topK;
    private Integer bestOf;
    private Integer n;
    private String model;
    private Float repetition_penalty;
    private Float encoder_repetition_penalty;
}
