package com.wenge.model.dto.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewResult {

    /**
     * 项
     */
    @ApiModelProperty(name = "term", value = "项", dataType = "String")
    private String term;

    /**
     * 结果
     */
    @ApiModelProperty(name = "result", value = "审查结果， 0未通过 1通过", dataType = "Integer")
    private String result;

}