package com.wg.appframe.yayi.config;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeepSeekConfig implements Serializable {

    private static final long serialVersionUID = -5390675541544178006L;

    private String host;
    private String appKey;
    private Boolean logEnabled;
}
