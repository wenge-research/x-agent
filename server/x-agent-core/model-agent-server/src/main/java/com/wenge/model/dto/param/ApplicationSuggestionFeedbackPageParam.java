package com.wenge.model.dto.param;

import com.mybatisflex.annotation.Column;
import com.wg.appframe.core.bean.WgPageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ApplicationSuggestionFeedbackPageParam extends WgPageInfo {

    private static final long serialVersionUID = 3514272693195916373L;

    @ApiModelProperty(value = "应用名称检索-模糊")
    private String applicationName;

    @ApiModelProperty(value = "类型-使用建议,BUG反馈,操作体验,其他反馈")
    private String type;

    @ApiModelProperty(value = "提交时间（开始） yyyy-MM-dd HH:mm:ss")
    private String createTimeStart;

    @ApiModelProperty(value = "提交时间（结束） yyyy-MM-dd HH:mm:ss")
    private String createTimeEnd;
    
}
