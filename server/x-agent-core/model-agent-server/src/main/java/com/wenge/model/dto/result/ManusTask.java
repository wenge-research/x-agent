package com.wenge.model.dto.result;

import lombok.Data;

import java.io.Serializable;

@Data
public class ManusTask implements Serializable {

    private static final long serialVersionUID = 4776737615988272640L;

    /**
     * 任务 id
     */
    private String taskId;

    /**
     * 任务内容
     */
    private String content;
}
