package com.wg.appframe.yayi.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class YayiParam implements Serializable {

    private static final long serialVersionUID = 7653482424420206619L;

    private String id;

    /**
     * 自定义appKey
     */
    private String appKey;

    /**
     * 自定义appSecret
     */
    private String appSecret;

    private Integer retryTimes;
    private Integer retryInterval;
    private String uri;
    private Integer connectTimeout;
    private Integer readTimeout;
}
