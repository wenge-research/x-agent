package com.wenge.model.dto.param;

import com.mybatisflex.annotation.Column;
import com.wg.appframe.core.bean.WgPageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class MatterGuideParam extends WgPageInfo {

    private static final long serialVersionUID = -7468622403799779577L;

    @ApiModelProperty(value = "应用ID")
    private String applicationId;

    @ApiModelProperty(value = "事项名称")
    private String matterName;

    @ApiModelProperty(value = "关键字")
    private String keyword;

    @ApiModelProperty(value = "需要删除的事项IDS")
    private List<String> delMatterIds;

    @ApiModelProperty(value = "是否显示，是/否")
    private String showFlag;

}
