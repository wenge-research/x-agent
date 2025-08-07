package com.wg.appframe.yayi.config;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * kimi对话接口
 */
@Data
public class WenxinChatConfig implements Serializable {

    private static final long serialVersionUID = 6560745921894428004L;

    private String uri;
    private Float temperature;
    private Float topP;
    private Float penaltyScore;
    private Boolean stream;
    private Boolean enableSystemMemory;
    private String systemMemoryId;
    private String system;
    private List<String> stop;
    private Boolean disableSearch;
    private Boolean enableCitation;
    private Boolean enableTrace;
    private Integer maxOutputTokens;
    private String responseFormat;
    private String userId;
}
