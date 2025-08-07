package com.wg.appframe.yayi.strategy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wg.appframe.yayi.config.YayiConfig;
import com.wg.appframe.yayi.config.YayiContentIndexConfig;
import com.wg.appframe.yayi.param.YayiContentIndexParam;
import com.wg.appframe.yayi.param.YayiParam;
import com.wg.appframe.yayi.result.YayiContentIndexResult;
import com.wg.appframe.yayi.result.YayiResult;
import com.wg.appframe.yayi.utils.YayiApiUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class YayiContentIndexStrategy implements YayiApiStrategy {

    @Autowired(required = false)
    private YayiContentIndexConfig yayiContentIndexConfig;

    @Autowired(required = false)
    private YayiConfig yayiConfig;

    public YayiResult run(YayiParam yayiParam) {
        YayiContentIndexResult result = new YayiContentIndexResult();
        if (yayiParam instanceof YayiContentIndexParam) {
            YayiContentIndexParam param = (YayiContentIndexParam) yayiParam;
            result = runIndexContent(param);

            // 判断是否需要重试
            if (null != result && null != result.getRetryFlag() && result.getRetryFlag()) {
                // 重试次数
                int retryTimes = null != yayiParam.getRetryTimes() ? yayiParam.getRetryTimes() : null != yayiContentIndexConfig.getRetryTimes() ? yayiContentIndexConfig.getRetryTimes() : 1;
                int retryInterval = null != yayiParam.getRetryInterval() ? yayiParam.getRetryInterval() : null != yayiContentIndexConfig.getRetryInterval() ? yayiContentIndexConfig.getRetryInterval() : 300;
                for (int i = 0; i < retryTimes; i++) {
                    // 重试等待时间
                    ThreadUtil.safeSleep(retryInterval);
                    log.info("===>yayiPluginSelectionModelStrategy 第{}次重试 ,总重试次数:{} ,重试间隔:{}毫秒, error:{}", i + 1, retryTimes, retryInterval, result.getMsg());
                    result = runIndexContent(param);
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
    public YayiContentIndexResult runIndexContent(YayiContentIndexParam yayiParam) {
        YayiContentIndexResult result = new YayiContentIndexResult();
        result.setRetryFlag(false);
        if (null == yayiParam.getContent()||StringUtils.isBlank(yayiParam.getContent().getText())|| CollectionUtil.isEmpty(yayiParam.getContent().getDoc_list())) {
            log.error("===>contentIndexParam 添加索引 参数错误");
            return result;
        }
        try {
            // 创建POST请求
            final Request.Builder builder = new Request.Builder();

            // 封装插件选择模型接口入参
            YayiContentIndexParam modelParam = indexContentParam(yayiParam);

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
    private YayiContentIndexParam indexContentParam(YayiContentIndexParam param) {
        YayiContentIndexParam modelParam = new YayiContentIndexParam();
        modelParam.setId(IdUtil.simpleUUID());
        YayiContentIndexParam.Content content = new YayiContentIndexParam.Content();
        modelParam.setContent(content);
        if (StringUtils.isNotBlank(yayiContentIndexConfig.getMode())) {
            content.setMode(yayiContentIndexConfig.getMode());
        }

        modelParam.setRetryTimes(1);
        if (null != yayiContentIndexConfig.getRetryTimes()) {
            modelParam.setRetryTimes(yayiContentIndexConfig.getRetryTimes());
        }
        modelParam.setRetryInterval(3000);
        if (null != yayiContentIndexConfig.getRetryInterval()) {
            modelParam.setRetryInterval(yayiContentIndexConfig.getRetryInterval());
        }

        if (StringUtils.isBlank(modelParam.getUri())) {
            modelParam.setUri(yayiContentIndexConfig.getUri());
        }

        YayiContentIndexParam.Content content1 = param.getContent();
        String text = content1.getText();
        content.setText(text);

        List<YayiContentIndexParam.Doc> docList = content1.getDoc_list();
        content.setDoc_list(docList);

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
    private YayiContentIndexResult getResult(Request.Builder builder, YayiParam yayiParam) {
        YayiContentIndexResult result = new YayiContentIndexResult();
        log.info("===>contentIndexParam 添加索引 url:{}  ,  param: {}", yayiParam.getUri(), JSONUtil.toJsonStr(yayiParam));
        String body = YayiApiUtils.invokeYaYiApi(builder, yayiParam);
        log.info("===>contentIndexParam 添加索引 body: {}", body);
        if (StringUtils.isNotBlank(body) && body.contains("QPS访问超出限制")) {
            result.setMsg("QPS访问超出限制");
        } else if (StringUtils.isNotBlank(body) && JSONUtil.isTypeJSONObject(body)) {
            JSONObject entries = JSONUtil.parseObj(body);
            result = entries.toBean(YayiContentIndexResult.class);
        }
        return result;
    }
}
