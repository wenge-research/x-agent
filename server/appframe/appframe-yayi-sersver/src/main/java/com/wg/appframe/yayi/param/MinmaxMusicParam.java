package com.wg.appframe.yayi.param;

import com.wg.appframe.yayi.config.MinmaxMusicConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class MinmaxMusicParam extends MinmaxMusicConfig {

    private static final long serialVersionUID = -6923248431762464576L;

    private String prompt;

    private String appKey;
    private AudioSetting audio_setting;

    @Data
    public static class AudioSetting implements Serializable {

        private static final long serialVersionUID = -2281211565313601941L;

        private Integer sample_rate;
        private Integer bitrate;
        private String format;
    }

}
