package com.wenge.model.dto.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class SseEmitterParam implements Serializable {

    private static final long serialVersionUID = -3164832769406209656L;

    private String clientId;

    /**
     * 请求是来源其他节点
     */
    private String fromNode;
}
