package com.wenge.model.dto.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class GenerateParam implements Serializable {

    private static final long serialVersionUID = 603264417259027527L;

    /**
     * 客户端id
     */
    protected String clientId;

    /**
     * 问题
     */
    protected String question;


}
