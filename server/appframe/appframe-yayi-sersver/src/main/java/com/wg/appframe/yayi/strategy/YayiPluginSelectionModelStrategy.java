package com.wg.appframe.yayi.strategy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wg.appframe.yayi.config.YayiPluginSelectionModelConfig;
import com.wg.appframe.yayi.config.YayiConfig;
import com.wg.appframe.yayi.param.YayiPluginSelectionModelParam;
import com.wg.appframe.yayi.param.YayiParam;
import com.wg.appframe.yayi.result.YayiPluginSelectionModelResult;
import com.wg.appframe.yayi.result.YayiResult;
import com.wg.appframe.yayi.utils.YayiApiUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class YayiPluginSelectionModelStrategy implements YayiApiStrategy {

    @Autowired(required = false)
    private YayiPluginSelectionModelConfig yayiPluginSelectionModelConfig;

    @Autowired(required = false)
    private YayiConfig yayiConfig;

    public YayiResult run(YayiParam yayiParam) {
        YayiPluginSelectionModelResult result = new YayiPluginSelectionModelResult();
        if (yayiParam instanceof YayiPluginSelectionModelParam) {
            YayiPluginSelectionModelParam param = (YayiPluginSelectionModelParam) yayiParam;
            result = yayiPluginSelectionModelStrategy(param);

            // 判断是否需要重试
            if (null != result && null != result.getRetryFlag() && result.getRetryFlag()) {
                // 重试次数
                int retryTimes = null != yayiParam.getRetryTimes() ? yayiParam.getRetryTimes() : null != yayiPluginSelectionModelConfig.getRetryTimes() ? yayiPluginSelectionModelConfig.getRetryTimes() : 1;
                int retryInterval = null != yayiParam.getRetryInterval() ? yayiParam.getRetryInterval() : null != yayiPluginSelectionModelConfig.getRetryInterval() ? yayiPluginSelectionModelConfig.getRetryInterval() : 300;
                for (int i = 0; i < retryTimes; i++) {
                    // 重试等待时间
                    ThreadUtil.safeSleep(retryInterval);
                    log.info("===>yayiPluginSelectionModelStrategy 第{}次重试 ,总重试次数:{} ,重试间隔:{}毫秒, error:{}", i + 1, retryTimes, retryInterval, result.getMsg());
                    result = yayiPluginSelectionModelStrategy(param);
                    if (!result.getRetryFlag()) {
                        break;
                    }
                }
            }
        }

        return result;
    }

    /**
     * 调用插件选择模型接口
     * @param yayiParam
     * @return
     */
    public YayiPluginSelectionModelResult yayiPluginSelectionModelStrategy(YayiPluginSelectionModelParam yayiParam) {
        YayiPluginSelectionModelResult result = new YayiPluginSelectionModelResult();
        result.setRetryFlag(false);
        try {
            // 创建POST请求
            final Request.Builder builder = new Request.Builder();

            // 封装插件选择模型接口入参
            YayiPluginSelectionModelParam modelParam = pluginSelectionModelStrategy(yayiParam);

            // 设置雅意接口请求的头部参数
            YayiApiUtils.setHeader(builder, yayiParam.getUri(), yayiConfig, yayiParam);

            // 请求插件选择模型接口
            result = getResult(builder, modelParam);
            result.setRetryFlag(result.getMsg().contains("QPS"));
            return result;
        } catch (Exception e) {
            log.error("error:{}", e.getMessage(), e);
        }

        return result;
    }

    /**
     * 初始化插件选择模型接口参数
     * @param param
     * @return
     */
    private YayiPluginSelectionModelParam pluginSelectionModelStrategy(YayiPluginSelectionModelParam param) {
        if (CollectionUtil.isEmpty(param.getMessages())) {
            return param;
        }
        YayiPluginSelectionModelParam modelParam = new YayiPluginSelectionModelParam();
        modelParam.setId(IdUtil.simpleUUID());

        if (null != yayiPluginSelectionModelConfig.getBest_of()) {
            modelParam.setBest_of(yayiPluginSelectionModelConfig.getBest_of());
        }

        if (CollectionUtil.isNotEmpty(yayiPluginSelectionModelConfig.getAvaliable_plugins())) {
            modelParam.setAvaliable_plugins(yayiPluginSelectionModelConfig.getAvaliable_plugins());
        }
        if (null != yayiPluginSelectionModelConfig.getMax_new_tokens()) {
            modelParam.setMax_new_tokens(yayiPluginSelectionModelConfig.getMax_new_tokens());
        }
        if (null != yayiPluginSelectionModelConfig.getMax_window_size()) {
            modelParam.setMax_window_size(yayiPluginSelectionModelConfig.getMax_window_size());
        }
        if (null != yayiPluginSelectionModelConfig.getUse_recommendation()) {
            modelParam.setUse_recommendation(yayiPluginSelectionModelConfig.getUse_recommendation());
        }
        if (null != yayiPluginSelectionModelConfig.getUse_only_custom_plugins()) {
            modelParam.setUse_only_custom_plugins(yayiPluginSelectionModelConfig.getUse_only_custom_plugins());
        }

        modelParam.setRetryTimes(1);
        if (null != yayiPluginSelectionModelConfig.getRetryTimes()) {
            modelParam.setRetryTimes(yayiPluginSelectionModelConfig.getRetryTimes());
        }
        modelParam.setRetryInterval(3000);
        if (null != yayiPluginSelectionModelConfig.getRetryInterval()) {
            modelParam.setRetryInterval(yayiPluginSelectionModelConfig.getRetryInterval());
        }

        if (StringUtils.isBlank(modelParam.getUri())) {
            modelParam.setUri(yayiPluginSelectionModelConfig.getUri());
        }

        CopyOptions copyOptions = new CopyOptions();
        copyOptions.ignoreNullValue();
        BeanUtil.copyProperties(param, modelParam, copyOptions);
        BeanUtil.copyProperties(modelParam, param, copyOptions);
        return modelParam;
    }

    /**
     * 获取结果
     *
     * @param builder
     * @return
     */
    private YayiPluginSelectionModelResult getResult(Request.Builder builder, YayiParam yayiParam) {
        YayiPluginSelectionModelResult result = new YayiPluginSelectionModelResult();
        log.info("===>pluginSelectionModel插件选择模型 url:{}  ,  param: {}", yayiParam.getUri(), JSONUtil.toJsonStr(yayiParam));
        String body = YayiApiUtils.invokeYaYiApi(builder, yayiParam);
        log.info("===>pluginSelectionModel插件选择模型 body: {}", body);
        if (StringUtils.isNotBlank(body) && body.contains("QPS访问超出限制")) {
            result.setMsg("QPS访问超出限制");
        } else if (StringUtils.isNotBlank(body) && JSONUtil.isTypeJSONObject(body)) {
            JSONObject entries = JSONUtil.parseObj(body);
            result = entries.toBean(YayiPluginSelectionModelResult.class);
        }
        return result;
    }
}
