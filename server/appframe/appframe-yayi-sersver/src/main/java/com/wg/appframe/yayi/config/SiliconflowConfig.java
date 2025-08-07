package com.wg.appframe.yayi.config;

import lombok.Data;

import java.io.Serializable;

@Data
public class SiliconflowConfig implements Serializable {

    private static final long serialVersionUID = 2242373060342495755L;

    private String host;
    private String appKey;
    private Boolean logEnabled;

}
