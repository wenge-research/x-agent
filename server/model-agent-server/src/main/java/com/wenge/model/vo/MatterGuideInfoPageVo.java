package com.wenge.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Description:
 * @Author: Levi
 * @Date: 2024/7/15 14:23
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatterGuideInfoPageVo {
    @ApiModelProperty(value = "主键，自增id，没有业务作用")
    private String id;

    @ApiModelProperty(value = "事项id")
    private String matterId;

    @ApiModelProperty(value = "事项名称")
    private String matterName;

    @ApiModelProperty(value = "数据ID")
    private String infoId;

    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "用户姓名")
    private String userName;

    @ApiModelProperty(value = "身份证号")
    private String idCard;

    @ApiModelProperty(value = "联系电话")
    private String phone;

    @ApiModelProperty(value = "0 未审批  1 已审批")
    private Integer status;

    @ApiModelProperty(value = "审批描述信息")
    private String remark;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createTime;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String updateTime;
}