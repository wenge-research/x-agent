package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ImageInfoParam extends WgPageInfo {

    /**
     * 图片标题
     */
    private String topic;

    /**
     * 发布状态(0:未发布,1:已发布)
     */
    private Integer publishedStatus;

    /**
     * 审核状态(0:未审核,1:审核通过,2:审核不通过)
     */
    private Integer reviewStatus;

    /**
     * 启用状态(0:禁用,1:启用)
     */
    private Integer status;

    /**
     * 业务图片ID(32位)
     */
    private String imageId;

    /**
     * 图片url
     */
    private String imageUrl;

    /**
     * 比例
     */
    private String ratio;

    /**
     * 图片生成描述
     */
    private String description;

    /**
     * 宽度
     */
    private Integer width;

    /**
     * 高度
     */
    private Integer height;
}

