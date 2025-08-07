package com.wenge.model.strategy.tts;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONPath;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.wenge.model.constants.TtsConstants;
import com.wenge.model.dto.param.MobvoiAsrParam;
import com.wenge.model.dto.param.MobvoiParam;
import com.wenge.model.dto.param.TtsParam;
import com.wenge.model.entity.VoiceComponentInfo;
import com.wenge.model.service.VoiceComponentInfoService;
import com.wenge.model.utils.MobvoiHttpClientUtil;
import com.wenge.model.utils.MobvoiSignatureUtil;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.wenge.model.entity.table.ApplicationInfoTableDef.APPLICATION_INFO;
import static com.wenge.model.entity.table.VoiceComponentInfoTableDef.VOICE_COMPONENT_INFO;

/**
 * 魔音工坊
 */
@Service(TtsConstants.MOBVOI)
@Slf4j
public class MobvoiStrategy implements TtsStrategy {

    @Autowired
    private VoiceComponentInfoService voiceComponentInfoService;

    @Override
    public void textToVoice(TtsParam ttsParam, HttpServletResponse response) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        CloseableHttpResponse audioResponse = null;
        try {
            Wrappers wrappers = Wrappers.init()
                    .select(VOICE_COMPONENT_INFO.ALL_COLUMNS)
                    .from(APPLICATION_INFO)
                    .innerJoin(VOICE_COMPONENT_INFO).on(APPLICATION_INFO.TTS_ID.eq(VOICE_COMPONENT_INFO.ID))
                    .where(APPLICATION_INFO.APPLICATION_ID.eq(ttsParam.getApplicationId()))
                    .limit(1);

            VoiceComponentInfo voiceComponentInfo = voiceComponentInfoService.getOne(wrappers);
            if (null == voiceComponentInfo) {
                log.info("没有找到语音配置");
                return;
            }

            // 调用魔音工坊
            audioResponse = sample(ttsParam, voiceComponentInfo);
            if (null == audioResponse) {
                log.info("转语音失败");
                return;
            }
            inputStream = audioResponse.getEntity().getContent();

            // 设置响应头
            response.setContentType("audio/mpeg");
            response.setHeader("Content-Disposition", "attachment;filename=download." + voiceComponentInfo.getAudioType());
            outputStream = response.getOutputStream();
            IoUtil.copy(inputStream, outputStream);
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != inputStream) {
                    inputStream.close();
                }
                if (null != audioResponse) {
                    audioResponse.close();
                }
                if (null != outputStream) {
                    outputStream.close();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

//    @Override
    public String voiceToText(TtsParam ttsParam) {

        Wrappers wrappers = Wrappers.init()
                .select(VOICE_COMPONENT_INFO.ALL_COLUMNS)
                .from(APPLICATION_INFO)
                .innerJoin(VOICE_COMPONENT_INFO).on(APPLICATION_INFO.TTS_ID.eq(VOICE_COMPONENT_INFO.ID))
                .where(APPLICATION_INFO.APPLICATION_ID.eq(ttsParam.getApplicationId()))
                .limit(1);

        VoiceComponentInfo voiceComponentInfo = voiceComponentInfoService.getOne(wrappers);
        if (null == voiceComponentInfo) {
            log.info("没有找到语音配置");
            return "";
        }
        // 调用魔音工坊API，创建解析任务
        return asr(ttsParam.getAudioUrl(), voiceComponentInfo);
    }



    private String asr(String audioUrl, VoiceComponentInfo voiceComponentInfo) {
        String timestamp = System.currentTimeMillis() / 1000 + "";

        MobvoiAsrParam param = MobvoiAsrParam.builder()
                .appkey(voiceComponentInfo.getApiKey())
                .timestamp(timestamp)
                .signature(MobvoiSignatureUtil.getSignature(voiceComponentInfo.getApiKey(), voiceComponentInfo.getApiSecret(), timestamp))
                .audio_url(audioUrl)
                .language(voiceComponentInfo.getLanguage())
                .enable_punctuation(Boolean.TRUE)
                .filler_words(new ArrayList<>()).build();
        // 调用接口
        log.info("魔音工坊音频转文本请求参数:{}", JSON.toJSONString(param));
        String responseText = HttpUtil.post(voiceComponentInfo.getVoiceLink(), JSON.toJSONString(param));
        log.info("魔音工坊音频转文本响应参数:{}", responseText);
        JSONObject jsonObject = JSON.parseObject(responseText);
        Integer code = jsonObject.getInteger("code");
        if (code != 0) {
            throw new RuntimeException("魔音工坊音频转文本失败: " + jsonObject.getString("message"));
        }

        Integer taskId = jsonObject.getJSONObject("data").getInteger("task_id");
        while (true) {
            // 3秒轮询一次
            ThreadUtil.safeSleep(3000);

            Map<String, Object> map = new HashMap<>();
            map.put("taskId", taskId);
            // todo 魔音工坊获取任务状态接口，后续改成配置文件，或者音频配置
            String s = HttpUtil.get("https://open.mobvoi.com/api/asr/get", map);
            log.info("查询语音转文本任务状态 testId:{} 响应：{}", taskId, s);
            JSONObject data = JSON.parseObject(s).getJSONObject("data");
            Integer status = data.getInteger("status");
            //	识别请求的当前处理状态： 0 - 加载中 1 - 处理中 2 - 处理成功 3 - 处理失败
            if (status == 2) {
                return (String) JSONPath.read(s, "$.data.result.overall.text");
            } else if (status == 3) {
                throw new RuntimeException("语音转文字失败 " + JSON.parseObject(s).getJSONObject("message"));
            }
        }
    }


    /**
     * 文字转语音
     */
    private CloseableHttpResponse sample(TtsParam ttsParam, VoiceComponentInfo voiceComponentInfo) throws IOException {
        MobvoiParam mobvoiParam = new MobvoiParam();
        mobvoiParam.setText(ttsParam.getText());
        mobvoiParam.setSpeaker(voiceComponentInfo.getVoicePeople());
        mobvoiParam.setAudio_type(voiceComponentInfo.getAudioType());
        mobvoiParam.setSpeed(voiceComponentInfo.getVoiceSpeed().floatValue());
        mobvoiParam.setGen_srt(false);
        mobvoiParam.setMerge_symbol(true);
        mobvoiParam.setAppkey(voiceComponentInfo.getApiKey());
        String timestamp = System.currentTimeMillis() / 1000 + "";
        mobvoiParam.setTimestamp(timestamp);
        mobvoiParam.setIgnore_limit(false);
        mobvoiParam.setStreaming(true);

        mobvoiParam.setSignature(MobvoiSignatureUtil.getSignature(voiceComponentInfo.getApiKey(), voiceComponentInfo.getApiSecret(), timestamp));
        log.info("魔音工坊.sample 参数:{}", JSON.toJSONString(mobvoiParam));
        CloseableHttpResponse audioResponse = null;
        audioResponse = MobvoiHttpClientUtil.doPostJsonStreaming(voiceComponentInfo.getVoiceLink(), JSON.toJSONString(mobvoiParam));
        Header firstHeader = audioResponse.getFirstHeader("Content-Type");
        if (audioResponse.getEntity().isStreaming() &&
                !firstHeader.getValue().contains("application/json")) {
            // 下载audio文件
            return audioResponse;
        } else {
            log.info("魔音工坊.sample 结果:{}", EntityUtils.toString(audioResponse.getEntity(), "utf-8"));
        }
        return null;
    }

}
