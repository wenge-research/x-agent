package com.wenge.model.dto.param;

import com.mybatisflex.annotation.Column;
import com.wenge.model.entity.File;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FileResult extends File {

    @ApiModelProperty(name = "readNum", value = "浏览次数", dataType = "Integer")
    private Integer readNum;

    @ApiModelProperty(name = "likeNum", value = "点赞次数", dataType = "Integer")
    private Integer likeNum;

    @ApiModelProperty(name = "subNum", value = "订阅数", dataType = "Integer")
    private Integer subNum;

    @ApiModelProperty(name = "isSub", value = "是否订阅 1 订阅 0 非订阅", dataType = "String")
    private String isSub;
}
