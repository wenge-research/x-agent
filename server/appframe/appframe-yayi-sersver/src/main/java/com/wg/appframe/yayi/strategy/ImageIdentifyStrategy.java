package com.wg.appframe.yayi.strategy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wg.appframe.yayi.config.ImageIdentifyConfig;
import com.wg.appframe.yayi.config.YayiConfig;
import com.wg.appframe.yayi.entity.YayiMessage;
import com.wg.appframe.yayi.param.ImageIdentifyParam;
import com.wg.appframe.yayi.param.YayiParam;
import com.wg.appframe.yayi.result.ImageIdentifyResult;
import com.wg.appframe.yayi.result.YayiResult;
import com.wg.appframe.yayi.utils.YayiApiUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 图片识别
 *
 */

@Slf4j
public class ImageIdentifyStrategy implements YayiApiStrategy{


    @Autowired(required = false)
    private ImageIdentifyConfig imageIdentifyConfig;

    @Autowired(required = false)
    private YayiConfig yayiConfig;


    public YayiResult run(String imageUrl, YayiParam yayiParam) {
        ImageIdentifyResult result = new ImageIdentifyResult();
        if (yayiParam instanceof ImageIdentifyParam) {
            ImageIdentifyParam param = (ImageIdentifyParam) yayiParam;
            result = runImageIdentify(imageUrl, param);

            // 判断是否需要重试
            if (null != result && null != result.getRetryFlag() && result.getRetryFlag()) {
                // 重试次数
                int retryTimes = null != yayiParam.getRetryTimes() ? yayiParam.getRetryTimes() : null != imageIdentifyConfig.getRetryTimes() ? imageIdentifyConfig.getRetryTimes() : 1;
                int retryInterval = null != yayiParam.getRetryInterval() ? yayiParam.getRetryInterval() : null != imageIdentifyConfig.getRetryInterval() ? imageIdentifyConfig.getRetryInterval() : 300;
                for (int i = 0; i < retryTimes; i++) {

                    // 重试等待时间
                    ThreadUtil.safeSleep(retryInterval);
                    log.info("===>runImageIdentify 第{}次重试 ,总重试次数:{} ,重试间隔:{}毫秒, error:{}", i + 1, retryTimes, retryInterval, result.getMsg());
                    result = runImageIdentify(imageUrl, param);
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
     * @param imageUrl
     * @param yayiParam
     * @return
     */
    public ImageIdentifyResult runImageIdentify(String imageUrl, ImageIdentifyParam yayiParam) {
        ImageIdentifyResult result = new ImageIdentifyResult();
        result.setRetryFlag(false);
        try {
            // 创建POST请求
            final Request.Builder builder = new Request.Builder();

            // 设置雅意接口请求的头部参数
            YayiApiUtils.setHeader(builder, imageIdentifyConfig.getUri(), yayiConfig, yayiParam);

            // 封装文档解析接口入参
            ImageIdentifyParam imageIdentifyParam = imageIdentifyParam(imageUrl, yayiParam);

            // 请求文档解析接口
            result = getResult(builder, imageIdentifyParam);
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
    private ImageIdentifyParam imageIdentifyParam(String imageUrl, ImageIdentifyParam param) {
        ImageIdentifyParam imageIdentifyParam = new ImageIdentifyParam();
        imageIdentifyParam.setId(IdUtil.simpleUUID());
        imageIdentifyParam.setUrl(imageUrl);
        ImageIdentifyParam.Content content = new ImageIdentifyParam.Content();
        imageIdentifyParam.setContent(content);
        if (null != imageIdentifyConfig.getGetOcr()) {
            content.setGet_ocr(imageIdentifyConfig.getGetOcr());
        }
        if (null != imageIdentifyConfig.getGetCaption()) {
            content.setGet_caption(imageIdentifyConfig.getGetCaption());
        }

        imageIdentifyParam.setRetryTimes(1);
        if (null != imageIdentifyConfig.getRetryTimes()) {
            imageIdentifyParam.setRetryTimes(imageIdentifyConfig.getRetryTimes());
        }
        imageIdentifyParam.setRetryInterval(3000);
        if (null != imageIdentifyConfig.getRetryInterval()) {
            imageIdentifyParam.setRetryInterval(imageIdentifyConfig.getRetryInterval());
        }

        if (StringUtils.isBlank(imageIdentifyParam.getUri())) {
            imageIdentifyParam.setUri(imageIdentifyConfig.getUri());
        }

        if (StringUtils.isBlank(imageIdentifyParam.getAppKey())) {
            imageIdentifyParam.setAppKey(yayiConfig.getAppKey());
        }

        if (StringUtils.isBlank(imageIdentifyParam.getAppSecret())) {
            imageIdentifyParam.setAppSecret(yayiConfig.getAppSecret());
        }

        CopyOptions copyOptions = new CopyOptions();
        copyOptions.ignoreNullValue();
        ImageIdentifyParam.Content content1 = param.getContent();
        if (null != content1) {
            List<YayiMessage> imgprompt = content1.getImgprompt();
            content.setImgprompt(imgprompt);
            BeanUtil.copyProperties(content1, content, copyOptions);
            BeanUtil.copyProperties(content, content1, copyOptions);
        }

        BeanUtil.copyProperties(param, imageIdentifyParam, copyOptions);
        BeanUtil.copyProperties(imageIdentifyParam, param, copyOptions);
        return imageIdentifyParam;
    }

    /**
     * 获取结果
     *
     * @param builder
     * @return
     */
    private ImageIdentifyResult getResult(Request.Builder builder, YayiParam yayiParam) {
        ImageIdentifyResult result = new ImageIdentifyResult();
        log.info("===>runImage url:{}  ,  param: {}", yayiParam.getUri(), JSONUtil.toJsonStr(yayiParam));
        String body = YayiApiUtils.invokeYaYiApi(builder, yayiParam);
        log.info("===>runImage body: {}", body);
        if (StringUtils.isNotBlank(body) && body.contains("QPS访问超出限制")) {
            result.setMsg("QPS访问超出限制");
        } else if (StringUtils.isNotBlank(body) && JSONUtil.isTypeJSONObject(body)) {
            JSONObject entries = JSONUtil.parseObj(body);
            result = entries.toBean(ImageIdentifyResult.class);
        }
        return result;
    }
}
