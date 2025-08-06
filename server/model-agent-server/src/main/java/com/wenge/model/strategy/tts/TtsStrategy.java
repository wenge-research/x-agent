package com.wenge.model.strategy.tts;

import com.wenge.model.dto.param.TtsParam;

import javax.servlet.http.HttpServletResponse;

public interface TtsStrategy {

    void textToVoice(TtsParam ttsParam, HttpServletResponse response);

}
