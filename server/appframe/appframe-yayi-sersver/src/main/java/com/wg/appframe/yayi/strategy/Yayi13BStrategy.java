package com.wg.appframe.yayi.strategy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wg.appframe.yayi.config.Yayi13BConfig;
import com.wg.appframe.yayi.config.YayiConfig;
import com.wg.appframe.yayi.entity.YayiMessage;
import com.wg.appframe.yayi.param.Generate13BParam;
import com.wg.appframe.yayi.param.YayiParam;
import com.wg.appframe.yayi.result.Generate13BResult;
import com.wg.appframe.yayi.result.YayiResult;
import com.wg.appframe.yayi.utils.YayiApiUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
public class Yayi13BStrategy implements YayiApiStrategy {

    @Autowired(required = false)
    private Yayi13BConfig yayi13BConfig;

    @Autowired(required = false)
    private YayiConfig yayiConfig;

    @Override
    public YayiResult run(String content, YayiParam yayiParam, Consumer<YayiResult> consumer) {
        Generate13BResult result = new Generate13BResult();
        if (yayiParam instanceof Generate13BParam) {
            Generate13BParam param = (Generate13BParam) yayiParam;
            result = run13b(content, param, consumer);

            // 判断是否需要重试
            if (null != result && null != result.getRetryFlag() && result.getRetryFlag()) {
                // 重试次数
                int retryTimes = null != yayiParam.getRetryTimes() ? yayiParam.getRetryTimes() : null != yayi13BConfig.getRetryTimes() ? yayi13BConfig.getRetryTimes() : 1;
                int retryInterval = null != yayiParam.getRetryInterval() ? yayiParam.getRetryInterval() : null != yayi13BConfig.getRetryInterval() ? yayi13BConfig.getRetryInterval() : 300;
                for (int i = 0; i < retryTimes; i++) {

                    // 重试等待时间
                    ThreadUtil.safeSleep(retryInterval);
                    log.info("===>run13b 第{}次重试 ,总重试次数:{} ,重试间隔:{}毫秒, error:{}", i + 1, retryTimes, retryInterval, result.getMsg());
                    result = run13b(content, param, consumer);
                    if (!result.getRetryFlag()) {
                        break;
                    }
                }
            }

        }

        return result;
    }

    /**
     * 请求雅意13B接口
     * @param content
     * @param param
     * @param consumer
     * @return
     */
    private Generate13BResult run13b(String content, Generate13BParam param, Consumer<YayiResult> consumer) {
        Generate13BResult result = new Generate13BResult();
        try {
            // 创建POST请求
            final Request.Builder builder = new Request.Builder();

            // 封装13B接口入参
            Generate13BParam yarni13BParam = getYarni13BParam(content, param);

            // 设置接口请求的头部参数
            YayiApiUtils.setHeader(builder, yarni13BParam.getUri(), yayiConfig, param);

            // 请求雅意13B接口
            if (param.getStream()) {
                if (null == consumer) {
                    return result;
                }
                log.info("===>searchGenerate13B url:{}  ,  param: {}", yarni13BParam.getUri(), JSONUtil.toJsonStr(yarni13BParam));
                Map<String, String> resultMap = MapUtil.newHashMap();
                YayiApiUtils.invokeYaYiStreamApi(builder, yayi13BConfig.getUri(), yarni13BParam, consumer, Generate13BResult.class, resultMap);
                result.setRetryFlag("1".equals(resultMap.get("isRetry")));
                return result;
            } else {
                result = getResult(builder, param);
                result.setRetryFlag(result.getMsg().contains("QPS"));
            }

        } catch (Exception e) {
            log.error("error:{}", e.getMessage(), e);
        }
        return result;
    }

