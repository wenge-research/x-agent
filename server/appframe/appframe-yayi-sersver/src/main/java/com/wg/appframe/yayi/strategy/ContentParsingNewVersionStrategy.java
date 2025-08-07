package com.wg.appframe.yayi.strategy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wg.appframe.yayi.config.ContentParsingNewVersionConfig;
import com.wg.appframe.yayi.config.YayiConfig;
import com.wg.appframe.yayi.entity.YayiMessage;
import com.wg.appframe.yayi.param.ContentParsingNewVersionParam;
import com.wg.appframe.yayi.param.YayiParam;
import com.wg.appframe.yayi.result.ContentParsingNewVersionResult;
import com.wg.appframe.yayi.result.YayiResult;
import com.wg.appframe.yayi.utils.YayiApiUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 内容解析
 *
 * 文档+网页+图像(包含图表) 的智能解析
 * 文档：具备包含docx、doc、pptx、ppt、pdf、txt、csv、xlsx、xls、markdown、wps、ofd格式的文档解析能力
 * 图像：具备包含jpg png jpeg bmp tiff webp wmf webp格式的图像解析能力
 * 音频：具备包含mp3, ogg, aac, flac, wav的中文语音识别能力
 */

@Slf4j
public class ContentParsingNewVersionStrategy implements YayiApiStrategy{


    @Autowired(required = false)
    private ContentParsingNewVersionConfig contentParsingNewVersionConfig;

    @Autowired(required = false)
    private YayiConfig yayiConfig;


    public YayiResult run(String fileUrl, YayiParam yayiParam) {
        ContentParsingNewVersionResult result = new ContentParsingNewVersionResult();
        if (yayiParam instanceof ContentParsingNewVersionParam) {
            ContentParsingNewVersionParam param = (ContentParsingNewVersionParam) yayiParam;
            result = runContentParsing(fileUrl, param);

            // 判断是否需要重试
            if (null != result && null != result.getRetryFlag() && result.getRetryFlag()) {
                // 重试次数
                int retryTimes = null != yayiParam.getRetryTimes() ? yayiParam.getRetryTimes() : null != contentParsingNewVersionConfig.getRetryTimes() ? contentParsingNewVersionConfig.getRetryTimes() : 1;
                int retryInterval = null != yayiParam.getRetryInterval() ? yayiParam.getRetryInterval() : null != contentParsingNewVersionConfig.getRetryInterval() ? contentParsingNewVersionConfig.getRetryInterval() : 300;
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
    public ContentParsingNewVersionResult runContentParsing(String fileUrl, ContentParsingNewVersionParam yayiParam) {
        ContentParsingNewVersionResult result = new ContentParsingNewVersionResult();
        result.setRetryFlag(false);
        try {
            // 创建POST请求
            final Request.Builder builder = new Request.Builder();

            // 设置雅意接口请求的头部参数
            YayiApiUtils.setHeader(builder, contentParsingNewVersionConfig.getUri(), yayiConfig, yayiParam);

            // 封装文档解析接口入参
            ContentParsingNewVersionParam contentParsingNewVersionParam = contentParsingNewVersionParam(fileUrl, yayiParam);

            // 请求文档解析接口
            result = getResult(builder, contentParsingNewVersionParam);
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
    private ContentParsingNewVersionParam contentParsingNewVersionParam(String fileUrl, ContentParsingNewVersionParam param) {
        ContentParsingNewVersionParam contentParsingNewVersionParam = new ContentParsingNewVersionParam();
        contentParsingNewVersionParam.setId(IdUtil.simpleUUID());
        contentParsingNewVersionParam.setUrl(fileUrl);
        ContentParsingNewVersionParam.Content content = new ContentParsingNewVersionParam.Content();
        contentParsingNewVersionParam.setContent(content);
        if (null != contentParsingNewVersionConfig.getMode()) {
            content.setMode(contentParsingNewVersionConfig.getMode());
        }
        if (null != contentParsingNewVersionConfig.getPdfSplitPagenum()) {
            content.setPdf_split_pagenum(contentParsingNewVersionConfig.getPdfSplitPagenum());
        }
        if (null != contentParsingNewVersionConfig.getGetOcr()) {
            content.setGet_ocr(contentParsingNewVersionConfig.getGetOcr());
        }
        if (null != contentParsingNewVersionConfig.getWatermark()) {
            content.setWatermark(contentParsingNewVersionConfig.getWatermark());
        }
        if (null != contentParsingNewVersionConfig.getXlsParse()) {
            content.setXls_parse(contentParsingNewVersionConfig.getXlsParse());
        }
        if (StringUtils.isNotBlank(contentParsingNewVersionConfig.getWebmode())) {
            content.setWebmode(contentParsingNewVersionConfig.getWebmode());
        }
        if (null != contentParsingNewVersionConfig.getGetCaption()) {
            content.setGet_caption(contentParsingNewVersionConfig.getGetCaption());
        }

        contentParsingNewVersionParam.setRetryTimes(1);
        if (null != contentParsingNewVersionConfig.getRetryTimes()) {
            contentParsingNewVersionParam.setRetryTimes(contentParsingNewVersionConfig.getRetryTimes());
        }
        contentParsingNewVersionParam.setRetryInterval(3000);
        if (null != contentParsingNewVersionConfig.getRetryInterval()) {
            contentParsingNewVersionParam.setRetryInterval(contentParsingNewVersionConfig.getRetryInterval());
        }

        if (StringUtils.isBlank(contentParsingNewVersionParam.getUri())) {
            contentParsingNewVersionParam.setUri(contentParsingNewVersionConfig.getUri());
        }

        if (StringUtils.isBlank(contentParsingNewVersionParam.getAppKey())) {
            contentParsingNewVersionParam.setAppKey(yayiConfig.getAppKey());
        }

        if (StringUtils.isBlank(contentParsingNewVersionParam.getAppSecret())) {
            contentParsingNewVersionParam.setAppSecret(yayiConfig.getAppSecret());
        }



        CopyOptions copyOptions = new CopyOptions();
        copyOptions.ignoreNullValue();
        ContentParsingNewVersionParam.Content content1 = param.getContent();
        if (null != content1) {
            List<YayiMessage> imgprompt = content1.getImgprompt();
            content.setImgprompt(imgprompt);
            BeanUtil.copyProperties(content1, content, copyOptions);
            BeanUtil.copyProperties(content, content1, copyOptions);
        }

        BeanUtil.copyProperties(param, contentParsingNewVersionParam, copyOptions);
        BeanUtil.copyProperties(contentParsingNewVersionParam, param, copyOptions);
        return contentParsingNewVersionParam;
    }

    /**
     * 获取结果
     *
     * @param builder
     * @return
     */
    private ContentParsingNewVersionResult getResult(Request.Builder builder, YayiParam yayiParam) {
        ContentParsingNewVersionResult result = new ContentParsingNewVersionResult();
        log.info("===>runDocument url:{}  ,  param: {}", yayiParam.getUri(), JSONUtil.toJsonStr(yayiParam));
        String body = YayiApiUtils.invokeYaYiApi(builder, yayiParam);
        log.info("===>runDocument body: {}", body);
        if (StringUtils.isNotBlank(body) && body.contains("QPS访问超出限制")) {
            result.setMsg("QPS访问超出限制");
        } else if (StringUtils.isNotBlank(body) && JSONUtil.isTypeJSONObject(body)) {
            JSONObject entries = JSONUtil.parseObj(body);
            result = entries.toBean(ContentParsingNewVersionResult.class);
        }
        return result;
    }
}
