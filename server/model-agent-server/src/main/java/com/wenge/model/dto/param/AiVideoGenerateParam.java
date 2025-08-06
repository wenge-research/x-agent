package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AiVideoGenerateParam extends WGParam {

    private static final long serialVersionUID = 7343791473765845469L;

    /**
     * 策略，默认dbVideoStrategy
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

}