    /**
     * 初始化雅意13B接口参数
     * @param content
     * @param param
     * @return
     */
    private Generate13BParam getYarni13BParam(String content, Generate13BParam param) {
        Generate13BParam generate13BParam = new Generate13BParam();
        generate13BParam.setId(IdUtil.simpleUUID());

        List<YayiMessage> messagesList = ListUtil.toList();
        YayiMessage messages = new YayiMessage();
        messages.setRole("user");
        messages.setContent(content);
        messagesList.add(messages);
        generate13BParam.setMessages(messagesList);

        if (null != yayi13BConfig.getMaxNewTokens()) {
            generate13BParam.setMax_new_tokens(yayi13BConfig.getMaxNewTokens());
        }
        if (null != yayi13BConfig.getDoSample()) {
            generate13BParam.setDo_sample(yayi13BConfig.getDoSample());
        }
        if (null != yayi13BConfig.getTemperature()) {
            generate13BParam.setTemperature(yayi13BConfig.getTemperature());
        }
        if (null != yayi13BConfig.getPresencePenalty()) {
            generate13BParam.setPresence_penalty(yayi13BConfig.getPresencePenalty());
        }
        if (null != yayi13BConfig.getFrequencyPenalty()) {
            generate13BParam.setFrequency_penalty(yayi13BConfig.getFrequencyPenalty());
        }
        if (null != yayi13BConfig.getTopP()) {
            generate13BParam.setTop_p(yayi13BConfig.getTopP());
        }
        if (null != yayi13BConfig.getTopK()) {
            generate13BParam.setTop_k(yayi13BConfig.getTopK());
        }
        if (null != yayi13BConfig.getBestOf()) {
            generate13BParam.setBest_of(yayi13BConfig.getBestOf());
        }

        generate13BParam.setRetryTimes(1);
        if (null != yayi13BConfig.getRetryTimes()) {
            generate13BParam.setRetryTimes(yayi13BConfig.getRetryTimes());
        }
        generate13BParam.setRetryInterval(3000);
        if (null != yayi13BConfig.getRetryInterval()) {
            generate13BParam.setRetryInterval(yayi13BConfig.getRetryInterval());
        }
        if (StringUtils.isNotBlank(yayi13BConfig.getModel())) {
            generate13BParam.setModel(yayi13BConfig.getModel());
        }
        if (null != yayi13BConfig.getRepetition_penalty()) {
            generate13BParam.setRepetition_penalty(yayi13BConfig.getRepetition_penalty());
        }
        if (null != yayi13BConfig.getEncoder_repetition_penalty()) {
            generate13BParam.setEncoder_repetition_penalty(yayi13BConfig.getEncoder_repetition_penalty());
        }

        generate13BParam.setStream(param.getStream());
        if (null != yayi13BConfig.getN()) {
            generate13BParam.setN(yayi13BConfig.getN());
        }

        if (StringUtils.isBlank(yayi13BConfig.getUri())) {
            generate13BParam.setUri(yayi13BConfig.getUri());
        }
        if (StringUtils.isBlank(generate13BParam.getAppKey())) {
            generate13BParam.setAppKey(yayiConfig.getAppKey());
        }
        if (StringUtils.isBlank(generate13BParam.getAppSecret())) {
            generate13BParam.setAppSecret(yayiConfig.getAppSecret());
        }

        generate13BParam.setAccept("*/*");
        CopyOptions copyOptions = new CopyOptions();
        copyOptions.ignoreNullValue();
        List<YayiMessage> messagesLists = param.getMessages();
        BeanUtil.copyProperties(param, generate13BParam, copyOptions);
        if (CollectionUtil.isNotEmpty(messagesLists)) {
            generate13BParam.setMessages(messagesLists);
        }
        BeanUtil.copyProperties(generate13BParam, param, copyOptions);
        return generate13BParam;
    }

    /**
     * 获取结果
     *
     * @param builder
     * @return
     */
    private Generate13BResult getResult(Request.Builder builder, YayiParam yayiParam) {
        Generate13BResult result = new Generate13BResult();
        log.info("===>searchGenerate13B url:{}  ,  param: {}", yayi13BConfig.getUri(), JSONUtil.toJsonStr(yayiParam));
        String body = YayiApiUtils.invokeYaYiApi(builder, yayiParam);
        log.info("===>searchGenerate13B body: {}", body);
        if (StringUtils.isNotBlank(body) && body.contains("QPS访问超出限制")) {
            result.setMsg("QPS访问超出限制");
        } else if (StringUtils.isNotBlank(body) && JSONUtil.isTypeJSONObject(body)) {
            JSONObject entries = JSONUtil.parseObj(body);
            result = entries.toBean(Generate13BResult.class);
        }
        return result;
    }
}
