package com.wenge.model.dto.param;

import lombok.Data;

@Data
public class VoiceComponentInfoConfigUpdateParam {

    private static final long serialVersionUID = -2636192558690322065L;


    /**
     * 声音组件配置业务id
     */
    private String configId;

    /**
     * 声音组件配置常用标识 0-非常用 1-常用
     */
    private Integer frequenceUseFlag;

}
