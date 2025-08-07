package com.wg.appframe.yayi.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class YayiHeader implements Serializable {

    private static final long serialVersionUID = 7365374866062187560L;

    private String appKey;
    private String signatureMethod;
    private String uri;
    private String contentType;
    private String accept;
}
