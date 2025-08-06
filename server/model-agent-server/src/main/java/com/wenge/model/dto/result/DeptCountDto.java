package com.wenge.model.dto.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DeptCountDto {

    /**
     * 部门名称
     */
    @ApiModelProperty(name = "DEPT_ID", value = "部门名称", dataType = "String")
    private String deptId;

    @ApiModelProperty(name = "count", value = "总数", dataType = "Long")
    private Integer count;

    @ApiModelProperty(name = "dataType", value = "类性", dataType = "Long")
    private String dataType;

}
