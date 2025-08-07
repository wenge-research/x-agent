package com.wg.appframe.yayi.strategy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wg.appframe.yayi.config.RearrangeConfig;
import com.wg.appframe.yayi.config.YayiConfig;
import com.wg.appframe.yayi.param.RearrangeParam;
import com.wg.appframe.yayi.param.YayiParam;
import com.wg.appframe.yayi.result.RearrangeResult;
import com.wg.appframe.yayi.result.YayiResult;
import com.wg.appframe.yayi.utils.YayiApiUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class RearrangeStrategy implements YayiApiStrategy{


    @Autowired(required = false)
    private RearrangeConfig rearrangeConfig;

    @Autowired(required = false)
    private YayiConfig yayiConfig;

    
    public YayiResult run(YayiParam yayiParam) {
        RearrangeResult result = new RearrangeResult();
        if (yayiParam instanceof RearrangeParam) {
            RearrangeParam param = (RearrangeParam) yayiParam;
            result = runRearrange(param);

            // 判断是否需要重试
            if (null != result && null != result.getRetryFlag() && result.getRetryFlag()) {
                // 重试次数
                int retryTimes = null != yayiParam.getRetryTimes() ? yayiParam.getRetryTimes() : null != rearrangeConfig.getRetryTimes() ? rearrangeConfig.getRetryTimes() : 1;
                int retryInterval = null != yayiParam.getRetryInterval() ? yayiParam.getRetryInterval() : null != rearrangeConfig.getRetryInterval() ? rearrangeConfig.getRetryInterval() : 300;
                for (int i = 0; i < retryTimes; i++) {
                    // 重试等待时间
                    ThreadUtil.safeSleep(retryInterval);
                    log.info("===>runRearrange 第{}次重试 ,总重试次数:{} ,重试间隔:{}毫秒, error:{}", i + 1, retryTimes, retryInterval, result.getMsg());
                    result = runRearrange(param);
                    if (!result.getRetryFlag()) {
                        break;
                    }
                }
            }
        }

        return result;
    }

    /**
     * 调用重排模型接口
     * @param yayiParam
     * @return
     */
    public RearrangeResult runRearrange(RearrangeParam yayiParam) {
        RearrangeResult result = new RearrangeResult();
        result.setRetryFlag(false);
        try {
            // 创建POST请求
            final Request.Builder builder = new Request.Builder();

            // 封装重排模型接口入参
            RearrangeParam rearrangeParam = rearrangeParam(yayiParam);

            // 设置雅意接口请求的头部参数
            YayiApiUtils.setHeader(builder, yayiParam.getUri(), yayiConfig, yayiParam);

            // 请求重排模型接口
            result = getResult(builder, rearrangeParam);
            result.setRetryFlag(result.getMsg().contains("QPS"));
            return result;
        } catch (Exception e) {
            log.error("error:{}", e.getMessage(), e);
        }

        return result;
    }

    /**
     * 初始化重排模型接口参数
     * @param param
     * @return
     */
    private RearrangeParam rearrangeParam(RearrangeParam param) {
        RearrangeParam rearrangeParam = new RearrangeParam();
        rearrangeParam.setId(IdUtil.simpleUUID());
        RearrangeParam.Content content = new RearrangeParam.Content();
        rearrangeParam.setContent(content);
        if (null != rearrangeConfig.getN()) {
            content.setN(rearrangeConfig.getN());
        }
        if (StringUtils.isNotBlank(rearrangeConfig.getFunction())) {
            content.setFunction(rearrangeConfig.getFunction());
        }
        if (null != rearrangeConfig.getBatchSize()) {
            content.setBatch_size(rearrangeConfig.getBatchSize());
        }

        rearrangeParam.setRetryTimes(1);
        if (null != rearrangeConfig.getRetryTimes()) {
            rearrangeParam.setRetryTimes(rearrangeConfig.getRetryTimes());
        }
        rearrangeParam.setRetryInterval(3000);
        if (null != rearrangeConfig.getRetryInterval()) {
            rearrangeParam.setRetryInterval(rearrangeConfig.getRetryInterval());
        }

        if (StringUtils.isBlank(rearrangeParam.getUri())) {
            rearrangeParam.setUri(rearrangeConfig.getUri());
        }

        CopyOptions copyOptions = new CopyOptions();
        copyOptions.ignoreNullValue();
        RearrangeParam.Content content1 = param.getContent();
        if (null != content1) {
            BeanUtil.copyProperties(content1, content, copyOptions);
            BeanUtil.copyProperties(content, content1, copyOptions);
        }
        BeanUtil.copyProperties(param, rearrangeParam, copyOptions);
        BeanUtil.copyProperties(rearrangeParam, param, copyOptions);
        return rearrangeParam;
    }

    /**
     * 获取结果
     *
     * @param builder
     * @return
     */
    private RearrangeResult getResult(Request.Builder builder, YayiParam yayiParam) {
        RearrangeResult result = new RearrangeResult();
        log.info("===>runRearrange重排模型 url:{}  ,  param: {}", yayiParam.getUri(), JSONUtil.toJsonStr(yayiParam));
        String body = YayiApiUtils.invokeYaYiApi(builder, yayiParam);
        log.info("===>runRearrange重排模型 body: {}", body);
        if (StringUtils.isNotBlank(body) && body.contains("QPS访问超出限制")) {
            result.setMsg("QPS访问超出限制");
        } else if (StringUtils.isNotBlank(body) && JSONUtil.isTypeJSONObject(body)) {
            JSONObject entries = JSONUtil.parseObj(body);
            result = entries.toBean(RearrangeResult.class);
        }
        return result;
    }
}
