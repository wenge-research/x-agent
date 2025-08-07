package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class AiVideoParam extends WGParam {

    private static final long serialVersionUID = 7343791473765845469L;

    /**
     * 策略，dbVideoStrategy（默认）,minmaxVideoStrategy
     */
    private String strategy;

    /**
     * 指令内容
     */
    private String content;

    /**
     * 分辨率
     */
    private String resolution;

    /**
     * 宽高比
     */
    private String ratio;

    /**
     * 时长
     */
    private Integer duration;

    /**
     * 应用ID
     */
    private String applicationId;

    /**
     * 是否保存，是/否
     */
    private String saveFlag;

    /**
     * 模型ID
     */
    private String modelId;

    /**
     * 首帧参考图
     */
    private String imageUrl;

    /**
     * 主体参考图（数组长度暂时只支持1）
     */
    private List<String> subjectImageUrl;

    /**
     * 帧率
     */
    private Integer framepersecond;

    /**
     * 是否固定摄像头。枚举值：
     * true：固定摄像头。平台会在用户提示词中追加固定摄像头，实际效果不保证。
     * false：不固定摄像头。
     */
    private Boolean camerafixed;

    /**
     * 控制是否开启prompt自动优化（注：minmax模型专用）
     */
    private Boolean promptOptimizer;

}
