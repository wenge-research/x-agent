package com.wenge.model.vo;


import lombok.Data;

import java.util.List;

@Data
public class ParserUrlVo {
    private String content;
    private String message;
    private String title;
    private String url;
    private Integer status_code;
    private List<ParserUrlVo> subUrl;
}
