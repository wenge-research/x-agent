package com.wenge.model.dto.param;

import lombok.Data;

import java.util.List;

@Data
public class YouyaApiCallbackParam {

    private String id;
    private int code;
    private String msg;
    private List<YouyaCallbackDataParam> data;
    private float time;
}
