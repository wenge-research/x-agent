package com.wenge.model.dto.result;

import lombok.Data;

@Data
public class AiSearchFileSizeCountResult {

    /**
     * 图片总大小
     */
    private Long pictureTotalSize;

    /**
     * 视频总大小
     */
    private Long videoTotalSize;

    /**
     * 文档总大小
     */
    private Long documentTotalSize;

    /**
     * 音频总大小
     */
    private Long audioTotalSize;

}
