package com.wg.appframe.yayi.strategy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wg.appframe.yayi.config.YayiConfig;
import com.wg.appframe.yayi.config.YayiEmbeddingConfig;
import com.wg.appframe.yayi.param.EmbeddingParam;
import com.wg.appframe.yayi.param.YayiParam;
import com.wg.appframe.yayi.result.EmbeddingResult;
import com.wg.appframe.yayi.result.YayiResult;
import com.wg.appframe.yayi.utils.YayiApiUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class EmbeddingStrategy implements YayiApiStrategy {

    @Autowired(required = false)
    private YayiEmbeddingConfig yayiEmbeddingConfig;

    @Autowired(required = false)
    private YayiConfig yayiConfig;

    @Override
    public YayiResult run(List<String> content, YayiParam yayiParam) {
        EmbeddingResult result = new EmbeddingResult();
        if (yayiParam instanceof EmbeddingParam) {
            EmbeddingParam param = (EmbeddingParam) yayiParam;
            result = postApi(content, param);

            // 判断是否需要重试
            if (null != result && null != result.getRetryFlag() && result.getRetryFlag()) {
                // 重试次数
                int retryTimes = null != yayiParam.getRetryTimes() ? yayiParam.getRetryTimes() : null != yayiEmbeddingConfig.getRetryTimes() ? yayiEmbeddingConfig.getRetryTimes() : 1;
                int retryInterval = null != yayiParam.getRetryInterval() ? yayiParam.getRetryInterval() : null != yayiEmbeddingConfig.getRetryInterval() ? yayiEmbeddingConfig.getRetryInterval() : 300;
                for (int i = 0; i < retryTimes; i++) {
                    // 重试等待时间
                    ThreadUtil.safeSleep(retryInterval);
                    log.info("===>embeddingStrategy 第{}次重试 ,总重试次数:{} ,重试间隔:{}毫秒, error:{}", i + 1, retryTimes, retryInterval, result.getMsg());
                    result = postApi(content, param);
                    if (!result.getRetryFlag()) {
                        break;
                    }
                }
            }
        }

        return result;
    }

    private EmbeddingResult postApi(List<String> content, EmbeddingParam param) {
        EmbeddingResult result = new EmbeddingResult();
        try {
            // 创建POST请求
            final Request.Builder builder = new Request.Builder();

            // 设置接口请求的头部参数
            YayiApiUtils.setHeader(builder, yayiEmbeddingConfig.getUri(), yayiConfig, param);

            // 封装接口入参
            EmbeddingParam embeddingParam = getEmbeddingParam(content, param);

            // 请求雅意接口
            log.info("===>embeddingStrategy url:{}  ,  param: {}", yayiEmbeddingConfig.getUri(), JSONUtil.toJsonStr(embeddingParam));
            String body = YayiApiUtils.invokeYaYiApi(builder, embeddingParam);
            log.info("===>embeddingStrategy body: {}", body);
            if (StringUtils.isNotBlank(body) && body.contains("QPS访问超出限制")) {
                result.setMsg("QPS访问超出限制");
            } else if (StringUtils.isNotBlank(body) && JSONUtil.isTypeJSONObject(body)) {
                JSONObject entries = JSONUtil.parseObj(body);
                result = entries.toBean(EmbeddingResult.class);
            }
            result.setRetryFlag(result.getMsg().contains("QPS"));

        } catch (Exception e) {
            log.error("error:{}", e.getMessage(), e);
        }
        return result;
    }

    /**
     * 初始化雅意接口参数
     * @param contentList
     * @param param
     * @return
     */
    private EmbeddingParam getEmbeddingParam(List<String> contentList, EmbeddingParam param) {
        EmbeddingParam apiParam = new EmbeddingParam();
        apiParam.setId(IdUtil.simpleUUID());

        EmbeddingParam.Content content = new EmbeddingParam.Content();
        apiParam.setContent(content);
        if (StringUtils.isNotBlank(yayiEmbeddingConfig.getFunction())) {
            content.setFunction(yayiEmbeddingConfig.getFunction());
        }
        if (null != yayiEmbeddingConfig.getModel()) {
            content.setModel(yayiEmbeddingConfig.getModel());
        }
        if (null != yayiEmbeddingConfig.getDataType()) {
            content.setData_type(yayiEmbeddingConfig.getDataType());
        }
        if (StringUtils.isBlank(apiParam.getAppKey())) {
            apiParam.setAppKey(yayiConfig.getAppKey());
        }

        if (StringUtils.isBlank(apiParam.getAppSecret())) {
            apiParam.setAppSecret(yayiConfig.getAppSecret());
        }

        apiParam.setRetryTimes(1);
        if (null != yayiEmbeddingConfig.getRetryTimes()) {
            apiParam.setRetryTimes(yayiEmbeddingConfig.getRetryTimes());
        }
        apiParam.setRetryInterval(3000);
        if (null != yayiEmbeddingConfig.getRetryInterval()) {
            apiParam.setRetryInterval(yayiEmbeddingConfig.getRetryInterval());
        }

        if (StringUtils.isBlank(apiParam.getUri())) {
            apiParam.setUri(yayiEmbeddingConfig.getUri());
        }

        if (StringUtils.isBlank(apiParam.getAppKey())) {
            apiParam.setAppKey(yayiConfig.getAppKey());
        }

        if (StringUtils.isBlank(apiParam.getAppSecret())) {
            apiParam.setAppSecret(yayiConfig.getAppSecret());
        }
        content.setData_list(contentList);
        CopyOptions copyOptions = new CopyOptions();
        copyOptions.ignoreNullValue();
        EmbeddingParam.Content content1 = param.getContent();
        if (null != content1) {
            BeanUtil.copyProperties(content1, content, copyOptions);
            BeanUtil.copyProperties(content, content1, copyOptions);
        }
        BeanUtil.copyProperties(param, apiParam, copyOptions);
        BeanUtil.copyProperties(apiParam, param, copyOptions);
        return apiParam;
    }

}
