package com.wg.appframe.yayi.config;

import lombok.Data;

import java.io.Serializable;

@Data
public class WenxinConfig implements Serializable {

    private static final long serialVersionUID = -4652793429747459321L;

    private String host;
    private String appKey;
    private String appSecret;
    private Boolean logEnabled;

}
