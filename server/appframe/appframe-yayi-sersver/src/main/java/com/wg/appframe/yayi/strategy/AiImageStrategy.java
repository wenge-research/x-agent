package com.wg.appframe.yayi.strategy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wg.appframe.yayi.config.AiImageConfig;
import com.wg.appframe.yayi.config.YayiAutoConfigure;
import com.wg.appframe.yayi.config.YayiConfig;
import com.wg.appframe.yayi.param.AiImageParam;
import com.wg.appframe.yayi.param.YayiParam;
import com.wg.appframe.yayi.result.AiImageResult;
import com.wg.appframe.yayi.result.YayiResult;
import com.wg.appframe.yayi.utils.YayiApiUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class AiImageStrategy implements YayiApiStrategy{


    @Autowired(required = false)
    private AiImageConfig aiImageConfig;

    @Autowired(required = false)
    private YayiConfig yayiConfig;


    public YayiResult run(String fileUrl, YayiParam yayiParam) {
        AiImageResult result = new AiImageResult();
        if (yayiParam instanceof AiImageParam) {
            AiImageParam param = (AiImageParam) yayiParam;
            result = runAiImage(fileUrl, param);

            // 判断是否需要重试
            if (null != result && null != result.getRetryFlag() && result.getRetryFlag()) {
                // 重试次数
                int retryTimes = null != yayiParam.getRetryTimes() ? yayiParam.getRetryTimes() : null != aiImageConfig.getRetryTimes() ? aiImageConfig.getRetryTimes() : 1;
                int retryInterval = null != yayiParam.getRetryInterval() ? yayiParam.getRetryInterval() : null != aiImageConfig.getRetryInterval() ? aiImageConfig.getRetryInterval() : 300;
                for (int i = 0; i < retryTimes; i++) {

                    // 重试等待时间
                    ThreadUtil.safeSleep(retryInterval);
                    log.info("===>AiImageStrategy 第{}次重试 ,总重试次数:{} ,重试间隔:{}毫秒, error:{}", i + 1, retryTimes, retryInterval, result.getMsg());
                    result = runAiImage(fileUrl, param);
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
     * @param fileUrl
     * @param yayiParam
     * @return
     */
    public AiImageResult runAiImage(String fileUrl, AiImageParam yayiParam) {
        AiImageResult result = new AiImageResult();
        result.setRetryFlag(false);
        try {
            // 创建POST请求
            final Request.Builder builder = new Request.Builder();

            // 设置雅意接口请求的头部参数
            YayiApiUtils.setHeader(builder, aiImageConfig.getUri(), yayiConfig, yayiParam);

            // 封装文档解析接口入参
            AiImageParam documentParam = documentParam(fileUrl, yayiParam);

            // 请求文档解析接口
            result = getResult(builder, documentParam);
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
    private AiImageParam documentParam(String fileUrl, AiImageParam param) {
        AiImageParam documentParam = new AiImageParam();
        documentParam.setId(IdUtil.simpleUUID());
        documentParam.setUrl(fileUrl);
        AiImageParam.Content content = new AiImageParam.Content();
        documentParam.setContent(content);

        documentParam.setRetryTimes(1);
        if (null != aiImageConfig.getRetryTimes()) {
            documentParam.setRetryTimes(aiImageConfig.getRetryTimes());
        }
        documentParam.setRetryInterval(3000);
        if (null != aiImageConfig.getRetryInterval()) {
            documentParam.setRetryInterval(aiImageConfig.getRetryInterval());
        }

        if (StringUtils.isBlank(documentParam.getUri())) {
            documentParam.setUri(aiImageConfig.getUri());
        }

        if (StringUtils.isBlank(documentParam.getAppKey())) {
            documentParam.setAppKey(yayiConfig.getAppKey());
        }

        if (StringUtils.isBlank(documentParam.getAppSecret())) {
            documentParam.setAppSecret(yayiConfig.getAppSecret());
        }

        CopyOptions copyOptions = new CopyOptions();
        copyOptions.ignoreNullValue();
        AiImageParam.Content content1 = param.getContent();
        if (null != content1) {
            BeanUtil.copyProperties(content1, content, copyOptions);
            BeanUtil.copyProperties(content, content1, copyOptions);
        }
        BeanUtil.copyProperties(param, documentParam, copyOptions);
        BeanUtil.copyProperties(documentParam, param, copyOptions);
        return documentParam;
    }

    /**
     * 获取结果
     *
     * @param builder
     * @return
     */
    private AiImageResult getResult(Request.Builder builder, YayiParam yayiParam) {
        AiImageResult result = new AiImageResult();
        log.info("===>aiImageConfig url:{}  ,  param: {}", yayiParam.getUri(), JSONUtil.toJsonStr(yayiParam));
        String body = invokeYaYiApi(builder, yayiParam);
        log.info("===>aiImageConfig body: {}", body);
        if (StringUtils.isNotBlank(body) && body.contains("QPS访问超出限制")) {
            result.setMsg("QPS访问超出限制");
        } else if (StringUtils.isNotBlank(body) && JSONUtil.isTypeJSONObject(body)) {
            JSONObject entries = JSONUtil.parseObj(body);
            result = entries.toBean(AiImageResult.class);
        }
        return result;
    }

    /**
     * 调用雅意大模型算法
     * @param builder
     * @return
     */
    public String invokeYaYiApi(Request.Builder builder, YayiParam yayiParam) {
        try {
            final String contentType = "application/json; charset=utf-8";

            final MediaType mediaType = MediaType.parse(contentType);
            final RequestBody body = RequestBody.create(mediaType, JSONUtil.toJsonStr(yayiParam));

            Request request = builder
                    .url(yayiParam.getUri())
                    .post(body)
                    .build();

            yayiParam.setConnectTimeout(100);
            yayiParam.setReadTimeout(3600);
            OkHttpClient okHttpClient = YayiAutoConfigure.getOkHttpClient(yayiParam);
            try (Response response = okHttpClient.newCall(request).execute();
                 ResponseBody responseBody = response.body()) {
                if (null != responseBody) {
                    return responseBody.string();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            log.error("调用雅意大模型算法失败，url = {}", yayiParam.getUri(), e);
        }
        return "";
    }
}
