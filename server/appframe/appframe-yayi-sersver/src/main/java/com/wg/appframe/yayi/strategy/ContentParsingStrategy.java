package com.wg.appframe.yayi.strategy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wg.appframe.yayi.config.ContentParsingConfig;
import com.wg.appframe.yayi.config.YayiConfig;
import com.wg.appframe.yayi.entity.YayiMessage;
import com.wg.appframe.yayi.param.ContentParsingParam;
import com.wg.appframe.yayi.param.YayiParam;
import com.wg.appframe.yayi.result.ContentParsingResult;
import com.wg.appframe.yayi.result.YayiResult;
import com.wg.appframe.yayi.utils.YayiApiUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class ContentParsingStrategy implements YayiApiStrategy{


    @Autowired(required = false)
    private ContentParsingConfig contentParsingConfig;

    @Autowired(required = false)
    private YayiConfig yayiConfig;


    public YayiResult run(String fileUrl, YayiParam yayiParam) {
        ContentParsingResult result = new ContentParsingResult();
        if (yayiParam instanceof ContentParsingParam) {
            ContentParsingParam param = (ContentParsingParam) yayiParam;
            result = runContentParsing(fileUrl, param);

            // 判断是否需要重试
            if (null != result && null != result.getRetryFlag() && result.getRetryFlag()) {
                // 重试次数
                int retryTimes = null != yayiParam.getRetryTimes() ? yayiParam.getRetryTimes() : null != contentParsingConfig.getRetryTimes() ? contentParsingConfig.getRetryTimes() : 1;
                int retryInterval = null != yayiParam.getRetryInterval() ? yayiParam.getRetryInterval() : null != contentParsingConfig.getRetryInterval() ? contentParsingConfig.getRetryInterval() : 300;
                for (int i = 0; i < retryTimes; i++) {

                    // 重试等待时间
                    ThreadUtil.safeSleep(retryInterval);
                    log.info("===>runContentParsing 第{}次重试 ,总重试次数:{} ,重试间隔:{}毫秒, error:{}", i + 1, retryTimes, retryInterval, result.getMsg());
                    result = runContentParsing(fileUrl, param);
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
    public ContentParsingResult runContentParsing(String fileUrl, ContentParsingParam yayiParam) {
        ContentParsingResult result = new ContentParsingResult();
        result.setRetryFlag(false);
        try {
            // 创建POST请求
            final Request.Builder builder = new Request.Builder();

            // 设置雅意接口请求的头部参数
            YayiApiUtils.setHeader(builder, contentParsingConfig.getUri(), yayiConfig, yayiParam);

            // 封装文档解析接口入参
            ContentParsingParam contentParsingParam = contentParsingParam(fileUrl, yayiParam);

            // 请求文档解析接口
            result = getResult(builder, contentParsingParam);
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
    private ContentParsingParam contentParsingParam(String fileUrl, ContentParsingParam param) {
        ContentParsingParam contentParsingParam = new ContentParsingParam();
        contentParsingParam.setId(IdUtil.simpleUUID());
        contentParsingParam.setUrl(fileUrl);
        ContentParsingParam.Content content = new ContentParsingParam.Content();
        contentParsingParam.setContent(content);
        if (null != contentParsingConfig.getMode()) {
            content.setMode(contentParsingConfig.getMode());
        }
        if (null != contentParsingConfig.getGetOcr()) {
            content.setGet_ocr(contentParsingConfig.getGetOcr());
        }
        if (null != contentParsingConfig.getWatermark()) {
            content.setWatermark(contentParsingConfig.getWatermark());
        }
        if (null != contentParsingConfig.getXlsParse()) {
            content.setXls_parse(contentParsingConfig.getXlsParse());
        }
        if (StringUtils.isNotBlank(contentParsingConfig.getWebmode())) {
            content.setWebmode(contentParsingConfig.getWebmode());
        }
        if (null != contentParsingConfig.getGetCaption()) {
            content.setGet_caption(contentParsingConfig.getGetCaption());
        }

        contentParsingParam.setRetryTimes(1);
        if (null != contentParsingConfig.getRetryTimes()) {
            contentParsingParam.setRetryTimes(contentParsingConfig.getRetryTimes());
        }
        contentParsingParam.setRetryInterval(3000);
        if (null != contentParsingConfig.getRetryInterval()) {
            contentParsingParam.setRetryInterval(contentParsingConfig.getRetryInterval());
        }

        if (StringUtils.isBlank(contentParsingParam.getUri())) {
            contentParsingParam.setUri(contentParsingConfig.getUri());
        }

        if (StringUtils.isBlank(contentParsingParam.getAppKey())) {
            contentParsingParam.setAppKey(yayiConfig.getAppKey());
        }

        if (StringUtils.isBlank(contentParsingParam.getAppSecret())) {
            contentParsingParam.setAppSecret(yayiConfig.getAppSecret());
        }



        CopyOptions copyOptions = new CopyOptions();
        copyOptions.ignoreNullValue();
        ContentParsingParam.Content content1 = param.getContent();
        if (null != content1) {
            List<YayiMessage> imgprompt = content1.getImgprompt();
            content.setImgprompt(imgprompt);
            BeanUtil.copyProperties(content1, content, copyOptions);
            BeanUtil.copyProperties(content, content1, copyOptions);
        }

        BeanUtil.copyProperties(param, contentParsingParam, copyOptions);
        BeanUtil.copyProperties(contentParsingParam, param, copyOptions);
        return contentParsingParam;
    }

    /**
     * 获取结果
     *
     * @param builder
     * @return
     */
    private ContentParsingResult getResult(Request.Builder builder, YayiParam yayiParam) {
        ContentParsingResult result = new ContentParsingResult();
        log.info("===>runDocument url:{}  ,  param: {}", yayiParam.getUri(), JSONUtil.toJsonStr(yayiParam));
        String body = YayiApiUtils.invokeYaYiApi(builder, yayiParam);
        log.info("===>runDocument body: {}", body);
        if (StringUtils.isNotBlank(body) && body.contains("QPS访问超出限制")) {
            result.setMsg("QPS访问超出限制");
        } else if (StringUtils.isNotBlank(body) && JSONUtil.isTypeJSONObject(body)) {
            JSONObject entries = JSONUtil.parseObj(body);
            result = entries.toBean(ContentParsingResult.class);
        }
        return result;
    }
}
