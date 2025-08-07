package com.wg.appframe.yayi.strategy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wg.appframe.yayi.config.VisualConfig;
import com.wg.appframe.yayi.config.YayiConfig;
import com.wg.appframe.yayi.entity.YayiMessage;
import com.wg.appframe.yayi.param.VisualParam;
import com.wg.appframe.yayi.param.YayiParam;
import com.wg.appframe.yayi.result.VisualResult;
import com.wg.appframe.yayi.result.YayiResult;
import com.wg.appframe.yayi.utils.YayiApiUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class VisualStrategy implements YayiApiStrategy{


    @Autowired(required = false)
    private VisualConfig visualConfig;

    @Autowired(required = false)
    private YayiConfig yayiConfig;


    public YayiResult run(String fileUrl, YayiParam yayiParam) {
        VisualResult result = new VisualResult();
        if (yayiParam instanceof VisualParam) {
            VisualParam param = (VisualParam) yayiParam;
            result = runVisual(fileUrl, param);

            // 判断是否需要重试
            if (null != result && null != result.getRetryFlag() && result.getRetryFlag()) {
                // 重试次数
                int retryTimes = null != yayiParam.getRetryTimes() ? yayiParam.getRetryTimes() : null != visualConfig.getRetryTimes() ? visualConfig.getRetryTimes() : 1;
                int retryInterval = null != yayiParam.getRetryInterval() ? yayiParam.getRetryInterval() : null != visualConfig.getRetryInterval() ? visualConfig.getRetryInterval() : 300;
                for (int i = 0; i < retryTimes; i++) {

                    // 重试等待时间
                    ThreadUtil.safeSleep(retryInterval);
                    log.info("===>runVisual 第{}次重试 ,总重试次数:{} ,重试间隔:{}毫秒, error:{}", i + 1, retryTimes, retryInterval, result.getMsg());
                    result = runVisual(fileUrl, param);
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
    public VisualResult runVisual(String fileUrl, VisualParam yayiParam) {
        VisualResult result = new VisualResult();
        result.setRetryFlag(false);
        try {
            // 创建POST请求
            final Request.Builder builder = new Request.Builder();

            // 设置雅意接口请求的头部参数
            YayiApiUtils.setHeader(builder, visualConfig.getUri(), yayiConfig, yayiParam);

            // 封装图片解析接口入参
            VisualParam visualParam = visualParam(fileUrl, yayiParam);

            // 请求图片解析接口
            result = getResult(builder, visualParam);
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
    private VisualParam visualParam(String fileUrl, VisualParam param) {
        VisualParam visualParam = new VisualParam();
        visualParam.setId(IdUtil.simpleUUID());
        visualParam.setUrl(fileUrl);

        if (StringUtils.isNotBlank(visualConfig.getModel())) {
            visualParam.setModel(visualConfig.getModel());
        }

        if (null != visualConfig.getStream()) {
            visualParam.setStream(visualConfig.getStream());
        }
        if (null != visualConfig.getTopP()) {
            visualParam.setTop_p(visualConfig.getTopP());
        }
        if (null != visualConfig.getTopK()) {
            visualParam.setTop_k(visualConfig.getTopK());
        }
        if (null != visualConfig.getTemperature()) {
            visualParam.setTemperature(visualConfig.getTemperature());
        }
        if (null != visualConfig.getMaxNewTokens()) {
            visualParam.setMax_new_tokens(visualConfig.getMaxNewTokens());
        }
        if (null != visualConfig.getDoSample()) {
            visualParam.setDo_sample(visualConfig.getDoSample());
        }

        if (StringUtils.isBlank(visualParam.getUri())) {
            visualParam.setUri(visualConfig.getUri());
        }

        if (StringUtils.isBlank(visualParam.getAppKey())) {
            visualParam.setAppKey(yayiConfig.getAppKey());
        }

        if (StringUtils.isBlank(visualParam.getAppSecret())) {
            visualParam.setAppSecret(yayiConfig.getAppSecret());
        }

        CopyOptions copyOptions = new CopyOptions();
        copyOptions.ignoreNullValue();
        List<YayiMessage> messages = param.getMessages();
        if (CollectionUtil.isNotEmpty(messages)) {
            visualParam.setMessages(messages);
        }
        BeanUtil.copyProperties(param, visualParam, copyOptions);
        BeanUtil.copyProperties(visualParam, param, copyOptions);
        return visualParam;
    }

    /**
     * 获取结果
     *
     * @param builder
     * @return
     */
    private VisualResult getResult(Request.Builder builder, YayiParam yayiParam) {
        VisualResult result = new VisualResult();
        log.info("===>runVisual url:{}  ,  param: {}", yayiParam.getUri(), JSONUtil.toJsonStr(yayiParam));
        String body = YayiApiUtils.invokeYaYiApi(builder, yayiParam);
        log.info("===>runVisual body: {}", body);
        if (StringUtils.isNotBlank(body) && body.contains("QPS访问超出限制")) {
            result.setMsg("QPS访问超出限制");
        } else if (StringUtils.isNotBlank(body) && JSONUtil.isTypeJSONObject(body)) {
            JSONObject entries = JSONUtil.parseObj(body);
            result = entries.toBean(VisualResult.class);
        }
        return result;
    }
}
