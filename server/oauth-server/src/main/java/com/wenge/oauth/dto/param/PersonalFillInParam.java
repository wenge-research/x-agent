package com.wenge.oauth.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PersonalFillInParam extends WGParam {

    private static final long serialVersionUID = -5528928722708792513L;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户类型，resident：微信公众号的普通居民，staff：工作人员
     */
    @ApiModelProperty(name = "userType", value = "用户类型，resident：微信公众号的普通居民，staff：工作人员", dataType = "String")
    private String userType;

    /**
     * 用户真实姓名
     */
    @ApiModelProperty(name = "userName", value = "用户真实姓名", dataType = "String")
    private String userName;

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
     * 主要工作内容
     */
    @ApiModelProperty(name = "mainDuty", value = "主要工作内容", dataType = "String")
    private String mainDuty;

    /**
     * 手机号
     */
    @ApiModelProperty(name = "phone", value = "手机号", dataType = "String")
    private String phone;

    /**
     * 工作状态，在职-on，离职-off，调离-transferred，休假-retired，病休-sick，退休-retire
     */
    @ApiModelProperty(name = "workStatus", value = "工作状态，在职-on，离职-off，调离-transferred，休假-retired，病休-sick，退休-retire", dataType = "String")
    private String workStatus;

    /**
     * 证件号码
     */
    private String idNo;

    /**
     * 座机
     */
    private String landline;
}
