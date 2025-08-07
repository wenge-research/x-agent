package com.wg.appframe.yayi.strategy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wg.appframe.yayi.config.ChartConfig;
import com.wg.appframe.yayi.config.YayiConfig;
import com.wg.appframe.yayi.entity.YayiMessage;
import com.wg.appframe.yayi.param.ChartParam;
import com.wg.appframe.yayi.param.YayiParam;
import com.wg.appframe.yayi.result.ChartResult;
import com.wg.appframe.yayi.result.ChartResult;
import com.wg.appframe.yayi.utils.YayiApiUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class ChartStrategy implements YayiApiStrategy{


    @Autowired(required = false)
    private ChartConfig chartConfig;

    @Autowired(required = false)
    private YayiConfig yayiConfig;


    public ChartResult run(String fileUrl, YayiParam yayiParam) {
        ChartResult result = new ChartResult();
        if (yayiParam instanceof ChartParam) {
            ChartParam param = (ChartParam) yayiParam;
            result = runChart(fileUrl, param);

            // 判断是否需要重试
            if (null != result && null != result.getRetryFlag() && result.getRetryFlag()) {
                // 重试次数
                int retryTimes = null != yayiParam.getRetryTimes() ? yayiParam.getRetryTimes() : null != chartConfig.getRetryTimes() ? chartConfig.getRetryTimes() : 1;
                int retryInterval = null != yayiParam.getRetryInterval() ? yayiParam.getRetryInterval() : null != chartConfig.getRetryInterval() ? chartConfig.getRetryInterval() : 300;
                for (int i = 0; i < retryTimes; i++) {

                    // 重试等待时间
                    ThreadUtil.safeSleep(retryInterval);
                    log.info("===>runChart 第{}次重试 ,总重试次数:{} ,重试间隔:{}毫秒, error:{}", i + 1, retryTimes, retryInterval, result.getMsg());
                    result = runChart(fileUrl, param);
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
    public ChartResult runChart(String fileUrl, ChartParam yayiParam) {
        ChartResult result = new ChartResult();
        result.setRetryFlag(false);
        try {
            // 创建POST请求
            final Request.Builder builder = new Request.Builder();

            // 设置雅意接口请求的头部参数
            YayiApiUtils.setHeader(builder, chartConfig.getUri(), yayiConfig, yayiParam);

            // 封装图片解析接口入参
            ChartParam chartParam = chartParam(fileUrl, yayiParam);

            // 请求图片解析接口
            result = getResult(builder, chartParam);
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
    private ChartParam chartParam(String fileUrl, ChartParam param) {
        ChartParam chartParam = new ChartParam();
        chartParam.setId(IdUtil.simpleUUID());
        chartParam.setUrl(fileUrl);

        if (StringUtils.isNotBlank(chartConfig.getModel())) {
            chartParam.setModel(chartConfig.getModel());
        }

        if (null != chartConfig.getStream()) {
            chartParam.setStream(chartConfig.getStream());
        }
        if (null != chartConfig.getTopP()) {
            chartParam.setTop_p(chartConfig.getTopP());
        }
        if (null != chartConfig.getTemperature()) {
            chartParam.setTemperature(chartConfig.getTemperature());
        }
        if (null != chartConfig.getMaxNewTokens()) {
            chartParam.setMax_new_tokens(chartConfig.getMaxNewTokens());
        }
        if (null != chartConfig.getDoSample()) {
            chartParam.setDo_sample(chartConfig.getDoSample());
        }

        if (StringUtils.isBlank(chartParam.getUri())) {
            chartParam.setUri(chartConfig.getUri());
        }

        if (StringUtils.isBlank(chartParam.getAppKey())) {
            chartParam.setAppKey(yayiConfig.getAppKey());
        }

        if (StringUtils.isBlank(chartParam.getAppSecret())) {
            chartParam.setAppSecret(yayiConfig.getAppSecret());
        }

        CopyOptions copyOptions = new CopyOptions();
        copyOptions.ignoreNullValue();
        List<YayiMessage> messages = param.getMessages();
        if (CollectionUtil.isNotEmpty(messages)) {
            chartParam.setMessages(messages);
        }
        BeanUtil.copyProperties(param, chartParam, copyOptions);
        BeanUtil.copyProperties(chartParam, param, copyOptions);
        return chartParam;
    }

    /**
     * 获取结果
     *
     * @param builder
     * @return
     */
    private ChartResult getResult(Request.Builder builder, YayiParam yayiParam) {
        ChartResult result = new ChartResult();
        log.info("===>runChart url:{}  ,  param: {}", yayiParam.getUri(), JSONUtil.toJsonStr(yayiParam));
        String body = YayiApiUtils.invokeYaYiApi(builder, yayiParam);
        log.info("===>runChart body: {}", body);
        if (StringUtils.isNotBlank(body) && body.contains("QPS访问超出限制")) {
            result.setMsg("QPS访问超出限制");
        } else if (StringUtils.isNotBlank(body) && JSONUtil.isTypeJSONObject(body)) {
            JSONObject entries = JSONUtil.parseObj(body);
            result = entries.toBean(ChartResult.class);
        }
        return result;
    }
}
