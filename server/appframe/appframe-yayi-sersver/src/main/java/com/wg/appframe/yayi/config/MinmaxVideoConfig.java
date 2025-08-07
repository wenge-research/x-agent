package com.wg.appframe.yayi.config;

import lombok.Data;

import java.io.Serializable;

@Data
public class MinmaxVideoConfig implements Serializable {

    private static final long serialVersionUID = 6404868151836104981L;

    private String uri;

    private String model;
    private Boolean prompt_optimizer;
    private Integer duration;
    private String resolution;
    private String first_frame_image;
    private String callback_url;
    private String searchTaskUri;
    private String downloadFileUri;

    private Integer retryTimes;
    private Integer retryInterval;

}
