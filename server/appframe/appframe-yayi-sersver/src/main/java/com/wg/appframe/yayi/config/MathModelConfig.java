package com.wg.appframe.yayi.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MathModelConfig extends YayiConfigSupper {

    private static final long serialVersionUID = 7500588766290789470L;

    private String uri;
    private Float temperature;
    private Boolean stream;
    private Integer maxTrySteps;
    private Integer maxNewTokens;
    private Boolean doSample;
    private Float presencePenalty;
    private Float frequencyPenalty;
    private Float topP;
    private Float topK;
    private Integer n;
}
