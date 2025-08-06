package com.wenge.model.dto.param;

import lombok.Data;

@Data
public class CopyAppParam {

    private static final long serialVersionUID = -2636192558690322065L;

    /**
     * 应用id
     */
    private String applicationId;

    /**
     * 复制应用标识： 0-从应用里面复制 1-从应用商店里面复制
     */
    private Integer copyFlag;

}
