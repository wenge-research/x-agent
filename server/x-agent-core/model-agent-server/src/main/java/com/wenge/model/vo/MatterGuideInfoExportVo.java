package com.wenge.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description:
 * @Author: Levi
 * @Date: 2024/7/15 14:23
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatterGuideInfoExportVo {

    @ApiModelProperty(value = "事项类型")
    private String matterName;

    @ApiModelProperty(value = "用户姓名")
    private String userName;

    @ApiModelProperty(value = "身份证号")
    private String idCard;

    @ApiModelProperty(value = "联系电话")
    private String phone;

    @ApiModelProperty(value = "0 未审批  1 已审批")
    private String status;

    @ApiModelProperty(value = "创建时间")
    private String createTime;
}