package com.wg.appframe.yayi.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class VolcengineParam implements Serializable {

    private static final long serialVersionUID = 7653482424420206619L;

    /**
     * 自定义appKey
     */
    private String appKey;

    /**
     * 自定义appSecret
     */
    private String appSecret;

    private String uri;
    private Integer connectTimeout;
    private Integer readTimeout;

    private String region;
    private String serve;
}
