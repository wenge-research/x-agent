package com.wenge.model.workflow.component.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class InterceptWordResult implements Serializable {

    private static final long serialVersionUID = -4756256521483638403L;


    /**
     * 是否为敏感词 "true"-是， “false”-否
     */
    private String sensitiveFlag;
}
