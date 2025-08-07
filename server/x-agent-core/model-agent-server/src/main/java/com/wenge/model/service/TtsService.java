package com.wenge.model.service;

import com.wenge.model.dto.param.TtsParam;
import com.wenge.model.entity.VoiceComponentInfo;
import com.wg.appframe.core.bean.Result;

import javax.servlet.http.HttpServletResponse;

public interface TtsService {

    void textToVoice(TtsParam ttsParam, HttpServletResponse response);

    Result<VoiceComponentInfo> getTtsConfig(TtsParam ttsParam);

    Result<VoiceComponentInfo> getSttConfig(TtsParam ttsParam);

    Result getAliToken(TtsParam ttsParam);
}
