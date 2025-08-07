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
import com.wg.appframe.yayi.config.Yayi30BConfig;
import com.wg.appframe.yayi.config.YayiConfig;
import com.wg.appframe.yayi.entity.YayiMessage;
import com.wg.appframe.yayi.param.Generate30BParam;
import com.wg.appframe.yayi.param.YayiParam;
import com.wg.appframe.yayi.result.Generate30BResult;
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
public class Yayi30BStrategy implements YayiApiStrategy {

    @Autowired(required = false)
    private Yayi30BConfig yayi30BConfig;

    @Autowired(required = false)
    private YayiConfig yayiConfig;

    @Override
    public YayiResult run(String content, YayiParam yayiParam, Consumer<YayiResult> consumer) {
        Generate30BResult result = new Generate30BResult();
        if (yayiParam instanceof Generate30BParam) {
            Generate30BParam param = (Generate30BParam) yayiParam;
            result = run30B(content, param, consumer);

            // 判断是否需要重试
            if (null != result && null != result.getRetryFlag() && result.getRetryFlag()) {
                // 重试次数
                int retryTimes = null != yayiParam.getRetryTimes() ? yayiParam.getRetryTimes() : null != yayi30BConfig.getRetryTimes() ? yayi30BConfig.getRetryTimes() : 1;
                int retryInterval = null != yayiParam.getRetryInterval() ? yayiParam.getRetryInterval() : null != yayi30BConfig.getRetryInterval() ? yayi30BConfig.getRetryInterval() : 300;
                for (int i = 0; i < retryTimes; i++) {

                    // 重试等待时间
                    ThreadUtil.safeSleep(retryInterval);
                    log.info("===>run30B 第{}次重试 ,总重试次数:{} ,重试间隔:{}毫秒, error:{}", i + 1, retryTimes, retryInterval, result.getMsg());
                    result = run30B(content, param, consumer);
                    if (!result.getRetryFlag()) {
                        break;
                    }
                }
            }
        }
        if (null == result) {
            result = new Generate30BResult();
        }
        return result;
    }

