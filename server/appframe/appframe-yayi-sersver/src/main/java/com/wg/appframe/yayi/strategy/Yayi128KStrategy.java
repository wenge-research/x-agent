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
import com.wg.appframe.yayi.config.Yayi128kConfig;
import com.wg.appframe.yayi.config.YayiConfig;
import com.wg.appframe.yayi.entity.YayiMessage;
import com.wg.appframe.yayi.param.Generate128KParam;
import com.wg.appframe.yayi.param.YayiParam;
import com.wg.appframe.yayi.result.Generate128KResult;
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
public class Yayi128KStrategy implements YayiApiStrategy {

    @Autowired(required = false)
    private Yayi128kConfig yayi128kConfig;

    @Autowired(required = false)
    private YayiConfig yayiConfig;

    @Override
    public YayiResult run(String content, YayiParam yayiParam, Consumer<YayiResult> consumer) {
        Generate128KResult result = new Generate128KResult();
        if (yayiParam instanceof Generate128KParam) {
            Generate128KParam param = (Generate128KParam) yayiParam;
            result = run128K(content, param, consumer);

            // 判断是否需要重试
            if (null != result && null != result.getRetryFlag() && result.getRetryFlag()) {
                // 重试次数
                int retryTimes = null != yayiParam.getRetryTimes() ? yayiParam.getRetryTimes() : null != yayi128kConfig.getRetryTimes() ? yayi128kConfig.getRetryTimes() : 1;
                int retryInterval = null != yayiParam.getRetryInterval() ? yayiParam.getRetryInterval() : null != yayi128kConfig.getRetryInterval() ? yayi128kConfig.getRetryInterval() : 300;
                for (int i = 0; i < retryTimes; i++) {
                    // 重试等待时间
                    ThreadUtil.safeSleep(retryInterval);
                    log.info("===>run128K 第{}次重试 ,总重试次数:{} ,重试间隔:{}毫秒, error:{}", i + 1, retryTimes, retryInterval, result.getMsg());
                    result = run128K(content, param, consumer);
                    if (!result.getRetryFlag()) {
                        break;
                    }
                }
            }
        }

        return result;
    }

