package com.wg.appframe.yayi.config;

import lombok.Data;

import java.io.Serializable;

@Data
public class MinmaxImageConfig implements Serializable {

    private static final long serialVersionUID = 4767801474133036866L;

    private String uri;
    private String model;

    private String aspect_ratio;
    private Integer width;
    private Integer seed;
    private Integer height;
    private Integer n;
    private String response_format;
    private Boolean prompt_optimizer;

    private Integer retryTimes;
    private Integer retryInterval;


}
