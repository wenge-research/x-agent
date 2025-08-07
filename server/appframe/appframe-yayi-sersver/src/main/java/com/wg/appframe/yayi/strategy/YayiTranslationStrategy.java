package com.wg.appframe.yayi.strategy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wg.appframe.yayi.config.YayiConfig;
import com.wg.appframe.yayi.config.YayiTranslationConfig;
import com.wg.appframe.yayi.param.YayiParam;
import com.wg.appframe.yayi.param.YayiTranslationParam;
import com.wg.appframe.yayi.result.YayiResult;
import com.wg.appframe.yayi.result.YayiTranslationResult;
import com.wg.appframe.yayi.utils.YayiApiUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class YayiTranslationStrategy implements YayiApiStrategy{


    @Autowired(required = false)
    private YayiTranslationConfig yayiTranslationConfig;

    @Autowired(required = false)
    private YayiConfig yayiConfig;


    public YayiResult run(String text, YayiParam yayiParam) {
        YayiTranslationResult result = new YayiTranslationResult();
        if (yayiParam instanceof YayiTranslationParam) {
            YayiTranslationParam param = (YayiTranslationParam) yayiParam;
            result = translation(text, param);

            // 判断是否需要重试
            if (null != result && null != result.getRetryFlag() && result.getRetryFlag()) {
                // 重试次数
                int retryTimes = null != yayiParam.getRetryTimes() ? yayiParam.getRetryTimes() : null != yayiTranslationConfig.getRetryTimes() ? yayiTranslationConfig.getRetryTimes() : 1;
                int retryInterval = null != yayiParam.getRetryInterval() ? yayiParam.getRetryInterval() : null != yayiTranslationConfig.getRetryInterval() ? yayiTranslationConfig.getRetryInterval() : 300;
                for (int i = 0; i < retryTimes; i++) {

                    // 重试等待时间
                    ThreadUtil.safeSleep(retryInterval);
                    log.info("===>translation 第{}次重试 ,总重试次数:{} ,重试间隔:{}毫秒, error:{}", i + 1, retryTimes, retryInterval, result.getMsg());
                    result = translation(text, param);
                    if (!result.getRetryFlag()) {
                        break;
                    }
                }
            }


        }

        return result;
    }

    /**
     * 文档解析
     * @param text
     * @param yayiParam
     * @return
     */
    public YayiTranslationResult translation(String text, YayiTranslationParam yayiParam) {
        YayiTranslationResult result = new YayiTranslationResult();
        result.setRetryFlag(false);
        try {
            // 创建POST请求
            final Request.Builder builder = new Request.Builder();

            // 设置雅意接口请求的头部参数
            YayiApiUtils.setHeader(builder, yayiTranslationConfig.getUri(), yayiConfig, yayiParam);

            // 封装文档解析接口入参
            YayiTranslationParam contentParsingParam = translationParam(text, yayiParam);

            // 请求文档解析接口
            result = getResult(builder, contentParsingParam);
            result.setRetryFlag(result.getMsg().contains("QPS"));

        } catch (Exception e) {
            log.error("error:{}", e.getMessage(), e);
        }

        return result;
    }

    /**
     * 初始化重排模型接口参数
     *
     * @param param
     * @return
     */
    private YayiTranslationParam translationParam(String text, YayiTranslationParam param) {
        YayiTranslationParam contentParsingParam = new YayiTranslationParam();
        contentParsingParam.setId(IdUtil.simpleUUID());
        YayiTranslationParam.Content content = new YayiTranslationParam.Content();
        contentParsingParam.setContent(content);
        content.setInput_text(text);
        if (null != yayiTranslationConfig.getSrcLang()) {
            content.setSrc_lang(yayiTranslationConfig.getSrcLang());
        }
        if (null != yayiTranslationConfig.getTgtLang()) {
            content.setTgt_lang(yayiTranslationConfig.getTgtLang());
        }

        contentParsingParam.setRetryTimes(1);
        if (null != yayiTranslationConfig.getRetryTimes()) {
            contentParsingParam.setRetryTimes(yayiTranslationConfig.getRetryTimes());
        }
        contentParsingParam.setRetryInterval(3000);
        if (null != yayiTranslationConfig.getRetryInterval()) {
            contentParsingParam.setRetryInterval(yayiTranslationConfig.getRetryInterval());
        }

        if (StringUtils.isBlank(contentParsingParam.getUri())) {
            contentParsingParam.setUri(yayiTranslationConfig.getUri());
        }

        if (StringUtils.isBlank(contentParsingParam.getAppKey())) {
            contentParsingParam.setAppKey(yayiConfig.getAppKey());
        }

        if (StringUtils.isBlank(contentParsingParam.getAppSecret())) {
            contentParsingParam.setAppSecret(yayiConfig.getAppSecret());
        }

        CopyOptions copyOptions = new CopyOptions();
        copyOptions.ignoreNullValue();
        YayiTranslationParam.Content content1 = param.getContent();
        if (null != content1) {
            BeanUtil.copyProperties(content1, content, copyOptions);
            BeanUtil.copyProperties(content, content1, copyOptions);
        }

        BeanUtil.copyProperties(param, contentParsingParam, copyOptions);
        BeanUtil.copyProperties(contentParsingParam, param, copyOptions);
        return contentParsingParam;
    }

    /**
     * 获取结果
     *
     * @param builder
     * @return
     */
    private YayiTranslationResult getResult(Request.Builder builder, YayiParam yayiParam) {
        YayiTranslationResult result = new YayiTranslationResult();
        log.info("===>translation url:{}  ,  param: {}", yayiParam.getUri(), JSONUtil.toJsonStr(yayiParam));
        String body = YayiApiUtils.invokeYaYiApi(builder, yayiParam);
        log.info("===>translation body: {}", body);
        if (StringUtils.isNotBlank(body) && body.contains("QPS访问超出限制")) {
            result.setMsg("QPS访问超出限制");
        } else if (StringUtils.isNotBlank(body) && JSONUtil.isTypeJSONObject(body)) {
            JSONObject entries = JSONUtil.parseObj(body);
            result = entries.toBean(YayiTranslationResult.class);
        }
        return result;
    }
}
