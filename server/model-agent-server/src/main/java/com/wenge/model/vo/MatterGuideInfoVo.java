package com.wenge.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * @Description:
 * @Author: Levi
 * @Date: 2024/7/15 14:23
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class MatterGuideInfoVo {
    @ApiModelProperty(value = "主键，自增id，没有业务作用")
    private Long id;

    @ApiModelProperty(value = "数据ID")
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
}