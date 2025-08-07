package com.wenge.model.dto.result;

import lombok.Data;

import java.io.Serializable;

@Data
public class EndNodeStream implements Serializable {

    private static final long serialVersionUID = 3468763127517082082L;

    /**
     * 变量名
     */
    private String variable;

    /**
     * 推送的内容
     */
    private String content;

    /**
     * 推送的状态 VariableStatusEnum
     */
    private String status;

    /**
     * 推送的顺序
     */
    private int sortOrder;

    /**
     * 1:推流答案，2：推流推理
     */
    private String type;

}
