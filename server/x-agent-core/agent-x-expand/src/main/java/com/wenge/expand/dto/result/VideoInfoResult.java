package com.wenge.expand.dto.result;

import lombok.Data;

/**
 * 视频信息实体类
 */
@Data
public class VideoInfoResult {

    /**
     * 视频标题
     */
    private String title;

    /**
     * 视频链接
     */
    private String videoUrl;

    /**
     * 封面链接
     */
    private String coverUrl;

    /**
     * 视频时长(秒)
     */
    private Double duration;

    /**
     * 视频大小(字节)
     */
    private Long size;

    /**
     * 宽度
     */
    private Integer width;

    /**
     * 高度
     */
    private Integer height;

    /**
     * 视频格式
     */
    private String format;

}