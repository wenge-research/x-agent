package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ApplicationDatasetParam extends WgPageInfo {
    /** 主键，自增id，没有业务作用 */
    private Long id;

    /** 代表问题 */
    @ApiModelProperty(name = "代表问题")
    private String input;

    /** 代表回答 */
    @ApiModelProperty(name = "代表回答")
    private String output;

    /** 专家答案 */
    @ApiModelProperty(name = "专家答案")
    private String referenceOutput;

    /** 数据集id */
    @ApiModelProperty(name = "数据集id")
    private String datasetId;

    /** 部门id//用来数据隔离 */
    @ApiModelProperty(name = "部门id//用来数据隔离")
    private String deptId;

    /** 用户id//用来数据隔离 */
    @ApiModelProperty(name = "用户id//用来数据隔离")
    private String userId;

    /** 租户id//用来数据隔离 */
    @ApiModelProperty(name = "租户id//用来数据隔离")
    private String tenantId;

    /** 是否删除[1-删除,0-未删除] */
    @ApiModelProperty(name = "是否删除[1-删除,0-未删除]")
    private Long deleteFlag;

    /** 创建人 */
    @ApiModelProperty(name = "创建人")
    private String createUser;
    /**
     * 创建时间
     */
    @ApiModelProperty(name = "createTime", value = "创建时间")
    private String createTime;
    /** 更新人 */
    @ApiModelProperty(name = "更新人")
    private String updateUser;
    /**
     * 更新时间
     */
    @ApiModelProperty(name = "updateTime", value = "更新时间")
    private String updateTime;
}
