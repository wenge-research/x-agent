package com.wg.appframe.yayi.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class YayiSignature implements Serializable {

    private static final long serialVersionUID = 5438172736300263667L;

    private String requestMethod;
    private String accept;
    private String contentType;
    private String host;
    private String signatureMethod;
    private String appSecret;
}
