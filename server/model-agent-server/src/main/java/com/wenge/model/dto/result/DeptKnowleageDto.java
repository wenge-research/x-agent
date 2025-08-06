package com.wenge.model.dto.result;

import com.mybatisflex.annotation.Column;
import com.wenge.oauth.dto.result.OauthDeptResult;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class DeptKnowleageDto {

    /**
     * 部门编码
     */
    @ApiModelProperty(name = "deptCode", value = "部门编码", dataType = "String")
    private String deptCode;

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

    /**
     *  子部门
     */
    private List<DeptKnowleageDto> children;

    @ApiModelProperty(name = "deptName", value = "总数", dataType = "Long")
    private Integer allCount;

    @ApiModelProperty(name = "deptName", value = "文档数据", dataType = "Long")
    private Integer wjCount;

    @ApiModelProperty(name = "QA对", value = "部门名称", dataType = "Long")
    private Integer wddCount;

    @ApiModelProperty(name = "deptName", value = "url链接数据", dataType = "Long")
    private Integer urlCount;

    @ApiModelProperty(name = "deptName", value = "结构化数据", dataType = "Long")
    private Integer jghCount;

}
