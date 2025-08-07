package com.wg.appframe.yayi.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class VisualConfig extends YayiConfigSupper {

    private static final long serialVersionUID = 1930427314671951416L;

    private String uri;
    private String model;
    private Boolean stream;
    private Float topP;
    private Integer topK;
    private Float temperature;
    private Integer maxNewTokens;
    private Boolean doSample;
}
