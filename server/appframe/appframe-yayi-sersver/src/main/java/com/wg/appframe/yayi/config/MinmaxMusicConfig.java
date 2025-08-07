package com.wg.appframe.yayi.config;

import lombok.Data;

import java.io.Serializable;

@Data
public class MinmaxMusicConfig implements Serializable {

    private static final long serialVersionUID = 7346370113560861159L;

    private String uri;

    private String model;
    private String refer_voice;
    private String refer_instrumental;
    private String refer_vocal;
    private String lyrics;
    private Boolean stream;


    private Integer retryTimes;
    private Integer retryInterval;

}
