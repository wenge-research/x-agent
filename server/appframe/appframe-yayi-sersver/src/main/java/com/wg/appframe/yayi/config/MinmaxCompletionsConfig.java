package com.wg.appframe.yayi.config;

import lombok.Data;

import java.io.Serializable;

@Data
public class MinmaxCompletionsConfig implements Serializable {

    private static final long serialVersionUID = -352579184071825425L;

    private String uri;
    private String model;
    private Float temperature;
    private Float top_p;
    private Integer max_tokens;
    private Boolean stream;
    private Boolean mask_sensitive_info;
    private String tool_choice;

    private Integer retryTimes;
    private Integer retryInterval;


}
