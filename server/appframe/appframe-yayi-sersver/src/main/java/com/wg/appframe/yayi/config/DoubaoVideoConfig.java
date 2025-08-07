package com.wg.appframe.yayi.config;

import lombok.Data;

import java.io.Serializable;

@Data
public class DoubaoVideoConfig implements Serializable {

    private static final long serialVersionUID = 2414923584321234703L;

    private String uri;
    private String model;
    private String callUri;
    private String callback_url;
    private String resolution;
    private String ratio;
    private Integer duration;
    private Integer framepersecond;
    private Boolean watermark;
    private Boolean camerafixed;
    private Integer seed;
}
