package com.wenge.model.vo;


import lombok.Data;

import java.util.List;

@Data
public class ParserDocumentVo {

    private String file_url;
    private List<ParserDocumentInfoVo> file_text;

}
