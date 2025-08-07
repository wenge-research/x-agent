package com.wg.appframe.yayi.config;

import lombok.Data;

import java.io.Serializable;

@Data
public class KimiConfig implements Serializable {

    private static final long serialVersionUID = 1207442465207203573L;

    private String host;
    private String appKey;
    private Boolean logEnabled;

}
