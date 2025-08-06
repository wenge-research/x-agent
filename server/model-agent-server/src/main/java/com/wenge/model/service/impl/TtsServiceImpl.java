package com.wenge.model.service.impl;


import cn.hutool.core.lang.Assert;
import com.wenge.model.constants.DialogueConstant;
import com.wenge.model.constants.RedisKey;
import com.wenge.model.constants.TtsConstants;
import com.wenge.model.dto.param.TtsParam;
import com.wenge.model.entity.VoiceComponentInfo;
import com.wenge.model.service.TtsService;
import com.wenge.model.service.VoiceComponentInfoService;
import com.wenge.model.strategy.tts.TtsStrategy;
import com.wenge.model.utils.CreateToken;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import com.wg.appframe.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wenge.model.entity.table.ApplicationInfoTableDef.APPLICATION_INFO;
import static com.wenge.model.entity.table.VoiceComponentInfoTableDef.VOICE_COMPONENT_INFO;

@Service
@Slf4j
public class TtsServiceImpl implements TtsService {

    @Autowired
    private Map<String, TtsStrategy> ttsStrategyMap;

    @Autowired
    private VoiceComponentInfoService voiceComponentInfoService;

    @Autowired
    private RedisService redisService;

    @Override
    public void textToVoice(TtsParam ttsParam, HttpServletResponse response) {
        Wrappers wrappers = null;
        if (StringUtils.isNotBlank(ttsParam.getTtsId())) {
            wrappers = Wrappers.init()
                    .select(VOICE_COMPONENT_INFO.ALL_COLUMNS)
                    .where(VOICE_COMPONENT_INFO.ID.eq(ttsParam.getTtsId()));
        } else if (StringUtils.isNotBlank(ttsParam.getApplicationId())) {
            wrappers = Wrappers.init()
                    .select(VOICE_COMPONENT_INFO.ALL_COLUMNS)
                    .from(APPLICATION_INFO)
                    .innerJoin(VOICE_COMPONENT_INFO).on(APPLICATION_INFO.TTS_ID.eq(VOICE_COMPONENT_INFO.ID))
                    .where(APPLICATION_INFO.APPLICATION_ID.eq(ttsParam.getApplicationId()))
                    .limit(1);
        }
        if (null == wrappers) {
            return;
        }
        VoiceComponentInfo voiceComponentInfo = voiceComponentInfoService.getOne(wrappers);
        if (null == voiceComponentInfo) {
            log.info("没有找到语音配置");
            return;
        }
        TtsStrategy ttsStrategy = ttsStrategyMap.get(voiceComponentInfo.getStrategy());
        if (null == ttsStrategy) {
            return;
        }

        ttsStrategy.textToVoice(ttsParam, response);
    }

    @Override
    public Result<VoiceComponentInfo> getTtsConfig(TtsParam ttsParam) {
        Wrappers wrappers = Wrappers.init()
                .select(VOICE_COMPONENT_INFO.ALL_COLUMNS)
                .from(APPLICATION_INFO)
                .innerJoin(VOICE_COMPONENT_INFO).on(APPLICATION_INFO.TTS_ID.eq(VOICE_COMPONENT_INFO.ID))
                .where(APPLICATION_INFO.APPLICATION_ID.eq(ttsParam.getApplicationId()))
                .limit(1);
        VoiceComponentInfo voiceComponentInfo = voiceComponentInfoService.getOne(wrappers);
        if (null == voiceComponentInfo) {
            log.info("没有找到语音配置");
        }
        return Result.success(voiceComponentInfo);
    }

    @Override
    public Result<VoiceComponentInfo> getSttConfig(TtsParam ttsParam) {
        Wrappers wrappers = Wrappers.init()
                .select(VOICE_COMPONENT_INFO.ALL_COLUMNS)
                .from(APPLICATION_INFO)
                .innerJoin(VOICE_COMPONENT_INFO).on(APPLICATION_INFO.STT_ID.eq(VOICE_COMPONENT_INFO.ID))
                .where(APPLICATION_INFO.APPLICATION_ID.eq(ttsParam.getApplicationId()))
                .limit(1);
        VoiceComponentInfo voiceComponentInfo = voiceComponentInfoService.getOne(wrappers);
        if (null == voiceComponentInfo) {
            log.info("没有找到语音配置");
        }
        return Result.success(voiceComponentInfo);
    }

