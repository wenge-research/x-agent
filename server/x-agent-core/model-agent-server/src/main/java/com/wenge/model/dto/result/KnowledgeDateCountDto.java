package com.wenge.model.dto.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class KnowledgeDateCountDto {

    /**
     * 部门名称
     */
    @ApiModelProperty(name = "date", value = "日期", dataType = "String ")
    private String date;

    @ApiModelProperty(name = "totalCount", value = "总数", dataType = "Long")
    private Integer totalCount;

}