    /**
     * 调用30B接口
     * @param content
     * @param param
     * @param consumer
     * @return
     */
    private Generate30BResult run30B(String content, Generate30BParam param, Consumer<YayiResult> consumer) {
        Generate30BResult result = new Generate30BResult();
        try {
            // 创建POST请求
            final Request.Builder builder = new Request.Builder();

            // 封装30B接口入参
            Generate30BParam yarni30BParam = getYarni30BParam(content, param);

            // 设置雅意接口请求的头部参数
            YayiApiUtils.setHeader(builder, yarni30BParam.getUri(), yayiConfig, param);

            // 请求雅意30B接口
            if (param.getStream()) {
                if (null == consumer) {
                    return result;
                }
                log.info("===>searchGenerate30B url:{}  ,  param: {}", yarni30BParam.getUri(), JSONUtil.toJsonStr(yarni30BParam));
                Map<String, String> resultMap = MapUtil.newHashMap();
                YayiApiUtils.invokeYaYiStreamApi(builder, yarni30BParam.getUri(), yarni30BParam, consumer, Generate30BResult.class, resultMap);
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
     * 初始化雅意30B接口参数
     * @param content
     * @param param
     * @return
     */
    private Generate30BParam getYarni30BParam(String content, Generate30BParam param) {
        Generate30BParam generate30BParam = new Generate30BParam();
        generate30BParam.setId(IdUtil.simpleUUID());

        List<YayiMessage> messagesList = ListUtil.toList();
        YayiMessage messages = new YayiMessage();
        messages.setRole("user");
        messages.setContent(content);
        messagesList.add(messages);
        generate30BParam.setMessages(messagesList);

        if (null != yayi30BConfig.getMaxNewTokens()) {
            generate30BParam.setMax_new_tokens(yayi30BConfig.getMaxNewTokens());
        }
        if (null != yayi30BConfig.getDoSample()) {
            generate30BParam.setDo_sample(yayi30BConfig.getDoSample());
        }
        if (null != yayi30BConfig.getTemperature()) {
            generate30BParam.setTemperature(yayi30BConfig.getTemperature());
        }
        if (null != yayi30BConfig.getPresencePenalty()) {
            generate30BParam.setPresence_penalty(yayi30BConfig.getPresencePenalty());
        }
        if (null != yayi30BConfig.getFrequencyPenalty()) {
            generate30BParam.setFrequency_penalty(yayi30BConfig.getFrequencyPenalty());
        }
        if (null != yayi30BConfig.getTopP()) {
            generate30BParam.setTop_p(yayi30BConfig.getTopP());
        }
        if (null != yayi30BConfig.getTopK()) {
            generate30BParam.setTop_k(yayi30BConfig.getTopK());
        }
        if (null != yayi30BConfig.getRepetition_penalty()) {
            generate30BParam.setRepetition_penalty(yayi30BConfig.getRepetition_penalty());
        }
        if (null != yayi30BConfig.getEncoder_repetition_penalty()) {
            generate30BParam.setEncoder_repetition_penalty(yayi30BConfig.getEncoder_repetition_penalty());
        }

        generate30BParam.setRetryTimes(1);
        if (null != yayi30BConfig.getRetryTimes()) {
            generate30BParam.setRetryTimes(yayi30BConfig.getRetryTimes());
        }
        generate30BParam.setRetryInterval(3000);
        if (null != yayi30BConfig.getRetryInterval()) {
            generate30BParam.setRetryInterval(yayi30BConfig.getRetryInterval());
        }

        if (StringUtils.isNotBlank(yayi30BConfig.getModel())) {
            generate30BParam.setModel(yayi30BConfig.getModel());
        }
        if (null != yayi30BConfig.getBestOf()) {
            generate30BParam.setBest_of(yayi30BConfig.getBestOf());
        }

        generate30BParam.setStream(param.getStream());
        if (null != yayi30BConfig.getN()) {
            generate30BParam.setN(yayi30BConfig.getN());
        }
        if (StringUtils.isBlank(generate30BParam.getUri())) {
            generate30BParam.setUri(yayi30BConfig.getUri());
        }
        if (StringUtils.isBlank(generate30BParam.getAppKey())) {
            generate30BParam.setAppKey(yayiConfig.getAppKey());
        }
        if (StringUtils.isBlank(generate30BParam.getAppSecret())) {
            generate30BParam.setAppSecret(yayiConfig.getAppSecret());
        }

        generate30BParam.setAccept("*/*");
        CopyOptions copyOptions = new CopyOptions();
        copyOptions.ignoreNullValue();
        List<YayiMessage> messagesLists = param.getMessages();
        BeanUtil.copyProperties(param, generate30BParam, copyOptions);
        if (CollectionUtil.isNotEmpty(messagesLists)) {
            generate30BParam.setMessages(messagesLists);
        }
        BeanUtil.copyProperties(generate30BParam, param, copyOptions);
        return generate30BParam;
    }

    /**
     * 获取结果
     *
     * @param builder
     * @return
     */
    private Generate30BResult getResult(Request.Builder builder, Generate30BParam yayiParam) {
        Generate30BResult result = new Generate30BResult();
        log.info("===>searchGenerate30B url:{}  ,  param: {}", yayiParam.getUri(), JSONUtil.toJsonStr(yayiParam));
        String body = YayiApiUtils.invokeYaYiApi(builder, yayiParam);
        log.info("===>searchGenerate30B body: {}", body);
        if (StringUtils.isNotBlank(body) && body.contains("QPS访问超出限制")) {
            result.setMsg("QPS访问超出限制");
        } else if (StringUtils.isNotBlank(body) && JSONUtil.isTypeJSONObject(body)) {
            JSONObject entries = JSONUtil.parseObj(body);
            result = entries.toBean(Generate30BResult.class);
        }
        return result;
    }
}
