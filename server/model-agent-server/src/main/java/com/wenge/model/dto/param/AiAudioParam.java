package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AiAudioParam extends WGParam {

    private static final long serialVersionUID = 1378235918747932589L;

    /**
     * 策略，默认dbVideoStrategy
     */
    private String strategy;

    /**
     * 指令内容
     */
    private String content;

    /**
     * 歌词
     */
    private String lyrics;

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
     * 比特率
     */
    private Integer bitrate;

    /**
     * 格式,mp3
     */
    private String format;

    /**
     * 采样率
     */
    private Integer sampleRate;

    /**
     * 是否保存，是/否
     */
    private String saveFlag;
}
