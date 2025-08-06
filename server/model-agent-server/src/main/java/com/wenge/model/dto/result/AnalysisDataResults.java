package com.wenge.model.dto.result;

import com.wenge.model.entity.YouyaDataEntries;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class AnalysisDataResults  implements Serializable {

    private static final long serialVersionUID = -1;

    @ApiModelProperty(name = "status", value = "任务状态 0 待解析 1 解析完成", dataType = "Integer")
    private int status;

    @ApiModelProperty(name = "videoUrl", value = "任务状态 0 待解析 1 解析完成", dataType = "String")
    private String videoUrl;

    @ApiModelProperty(name = "requestId", value = "请求id", dataType = "String")
    private String requestId;

    /**
     * 创建时间
     */
    @ApiModelProperty(name = "createTime", value = "创建时间", dataType = "String")
    private String createTime;

    /**
     * 创建时间
     */
    @ApiModelProperty(name = "endTime", value = "解析完时间", dataType = "String")
    private String endTime;

    @ApiModelProperty(name = "dataEntriesList", value = "解析完数据泪飙", dataType = "ArrayList")
    List<YouyaDataEntries> dataEntriesList = new ArrayList<>();
}
