package com.wg.appframe.yayi.config;

import lombok.Data;

import java.io.Serializable;

@Data
public class DoubaoConfig implements Serializable {

    private static final long serialVersionUID = 3796172616010163544L;

    private String apiKey;

    private String host;

    private Boolean logEnabled;
}
