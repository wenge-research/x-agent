package com.wenge.model.strategy.aiAudio;

import cn.hutool.core.util.IdUtil;
import com.wenge.model.dto.param.AiAudioParam;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.core.constant.enums.YesNoEnum;
import com.wg.appframe.yayi.api.MinimaxiServer;
import com.wg.appframe.yayi.param.MinmaxMusicParam;
import com.wg.appframe.yayi.result.MinmaxMusicResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 描述：minmax音频生成
 * https://platform.minimaxi.com/document/Music%20Generation?key=667cd92e3be2027f69b723dd
 */
@Service("minmaxAudioStrategy")
@Slf4j
public class MinmaxAudioStrategy implements AiAudioStrategy {

    @Autowired(required = false)
    private MinimaxiServer minimaxiServer;

    @Override
    public String getAiAudio(AiAudioParam param) {
        if (null == minimaxiServer) {
            log.error("MinimaxiServer is null");
            return StringConstant.BLANK;
        }

        MinmaxMusicParam minmaxMusicParam = new MinmaxMusicParam();
        minmaxMusicParam.setLyrics(param.getLyrics());
        MinmaxMusicParam.AudioSetting audio_setting = new MinmaxMusicParam.AudioSetting();
        audio_setting.setBitrate(param.getBitrate());
        audio_setting.setFormat(param.getFormat());
        audio_setting.setSample_rate(param.getSampleRate());
        minmaxMusicParam.setAudio_setting(audio_setting);
        MinmaxMusicResult minmaxMusicResult = minimaxiServer.aiMusic(param.getContent(), minmaxMusicParam);
        String audio = StringConstant.BLANK;
        if (0 == minmaxMusicResult.getBase_resp().getStatus_code()) {
            MinmaxMusicResult.Data data = minmaxMusicResult.getData();
            if (null != data) {
                audio = data.getAudio();

                if (!YesNoEnum.NO.getName().equals(param.getSaveFlag())) {
                    audio = uploadMinio(StringConstant.BLANK, audio, IdUtil.simpleUUID() + "." + param.getFormat());
                }
            }
        }

        return audio;
    }
}
