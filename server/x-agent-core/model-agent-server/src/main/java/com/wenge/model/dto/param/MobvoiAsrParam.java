package com.wenge.model.dto.param;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 魔音工坊语音识别接口参数
 */
@Data
@Builder
public class MobvoiAsrParam implements Serializable {

    private String appkey;
    private String timestamp;
    private String signature;
    private String audio_url;
    private String language;
    private Boolean enable_punctuation;
    private List<String> filler_words;
    private String callbackUrl;
}
