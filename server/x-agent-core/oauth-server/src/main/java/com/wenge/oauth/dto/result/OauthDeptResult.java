package com.wenge.oauth.dto.result;

import com.mybatisflex.annotation.Column;
import com.wenge.oauth.entity.OauthDept;
import com.wg.appframe.core.dto.results.WGResult;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class OauthDeptResult extends WGResult {

    private static final long serialVersionUID = 8530426583283388101L;


    /**
     * 创建时间
     */
    @ApiModelProperty(name = "createTime", value = "创建时间", dataType = "String")
    private String createTime;

    /**
     * 创建人
     */
    @ApiModelProperty(name = "createUser", value = "创建人", dataType = "String")
    private String createUser;

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
     * 部门地址
     */
    @ApiModelProperty(name = "deptLocation", value = "部门地址", dataType = "String")
    private String deptLocation;

    /**
     * 部门名称
     */
    @ApiModelProperty(name = "deptName", value = "部门名称", dataType = "String")
    private String deptName;

    /**
     * 排序
     */
    @ApiModelProperty(name = "deptOrder", value = "排序", dataType = "Integer")
    private Integer deptOrder;

    /**
     * 主键自增
     */
    @ApiModelProperty(name = "id", value = "主键自增", dataType = "Long")
    private Long id;

    /**
     * 父级部门id
     */
    @ApiModelProperty(name = "pid", value = "父级部门id", dataType = "String")
    private String pid;

    /**
     * 备注
     */
    @ApiModelProperty(name = "remark", value = "备注", dataType = "String")
    private String remark;

    /**
     * 租户id
     */
    @ApiModelProperty(name = "tenantId", value = "租户id", dataType = "String")
    private String tenantId;

    /**
     * 更新时间
     */
    @ApiModelProperty(name = "updateTime", value = "更新时间", dataType = "String")
    private String updateTime;

    /**
     * 更新人
     */
    @ApiModelProperty(name = "updateUser", value = "更新人", dataType = "String")
    private String updateUser;

    /**
     *  子部门
     */
    private List<OauthDeptResult> children;
}
