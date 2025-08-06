package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TtsParam extends WGParam {

    private static final long serialVersionUID = 1669208944957156887L;

    /**
     * 应用id
     */
    private String applicationId;

    /**
     *  ttsid
     */
    private String ttsId;

    /**
     * 文本
     */
    private String text;

    /**
     * tts厂商
     */
    private String ttsManufacturer;

    /**
     * 音频文件链接 用于语音转文本
     */
    private String audioUrl;

    /**
     * clientId
     */
    private String clientId;
}
