package com.wenge.model.controller;

import com.wenge.model.dto.param.TtsParam;
import com.wenge.model.entity.VoiceComponentInfo;
import com.wenge.model.service.TtsService;
import com.wg.appframe.core.bean.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/tts")
public class TtsController {

    @Autowired
    private TtsService ttsService;

    /**
     * 文本转语音
     */
    @PostMapping("/textToVoice")
    public void textToVoice(@RequestBody TtsParam ttsParam, HttpServletResponse response) {
        ttsService.textToVoice(ttsParam, response);
    }

    /**
     * 获取tts配置
     */
    @PostMapping("/getTtsConfig")
    public Result<VoiceComponentInfo> getTtsConfig(@RequestBody TtsParam ttsParam) {
        return ttsService.getTtsConfig(ttsParam);
    }

    /**
     * 获取stt配置
     */
    @PostMapping("/getSttConfig")
    public Result<VoiceComponentInfo> getSttConfig(@RequestBody TtsParam ttsParam) {
        return ttsService.getSttConfig(ttsParam);
    }

    /**
     * 获取阿里动态语音token
     */
    @PostMapping("/getAliToken")
    public Result getAliToken(@RequestBody TtsParam ttsParam) {
        return ttsService.getAliToken(ttsParam);
    }

}
