package com.wenge.model.dto.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MatterInfoUpdateParam {

    @ApiModelProperty(value = "用户ID")
    private String infoId;
    @ApiModelProperty(value = "事项id")
    private String matterId;
    @ApiModelProperty(value = "事项名称")
    private String matterName;

    @ApiModelProperty(value = "用户ID")
    private String userId;
    @ApiModelProperty(value = "用户name")
    private String userName;

    @ApiModelProperty(value = "身份证号")
    private String idCard;

    @ApiModelProperty(value = "0 未审批  1 已审批")
    private Integer status;

    @ApiModelProperty(value = "审批描述信息")
    private String remark;
}
