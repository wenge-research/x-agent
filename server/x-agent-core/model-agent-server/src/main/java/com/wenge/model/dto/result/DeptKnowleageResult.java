package com.wenge.model.dto.result;

import com.wenge.oauth.dto.result.OauthDeptResult;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;

import java.util.List;

/**
 * 部门知识库统计信息
 */
@Data
public class DeptKnowleageResult {

    // 部门数量
    @ApiModelProperty(name = "部门数量", value = "部门数量", dataType = "Long")
    private Integer deptCount;

    // 子部门数量
    @ApiModelProperty(name = "子部门数量", value = "子部门数量", dataType = "Long")
    private Integer childrenCount;

    /**
     *  子部门
     */
    @ApiModelProperty(name = "部门统计数据", value = "部门统计数据", dataType = "List")
    private List<DeptKnowleageDto> deptDataList;

}
