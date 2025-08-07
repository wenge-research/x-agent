package com.wg.appframe.yayi.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChartConfig extends YayiConfigSupper {

    private static final long serialVersionUID = 8688568298676360871L;

    private String uri;
    private String model;
    private Boolean stream;
    private Float topP;
    private Float temperature;
    private Integer maxNewTokens;
    private Boolean doSample;
}
