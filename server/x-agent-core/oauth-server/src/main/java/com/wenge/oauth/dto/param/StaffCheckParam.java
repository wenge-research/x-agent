package com.wenge.oauth.dto.param;

import com.mybatisflex.annotation.Column;
import com.wenge.oauth.enums.UserTypeEnum;
import com.wg.appframe.core.dto.params.WGParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class StaffCheckParam extends WGParam {

    private static final long serialVersionUID = 1102953655961772172L;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 审核意见
     */
    private String checkOpinion;

    /**
     * 审核状态
     */
    private String checkStatus;

    /**
     * 用户真实姓名
     */
    @ApiModelProperty(name = "userName", value = "用户真实姓名", dataType = "String")
    private String userName;

    /**
     * 用户类型，具体查看 {@link UserTypeEnum}
     */
    @Column("user_type")
    private String userType;

    /**
     * 部门id
     */
    @ApiModelProperty(name = "deptId", value = "部门id", dataType = "String")
    private String deptId;

    /**
     * 工作职务
     */
    @ApiModelProperty(name = "workPosition", value = "工作职务", dataType = "String")
    private String workPosition;

    /**
     * 工作状态，在职-on，离职-off，调离-transferred，休假-retired，病休-sick，退休-retire
     */
    @ApiModelProperty(name = "workStatus", value = "工作状态，在职-on，离职-off，调离-transferred，休假-retired，病休-sick，退休-retire", dataType = "String")
    private String workStatus;

    /**
     * 手机号
     */
    @ApiModelProperty(name = "phone", value = "手机号", dataType = "String")
    private String phone;

    /**
     * 主要工作内容
     */
    @ApiModelProperty(name = "mainDuty", value = "主要工作内容", dataType = "String")
    private String mainDuty;

    /**
     * 座机号
     */
    @ApiModelProperty(name = "landline", value = "座机号", dataType = "String")
    private String landline;
}
