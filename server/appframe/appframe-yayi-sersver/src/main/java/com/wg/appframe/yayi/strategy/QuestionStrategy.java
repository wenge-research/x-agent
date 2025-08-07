package com.wg.appframe.yayi.strategy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wg.appframe.yayi.config.QuestionConfig;
import com.wg.appframe.yayi.config.YayiConfig;
import com.wg.appframe.yayi.param.QuestionParam;
import com.wg.appframe.yayi.param.YayiParam;
import com.wg.appframe.yayi.result.QuestionResult;
import com.wg.appframe.yayi.result.YayiResult;
import com.wg.appframe.yayi.utils.YayiApiUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class QuestionStrategy implements YayiApiStrategy{


    @Autowired(required = false)
    private QuestionConfig questionConfig;

    @Autowired(required = false)
    private YayiConfig yayiConfig;


    public YayiResult run(String inputText, YayiParam yayiParam) {
        QuestionResult result = new QuestionResult();
        if (yayiParam instanceof QuestionParam) {
            QuestionParam param = (QuestionParam) yayiParam;
            result = runQuestion(inputText, param);

            // 判断是否需要重试
            if (null != result && null != result.getRetryFlag() && result.getRetryFlag()) {
                // 重试次数
                int retryTimes = null != yayiParam.getRetryTimes() ? yayiParam.getRetryTimes() : null != questionConfig.getRetryTimes() ? questionConfig.getRetryTimes() : 1;
                int retryInterval = null != yayiParam.getRetryInterval() ? yayiParam.getRetryInterval() : null != questionConfig.getRetryInterval() ? questionConfig.getRetryInterval() : 300;
                for (int i = 0; i < retryTimes; i++) {

                    // 重试等待时间
                    ThreadUtil.safeSleep(retryInterval);
                    log.info("===>runQuestion 第{}次重试 ,总重试次数:{} ,重试间隔:{}毫秒, error:{}", i + 1, retryTimes, retryInterval, result.getMsg());
                    result = runQuestion(inputText, param);
                    if (!result.getRetryFlag()) {
                        break;
                    }
                }
            }
        }

        return result;
    }

    /**
     * 调用问题生成接口
     * @param inputText
     * @param yayiParam
     * @return
     */
    public QuestionResult runQuestion(String inputText, QuestionParam yayiParam) {
        QuestionResult result = new QuestionResult();
        result.setRetryFlag(false);
        try {
            // 创建POST请求
            final Request.Builder builder = new Request.Builder();

            // 封装重排模型接口入参
            QuestionParam questionParam = questionParam(inputText, yayiParam);

            // 设置雅意接口请求的头部参数
            YayiApiUtils.setHeader(builder, yayiParam.getUri(), yayiConfig, yayiParam);

            // 请求重排模型接口
            result = getResult(builder, questionParam);
            result.setRetryFlag(result.getMsg().contains("QPS"));
            return result;
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
    private QuestionParam questionParam(String inputText, QuestionParam param) {
        QuestionParam questionParam = new QuestionParam();
        questionParam.setId(IdUtil.simpleUUID());
        QuestionParam.Content content = new QuestionParam.Content();
        questionParam.setContent(content);
        content.setInput_text(inputText);

        if (StringUtils.isNotBlank(questionConfig.getModel())) {
            content.setModel(questionConfig.getModel());
        }
        questionParam.setRetryTimes(1);
        if (null != questionConfig.getRetryTimes()) {
            questionParam.setRetryTimes(questionConfig.getRetryTimes());
        }
        questionParam.setRetryInterval(3000);
        if (null != questionConfig.getRetryInterval()) {
            questionParam.setRetryInterval(questionConfig.getRetryInterval());
        }
        if (StringUtils.isBlank(questionParam.getUri())) {
            questionParam.setUri(questionConfig.getUri());
        }
        if (null != questionConfig.getMaxLength()) {
            content.setMax_length(questionConfig.getMaxLength());
        }
        if (null != questionConfig.getNoRepeatNgramSize()) {
            content.setNo_repeat_ngram_size(questionConfig.getNoRepeatNgramSize());
        }
        if (null != questionConfig.getNumBeams()) {
            content.setNum_beams(questionConfig.getNumBeams());
        }

        if (null != questionConfig.getQuestionsNum()) {
            content.setQuestions_num(questionConfig.getQuestionsNum());
        }

        CopyOptions copyOptions = new CopyOptions();
        copyOptions.ignoreNullValue();
        QuestionParam.Content content1 = param.getContent();
        if (null != content1) {
            BeanUtil.copyProperties(content1, content, copyOptions);
            BeanUtil.copyProperties(content, content1, copyOptions);
        }

        BeanUtil.copyProperties(param, questionParam, copyOptions);
        BeanUtil.copyProperties(questionParam, param, copyOptions);
        return questionParam;
    }

    /**
     * 获取结果
     *
     * @param builder
     * @return
     */
    private QuestionResult getResult(Request.Builder builder, YayiParam yayiParam) {
        QuestionResult result = new QuestionResult();
        log.info("===>runQuestion url:{}  ,  param: {}", yayiParam.getUri(), JSONUtil.toJsonStr(yayiParam));
        String body = YayiApiUtils.invokeYaYiApi(builder, yayiParam);
        log.info("===>runQuestion body: {}", body);
        if (StringUtils.isNotBlank(body) && body.contains("QPS访问超出限制")) {
            result.setMsg("QPS访问超出限制");
        } else if (StringUtils.isNotBlank(body) && JSONUtil.isTypeJSONObject(body)) {
            JSONObject entries = JSONUtil.parseObj(body);
            result = entries.toBean(QuestionResult.class);
        }
        return result;
    }
}
