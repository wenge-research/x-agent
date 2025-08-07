package com.wenge.model.dto.param;

import com.mybatisflex.annotation.Column;
import com.wg.appframe.core.bean.WgPageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ServerPublishAuditPageParam extends WgPageInfo {

    private static final long serialVersionUID = 3514272693195916373L;

    @ApiModelProperty(value = "消息来源 1-应用审核 2-插件审核 3-其他")
    private Integer messageSource;

    @ApiModelProperty(value = "发布类型(分类)")
    private String publishType;

    @ApiModelProperty(value = "提交时间（开始） yyyy-MM-dd HH:mm:ss")
    private String submitTimeStart;

    @ApiModelProperty(value = "提交时间（结束） yyyy-MM-dd HH:mm:ss")
    private String submitTimeEnd;

    @ApiModelProperty(value = "应用名称检索-模糊")
    private String applicationName;

    @ApiModelProperty(value = "审核状态 2-待审核 1-审核通过（已审核） 0-驳回（已审核）")
    private List<Integer> auditStatusList;






    
}
