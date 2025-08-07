package com.wg.appframe.yayi.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 调用雅意问答接口的请求头
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class YayiConfig extends YayiConfigSupper {

    private static final long serialVersionUID = -4487269578835137474L;

    private String appKey;
    private String signatureMethod;
    private String appSecret;
    private String accept;
    private String contentType;
    private String host;
    private String method;
    private Boolean logEnabled;
}
