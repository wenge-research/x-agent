package com.wg.appframe.yayi.config;

import lombok.Data;

import java.io.Serializable;

@Data
public class MinimaxConfig implements Serializable {

    private static final long serialVersionUID = 4114379567038410954L;

    private String apiKey;

    private String host;

    private Boolean logEnabled;
}
