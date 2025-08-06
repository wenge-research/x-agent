package com.wenge.model.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wenge.model.utils.DateUtil;
import com.wg.appframe.core.dto.results.WGResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class MediaResult extends WGResult {

    /**
     * 媒体类型 1:视频 2:图片
     */
    private Integer mediaType;

    /**
     * 视频/图片ID
     */
    private String id;

    /**
     * 创建人名称
     */
    private String createUserName;

    /**
     * 标题
     */
    private String title;

    /**
     * 媒体URL
     */
    private String url;

    /**
     * 文件大小
     */
    private Long size;

    /**
     * 宽
     */
    private Integer width;

    /**
     * 高
     */
    private Integer height;

    /**
     * 时长
     */
    private Double duration;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = DateUtil.PATTERN_1, timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = DateUtil.PATTERN_1, timezone = "GMT+8")
    private Date updateTime;

    /**
     * 发布状态 0:未发布 1:已发布
     */
    private Integer publishedStatus;

    /**
     * 审核状态 0:未审核 1:审核通过 2:审核未通过
     */
    private Integer reviewStatus;

    /**
     * 宽高比
     */
    private String ratio;

    /**
     * 分辨率
     */
    private String resolution;


    /**
     * 描述
     */
    private String description;


}