    @Override
    public Result getAliToken(TtsParam ttsParam) {

        // 先从缓存中获取
        String key = RedisKey.ALI_TTS_TOKEN + ttsParam.getTtsId();
        String token;
        if (redisService.hasKey(key)) {
            Object tokenObj = redisService.get(key);
            if (ObjectUtils.isNotEmpty(tokenObj)) {
                token = tokenObj.toString();
                return Result.success(token);
            }
        }
        Wrappers wrappers = Wrappers.init()
                .select(VOICE_COMPONENT_INFO.ALL_COLUMNS)
                .from(APPLICATION_INFO)
                .innerJoin(VOICE_COMPONENT_INFO).on(APPLICATION_INFO.TTS_ID.eq(VOICE_COMPONENT_INFO.ID))
                .where(APPLICATION_INFO.APPLICATION_ID.eq(ttsParam.getApplicationId()))
                .limit(1);
        if (StringUtils.isBlank(ttsParam.getApplicationId())) {
            // 调试用
            wrappers = Wrappers.init().where(VOICE_COMPONENT_INFO.ID.eq(ttsParam.getTtsId()));
        }
        VoiceComponentInfo voiceComponentInfo = voiceComponentInfoService.getOne(wrappers);

        String accessKeyId = voiceComponentInfo.getApiKey();
        Assert.notBlank(accessKeyId,"语音认证配置错误");
        String accessKeySecret = voiceComponentInfo.getApiSecret();
        Assert.notBlank(accessKeySecret, "语音认证配置错误");

        System.out.println(CreateToken.getISO8601Time(null));
        // 所有请求参数
        Map<String, String> queryParamsMap = new HashMap<String, String>();
        queryParamsMap.put("AccessKeyId", accessKeyId);
        queryParamsMap.put("Action", "CreateToken");
        queryParamsMap.put("Version", "2019-02-28");
        queryParamsMap.put("Timestamp", CreateToken.getISO8601Time(null));
        queryParamsMap.put("Format", "JSON");
        queryParamsMap.put("RegionId", "cn-shanghai");
        queryParamsMap.put("SignatureMethod", "HMAC-SHA1");
        queryParamsMap.put("SignatureVersion", "1.0");
        queryParamsMap.put("SignatureNonce", CreateToken.getUniqueNonce());
        /**
         * 1.构造规范化的请求字符串
         */
        String queryString = CreateToken.canonicalizedQuery(queryParamsMap);
        Assert.notBlank(queryString, "构造规范化的请求字符串失败！");
        /**
         * 2.构造签名字符串
         */
        String method = "GET";  // 发送请求的 HTTP 方法，GET
        String urlPath = "/";   // 请求路径
        String stringToSign = CreateToken.createStringToSign(method, urlPath, queryString);
        Assert.notBlank(stringToSign, "构造签名字符串失败");

        /**
         * 3.计算签名
         */
        String signature = CreateToken.sign(stringToSign, accessKeySecret + "&");
        Assert.notBlank(signature, "计算签名失败!");

        /**
         * 4.将签名加入到第1步获取的请求字符串
         */
        String queryStringWithSign = "Signature=" + signature + "&" + queryString;
        System.out.println("带有签名的请求字符串：" + queryStringWithSign);
        /**
         * 5.发送HTTP GET请求，获取token。
         */
        String url = "http://nls-meta.cn-shanghai.aliyuncs.com";
        token = CreateToken.processGETRequest(url, queryStringWithSign);
        Assert.notBlank(token, "未获取到阿里token");
        // 更新缓存
        redisService.set(key, token, TtsConstants.ALI_TTS_TOKEN_EXPIRE_TIME);
        return Result.success(token);
    }

}
