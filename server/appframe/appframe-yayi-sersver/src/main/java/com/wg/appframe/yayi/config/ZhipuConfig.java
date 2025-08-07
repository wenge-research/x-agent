package com.wg.appframe.yayi.config;

import lombok.Data;

import java.io.Serializable;

@Data
public class ZhipuConfig implements Serializable {

    private static final long serialVersionUID = 9130200020051214466L;

    private String host;
    private String appKey;
    private Boolean logEnabled;

}
