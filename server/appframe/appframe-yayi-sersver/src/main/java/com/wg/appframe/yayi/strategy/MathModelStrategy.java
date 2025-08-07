package com.wg.appframe.yayi.strategy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wg.appframe.yayi.config.MathModelConfig;
import com.wg.appframe.yayi.config.YayiConfig;
import com.wg.appframe.yayi.entity.YayiMessage;
import com.wg.appframe.yayi.param.MathModelParam;
import com.wg.appframe.yayi.param.YayiParam;
import com.wg.appframe.yayi.result.MathModelResult;
import com.wg.appframe.yayi.utils.YayiApiUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class MathModelStrategy implements YayiApiStrategy{


    @Autowired(required = false)
    private MathModelConfig mathModelConfig;

    @Autowired(required = false)
    private YayiConfig yayiConfig;


    public MathModelResult run(String fileUrl, YayiParam yayiParam) {
        MathModelResult result = new MathModelResult();
        if (yayiParam instanceof MathModelParam) {
            MathModelParam param = (MathModelParam) yayiParam;
            result = runMathModel(fileUrl, param);

            // 判断是否需要重试
            if (null != result && null != result.getRetryFlag() && result.getRetryFlag()) {
                // 重试次数
                int retryTimes = null != yayiParam.getRetryTimes() ? yayiParam.getRetryTimes() : null != mathModelConfig.getRetryTimes() ? mathModelConfig.getRetryTimes() : 1;
                int retryInterval = null != yayiParam.getRetryInterval() ? yayiParam.getRetryInterval() : null != mathModelConfig.getRetryInterval() ? mathModelConfig.getRetryInterval() : 300;
                for (int i = 0; i < retryTimes; i++) {

                    // 重试等待时间
                    ThreadUtil.safeSleep(retryInterval);
                    log.info("===>runMathModel 第{}次重试 ,总重试次数:{} ,重试间隔:{}毫秒, error:{}", i + 1, retryTimes, retryInterval, result.getMsg());
                    result = runMathModel(fileUrl, param);
                    if (!result.getRetryFlag()) {
                        break;
                    }
                }
            }
        }

        return result;
    }

    /**
     * 图片解析
     * @param fileUrl
     * @param yayiParam
     * @return
     */
    public MathModelResult runMathModel(String fileUrl, MathModelParam yayiParam) {
        MathModelResult result = new MathModelResult();
        result.setRetryFlag(false);
        try {
            // 创建POST请求
            final Request.Builder builder = new Request.Builder();

            // 设置雅意接口请求的头部参数
            YayiApiUtils.setHeader(builder, mathModelConfig.getUri(), yayiConfig, yayiParam);

            // 封装图片解析接口入参
            MathModelParam mathModelParam = mathModelParam(fileUrl, yayiParam);

            // 请求图片解析接口
            result = getResult(builder, mathModelParam);
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
    private MathModelParam mathModelParam(String question, MathModelParam param) {
        MathModelParam mathModelParam = new MathModelParam();
        mathModelParam.setId(IdUtil.simpleUUID());

        List<YayiMessage> messagesList = ListUtil.toList();
        YayiMessage messages = new YayiMessage();
        messages.setRole("user");
        messages.setContent(question);
        messagesList.add(messages);
        mathModelParam.setMessages(messagesList);

        if (null != mathModelConfig.getTemperature()) {
            mathModelParam.setTemperature(mathModelConfig.getTemperature());
        }
        if (null != mathModelConfig.getStream()) {
            mathModelParam.setStream(mathModelConfig.getStream());
        }

        if (null != mathModelConfig.getMaxTrySteps()) {
            mathModelParam.setMax_try_steps(mathModelConfig.getMaxTrySteps());
        }

        if (null != mathModelConfig.getMaxNewTokens()) {
            mathModelParam.setSetMax_new_tokens(mathModelConfig.getMaxNewTokens());
        }

        if (null != mathModelConfig.getFrequencyPenalty()) {
            mathModelParam.setFrequency_penalty(mathModelConfig.getFrequencyPenalty());
        }

        if (null != mathModelConfig.getPresencePenalty()) {
            mathModelParam.setPresence_penalty(mathModelConfig.getPresencePenalty());
        }
        if (null != mathModelConfig.getTopP()) {
            mathModelParam.setTop_p(mathModelConfig.getTopP());
        }
        if (null != mathModelConfig.getTopK()) {
            mathModelParam.setTop_k(mathModelConfig.getTopK());
        }

        if (null != mathModelConfig.getN()) {
            mathModelParam.setN(mathModelConfig.getN());
        }

        if (null != mathModelConfig.getDoSample()) {
            mathModelParam.setDo_sample(mathModelConfig.getDoSample());
        }

        if (StringUtils.isBlank(mathModelParam.getUri())) {
            mathModelParam.setUri(mathModelConfig.getUri());
        }

        if (StringUtils.isBlank(mathModelParam.getAppKey())) {
            mathModelParam.setAppKey(yayiConfig.getAppKey());
        }

        if (StringUtils.isBlank(mathModelParam.getAppSecret())) {
            mathModelParam.setAppSecret(yayiConfig.getAppSecret());
        }

        CopyOptions copyOptions = new CopyOptions();
        copyOptions.ignoreNullValue();
        List<YayiMessage> messages2 = param.getMessages();
        if (CollectionUtil.isNotEmpty(messages2)) {
            mathModelParam.setMessages(messages2);
        }
        BeanUtil.copyProperties(param, mathModelParam, copyOptions);
        BeanUtil.copyProperties(mathModelParam, param, copyOptions);
        return mathModelParam;
    }

    /**
     * 获取结果
     *
     * @param builder
     * @return
     */
    private MathModelResult getResult(Request.Builder builder, YayiParam yayiParam) {
        MathModelResult result = new MathModelResult();
        log.info("===>runMathModel url:{}  ,  param: {}", yayiParam.getUri(), JSONUtil.toJsonStr(yayiParam));
        String body = YayiApiUtils.invokeYaYiApi(builder, yayiParam);
        log.info("===>runMathModel body: {}", body);
        if (StringUtils.isNotBlank(body) && body.contains("QPS访问超出限制")) {
            result.setMsg("QPS访问超出限制");
        } else if (StringUtils.isNotBlank(body) && JSONUtil.isTypeJSONObject(body)) {
            JSONObject entries = JSONUtil.parseObj(body);
            result = entries.toBean(MathModelResult.class);
        }
        return result;
    }
}
