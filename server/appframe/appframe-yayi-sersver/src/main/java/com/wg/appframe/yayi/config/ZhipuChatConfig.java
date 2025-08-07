package com.wg.appframe.yayi.config;

import lombok.Data;

import java.io.Serializable;

/**
 * kimi对话接口
 */
@Data
public class ZhipuChatConfig implements Serializable {

    private static final long serialVersionUID = 2198634262216383193L;

    private String uri;
    private String model;
    private Boolean doSample;
    private Boolean stream;
    private Float temperature;
    private Float topP;
    private Integer maxTokens;
    private String stop;
    private Object toolChoice;

}
