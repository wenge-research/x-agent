package com.wg.appframe.yayi.config;

import lombok.Data;

import java.io.Serializable;

@Data
public class DoubaoImageConfig implements Serializable {

    private static final long serialVersionUID = 2414923584321234703L;

    private String uri;
    private String model;
    private String response_format;
    private String size;
    private Integer seed;
    private Float guidance_scale;
    private Boolean watermark;
}
