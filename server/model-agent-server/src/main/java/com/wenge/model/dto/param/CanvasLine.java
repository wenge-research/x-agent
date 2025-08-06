package com.wenge.model.dto.param;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
public class CanvasLine implements Serializable {

    private static final long serialVersionUID = -5198914545337181602L;

    private String id;
    private String shape = "dag-edge";
    private JSONObject attrs = JSONUtil.parseObj("{\"line\":{\"style\":{\"animation\":\"\"}}}");
    private Integer zIndex = 1000;
    private NodeCar source;
    private NodeCar target;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NodeCar implements Serializable {

        private static final long serialVersionUID = -7292506813267249812L;

        private String cell;
        private String port;
    }

}
