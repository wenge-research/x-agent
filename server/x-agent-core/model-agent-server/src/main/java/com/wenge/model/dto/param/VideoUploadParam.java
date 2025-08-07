package com.wenge.model.dto.param;

import lombok.Data;

/**
 * 批量视频链接上传参数
 * 
 * @author system
 * @since 2024-01-01
 */
@Data
public class VideoUploadParam {
    
    /**
     * 视频链接列表
     * 支持HTTP/HTTPS链接，如：https://example.com/video.mp4
     */
    private String videoUrl;

    /**
     * 封面链接
     * 支持HTTP/HTTPS链接，如：https://example.com/cover.jpg
     */
    private String coverUrl;

    /**
     * 描述
     */
    private String description;

    /**
     * 比例
     */
    private String ratio;
}