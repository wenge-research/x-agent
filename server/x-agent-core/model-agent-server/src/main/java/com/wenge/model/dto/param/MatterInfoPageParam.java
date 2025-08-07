package com.wenge.model.dto.param;

import cn.hutool.json.JSONObject;
import com.wenge.model.entity.MatterGuideFiled;
import com.wg.appframe.core.bean.WgPageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class MatterInfoPageParam extends WgPageInfo {


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

    @ApiModelProperty(value = "应用id")
    private String applicationId;

    private String nameCardPhone;

    private String startTime;

    private String endTime;

    private List<MatterGuideFiled> searchList;

    private String keyword;

}
