package com.wg.appframe.yayi.strategy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wg.appframe.yayi.config.Url2TextConfig;
import com.wg.appframe.yayi.config.YayiConfig;
import com.wg.appframe.yayi.param.UrlTextParam;
import com.wg.appframe.yayi.param.YayiParam;
import com.wg.appframe.yayi.result.UrlTextResult;
import com.wg.appframe.yayi.result.YayiResult;
import com.wg.appframe.yayi.utils.YayiApiUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class UrlTextStrategy implements YayiApiStrategy{


    @Autowired(required = false)
    private Url2TextConfig url2TextConfig;

    @Autowired(required = false)
    private YayiConfig yayiConfig;


    public YayiResult run(String url, YayiParam yayiParam) {
        UrlTextResult result = new UrlTextResult();
        if (yayiParam instanceof UrlTextParam) {
            UrlTextParam param = (UrlTextParam) yayiParam;
            result = runUrl(url, param);


            // 判断是否需要重试
            if (null != result && null != result.getRetryFlag() && result.getRetryFlag()) {
                // 重试次数
                int retryTimes = null != yayiParam.getRetryTimes() ? yayiParam.getRetryTimes() : null != url2TextConfig.getRetryTimes() ? url2TextConfig.getRetryTimes() : 1;
                int retryInterval = null != yayiParam.getRetryInterval() ? yayiParam.getRetryInterval() : null != url2TextConfig.getRetryInterval() ? url2TextConfig.getRetryInterval() : 300;
                for (int i = 0; i < retryTimes; i++) {

                    // 重试等待时间
                    ThreadUtil.safeSleep(retryInterval);
                    log.info("===>runUrl 第{}次重试 ,总重试次数:{} ,重试间隔:{}毫秒, error:{}", i + 1, retryTimes, retryInterval, result.getMsg());
                    result = runUrl(url, param);
                    if (!result.getRetryFlag()) {
                        break;
                    }
                }
            }
        }

        return result;
    }

    public UrlTextResult runUrl(String url, UrlTextParam yayiParam) {
        UrlTextResult result = new UrlTextResult();
        try {
            // 创建POST请求
            final Request.Builder builder = new Request.Builder();

            // 封装重排模型接口入参
            UrlTextParam urlTextParam = urlParam(url, yayiParam);
            
            // 设置雅意接口请求的头部参数
            YayiApiUtils.setHeader(builder, urlTextParam.getUri(), yayiConfig, yayiParam);

            // 请求重排模型接口
            log.info("===>UrlText解析 url:{}  ,  param: {}", urlTextParam.getUri(), JSONUtil.toJsonStr(urlTextParam));
            String body = YayiApiUtils.invokeYaYiApi(builder, urlTextParam);
            log.info("===>UrlText解析 body: {}", body);
            if (StringUtils.isNotBlank(body) && body.contains("QPS访问超出限制")) {
                result.setMsg("QPS访问超出限制");
                result.setRetryFlag(true);
            } else if (StringUtils.isNotBlank(body) && JSONUtil.isTypeJSONObject(body)) {
                JSONObject entries = JSONUtil.parseObj(body);
                result = entries.toBean(UrlTextResult.class);
                result.setRetryFlag(false);
            }

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
    private UrlTextParam urlParam(String url, UrlTextParam param) {
        UrlTextParam urlTextParam = new UrlTextParam();
        urlTextParam.setId(IdUtil.simpleUUID());
        urlTextParam.setUrl(url);
        if (null != url2TextConfig.getIgnoreImages()) {
            urlTextParam.setIgnore_images(url2TextConfig.getIgnoreImages());
        }
        if (null != url2TextConfig.getIgnoreLinks()) {
            urlTextParam.setIgnore_links(url2TextConfig.getIgnoreLinks());
        }
        if (StringUtils.isNotBlank(url2TextConfig.getMode())) {
            urlTextParam.setMode(url2TextConfig.getMode());
        }

        urlTextParam.setRetryTimes(1);
        if (null != url2TextConfig.getRetryTimes()) {
            urlTextParam.setRetryTimes(url2TextConfig.getRetryTimes());
        }
        urlTextParam.setRetryInterval(3000);
        if (null != url2TextConfig.getRetryInterval()) {
            urlTextParam.setRetryInterval(url2TextConfig.getRetryInterval());
        }

        if (StringUtils.isNotBlank(param.getUri())) {
            urlTextParam.setUri(param.getUri());
        }

        if (StringUtils.isBlank(urlTextParam.getUri())) {
            urlTextParam.setUri(url2TextConfig.getUri());
        }

        CopyOptions copyOptions = new CopyOptions();
        copyOptions.ignoreNullValue();
        BeanUtil.copyProperties(param, urlTextParam, copyOptions);
        BeanUtil.copyProperties(urlTextParam, param, copyOptions);
        return urlTextParam;
    }
}