    /**
     * 运行雅意128K接口
     * @param content
     * @param param
     * @param consumer
     * @return
     */
    private Generate128KResult run128K(String content, Generate128KParam param, Consumer<YayiResult> consumer) {
        Generate128KResult result = new Generate128KResult();
        try {
            // 创建POST请求
            final Request.Builder builder = new Request.Builder();

            // 封装接口入参
            Generate128KParam yarni128KParam = getYarni128KParam(content, param);

            // 设置接口请求的头部参数
            YayiApiUtils.setHeader(builder, yarni128KParam.getUri(), yayiConfig, param);

            // 请求雅意128K接口
            log.info("===>searchGenerate128K url:{}  ,  param: {}", yarni128KParam.getUri(), JSONUtil.toJsonStr(yarni128KParam));
            if (param.getStream()) {
                if (null == consumer) {
                    return result;
                }
                Map<String, String> resultMap = MapUtil.newHashMap();
                YayiApiUtils.invokeYaYiStreamApi(builder, yarni128KParam.getUri(), yarni128KParam, consumer, Generate128KResult.class, resultMap);
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
     * 获取结果
     *
     * @param builder
     * @return
     */
    private Generate128KResult getResult(Request.Builder builder, Generate128KParam yayiParam) {
        Generate128KResult result = new Generate128KResult();
        log.info("===>searchGenerate128K url:{}  ,  param: {}", yayiParam.getUri(), JSONUtil.toJsonStr(yayiParam));
        String body = YayiApiUtils.invokeYaYiApi(builder, yayiParam);
        log.info("===>searchGenerate128K body: {}", body);
        if (StringUtils.isNotBlank(body) && body.contains("QPS访问超出限制")) {
            result.setMsg("QPS访问超出限制");
        } else if (StringUtils.isNotBlank(body) && JSONUtil.isTypeJSONObject(body)) {
            JSONObject entries = JSONUtil.parseObj(body);
            result = entries.toBean(Generate128KResult.class);
        }
        return result;
    }

    /**
     * 初始化雅意128K接口参数
     * @param content
     * @param param
     * @return
     */
    private Generate128KParam getYarni128KParam(String content, Generate128KParam param) {
        Generate128KParam generate128KParam = new Generate128KParam();
        generate128KParam.setId(IdUtil.simpleUUID());

        List<YayiMessage> messagesList = ListUtil.toList();
        YayiMessage messages = new YayiMessage();
        messages.setRole("user");
        messages.setContent(content);
        messagesList.add(messages);
        generate128KParam.setMessages(messagesList);

        if (null != yayi128kConfig.getMaxNewTokens()) {
            generate128KParam.setMax_new_tokens(yayi128kConfig.getMaxNewTokens());
        }
        if (null != yayi128kConfig.getDoSample()) {
            generate128KParam.setDo_sample(yayi128kConfig.getDoSample());
        }
        if (null != yayi128kConfig.getTemperature()) {
            generate128KParam.setTemperature(yayi128kConfig.getTemperature());
        }
        if (null != yayi128kConfig.getPresencePenalty()) {
            generate128KParam.setPresence_penalty(yayi128kConfig.getPresencePenalty());
        }
        if (null != yayi128kConfig.getFrequencyPenalty()) {
            generate128KParam.setFrequency_penalty(yayi128kConfig.getFrequencyPenalty());
        }
        if (null != yayi128kConfig.getTopP()) {
            generate128KParam.setTop_p(yayi128kConfig.getTopP());
        }
        if (null != yayi128kConfig.getTopK()) {
            generate128KParam.setTop_k(yayi128kConfig.getTopK());
        }

        if (null != yayi128kConfig.getRepetition_penalty()) {
            generate128KParam.setRepetition_penalty(yayi128kConfig.getRepetition_penalty());
        }
        if (null != yayi128kConfig.getEncoder_repetition_penalty()) {
            generate128KParam.setEncoder_repetition_penalty(yayi128kConfig.getEncoder_repetition_penalty());
        }
        if (null != yayi128kConfig.getBestOf()) {
            generate128KParam.setBest_of(yayi128kConfig.getBestOf());
        }
        generate128KParam.setRetryTimes(1);
        if (null != yayi128kConfig.getRetryTimes()) {
            generate128KParam.setRetryTimes(yayi128kConfig.getRetryTimes());
        }
        generate128KParam.setRetryInterval(3000);
        if (null != yayi128kConfig.getRetryInterval()) {
            generate128KParam.setRetryInterval(yayi128kConfig.getRetryInterval());
        }

        if (StringUtils.isNotBlank(yayi128kConfig.getModel())) {
            generate128KParam.setModel(yayi128kConfig.getModel());
        }

        generate128KParam.setStream(param.getStream());
        if (null != yayi128kConfig.getN()) {
            generate128KParam.setN(yayi128kConfig.getN());
        }

        if (StringUtils.isBlank(param.getUri())) {
            generate128KParam.setUri(yayi128kConfig.getUri());
        }

        if (StringUtils.isBlank(param.getAppKey())) {
            generate128KParam.setAppKey(yayiConfig.getAppKey());
        }
        if (StringUtils.isBlank(param.getAppSecret())) {
            generate128KParam.setAppSecret(yayiConfig.getAppSecret());
        }

        generate128KParam.setAccept("*/*");
        CopyOptions copyOptions = new CopyOptions();
        copyOptions.ignoreNullValue();
        List<YayiMessage> messagesLists = param.getMessages();
        BeanUtil.copyProperties(param, generate128KParam, copyOptions);
        if (CollectionUtil.isNotEmpty(messagesLists)) {
            generate128KParam.setMessages(messagesLists);
        }
        BeanUtil.copyProperties(generate128KParam, param, copyOptions);
        return generate128KParam;
    }

}
