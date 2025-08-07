package com.wenge.model.dto.param.wenhai;

import lombok.Data;

@Data
public class WenHaiParameter {
    private WenHaiHeader header;

    private WenHaiBody body;

    private String url;
}
