package com.wenge.model.dto.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class KnowledgeCountDto {

    /**
     * 部门名称
     */
    @ApiModelProperty(name = "dataType", value = "数据类型 wj文件、wdd问答对、url、jgh 结构化", dataType = "String ")
    private String dataType;

    @ApiModelProperty(name = "count", value = "总数", dataType = "Long")
    private Integer count;

    /**
     * 部门名称
     */
    @ApiModelProperty(name = "extendedType", value = "扩展字段", dataType = "String ")
    private String extendedType;
}
