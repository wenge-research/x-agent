package com.wg.appframe.yayi.config;

import lombok.Data;

import java.io.Serializable;

@Data
public class YayiConfigSupper implements Serializable {

    private static final long serialVersionUID = -5589117444959846675L;

    private Integer retryTimes;
    private Integer retryInterval;
}
