package com.wenge.model.dto.result;

import com.mybatisflex.annotation.Column;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DeptInfoDto {

    /**
     * 部门id
     */
    @Column("dept_id")
    @ApiModelProperty(name = "deptId", value = "部门id", dataType = "String")
    private String deptId;

    /**
     * 部门名称
     */
    @ApiModelProperty(name = "deptName", value = "部门名称", dataType = "String")
    private String deptName;
}
