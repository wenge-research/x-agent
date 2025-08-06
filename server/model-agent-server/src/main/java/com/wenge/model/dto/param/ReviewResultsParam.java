package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ReviewResultsParam extends WGParam {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(name = "id", value = "自增id", dataType = "Long")
    private Long id;

    /**
     * 应用id(如果为空，则是通用的，如果不为空，则为指定应用的)
     */
    @ApiModelProperty(name = "applicationId", value = "应用id(如果为空，则是通用的，如果不为空，则为指定应用的)", dataType = "String")
    private String applicationId;

    @ApiModelProperty(name = "terms", value = "审查应用每项结果")
    private List<ReviewResult> terms;

    /**
     * 招标文件链接
     */
    @ApiModelProperty(name = "biddingFileUrl", value = "文件链接", dataType = "String")
    private String biddingFileUrl;

    /**
     * 投标文件链接
     */
    @ApiModelProperty(name = "bidFileUrl", value = "投标文件链接", dataType = "String")
    private String bidFileUrl;

    /**
     * 创建时间
     */
    @ApiModelProperty(name = "createTime", value = "创建时间", dataType = "String")
    private String createTime;


}
