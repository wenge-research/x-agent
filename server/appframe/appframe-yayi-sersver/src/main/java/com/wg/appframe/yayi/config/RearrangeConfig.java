package com.wg.appframe.yayi.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RearrangeConfig extends YayiConfigSupper{

    private static final long serialVersionUID = 8445390274732898512L;

    private String uri;
    private Integer n;
    private String function;
    private Integer batchSize;
}
