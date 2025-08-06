package com.wenge.model.dto.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AddAnalysisDataResult {

    @ApiModelProperty(name = "requestId", value = "请求id", dataType = "String")
    private String requestId;
}

