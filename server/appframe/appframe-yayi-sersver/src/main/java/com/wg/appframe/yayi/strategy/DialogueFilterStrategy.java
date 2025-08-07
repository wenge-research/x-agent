package com.wg.appframe.yayi.strategy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wg.appframe.yayi.config.YayiConfig;
import com.wg.appframe.yayi.config.YayiDialogueFilterConfig;
import com.wg.appframe.yayi.entity.YayiMessage;
import com.wg.appframe.yayi.param.DialogueFilterParam;
import com.wg.appframe.yayi.param.YayiParam;
import com.wg.appframe.yayi.result.DialogueFilterResult;
import com.wg.appframe.yayi.result.YayiResult;
import com.wg.appframe.yayi.utils.YayiApiUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.wg.appframe.yayi.utils.YayiApiUtils.setHeader;

@Slf4j
public class DialogueFilterStrategy implements YayiApiStrategy {

    @Autowired(required = false)
    private YayiDialogueFilterConfig dialogueFilterConfig;

    @Autowired(required = false)
    private YayiConfig yayiConfig;

    @Override
    public YayiResult runMsg(List<YayiMessage> contents, YayiParam yayiParam){
        DialogueFilterResult result = new DialogueFilterResult();
        if (yayiParam instanceof DialogueFilterParam) {
            DialogueFilterParam param = (DialogueFilterParam) yayiParam;
            result = runDialogue(contents, param);

            // 判断是否需要重试
            if (null != result && null != result.getRetryFlag() && result.getRetryFlag()) {
                // 重试次数
                int retryTimes = null != yayiParam.getRetryTimes() ? yayiParam.getRetryTimes() : null != dialogueFilterConfig.getRetryTimes() ? dialogueFilterConfig.getRetryTimes() : 1;
                int retryInterval = null != yayiParam.getRetryInterval() ? yayiParam.getRetryInterval() : null != dialogueFilterConfig.getRetryInterval() ? dialogueFilterConfig.getRetryInterval() : 300;
                for (int i = 0; i < retryTimes; i++) {
                    // 重试等待时间
                    ThreadUtil.safeSleep(retryInterval);
                    log.info("===>runDialogue 第{}次重试 ,总重试次数:{} ,重试间隔:{}毫秒, error:{}", i + 1, retryTimes, retryInterval, result.getMsg());
                    result = runDialogue(contents, param);
                    if (!result.getRetryFlag()) {
                        break;
                    }
                }
            }

        }

        return result;
    }

    /**
     * 调用过滤接口
     * @param contents
     * @param yayiParam
     * @return
     */
    private DialogueFilterResult runDialogue(List<YayiMessage> contents, DialogueFilterParam yayiParam) {
        DialogueFilterResult result = new DialogueFilterResult();
        result.setRetryFlag(false);
        try {
            // 创建POST请求
            final Request.Builder builder = new Request.Builder();

            // 设置雅意接口请求的头部参数
            setHeader(builder, dialogueFilterConfig.getUri(), yayiConfig, yayiParam);

            // 封装30B接口入参
            DialogueFilterParam dialogParam = getDialogParam(contents, yayiParam);

            // 请求雅意filter接口
            log.info("===>dialog filter url:{}  ,  param: {}", dialogueFilterConfig.getUri(), JSONUtil.toJsonStr(dialogParam));
            String body = YayiApiUtils.invokeYaYiApi(builder, dialogParam);
            log.info("===>dialog filter body: {}", body);
            if (StringUtils.isNotBlank(body) && body.contains("QPS访问超出限制")) {
                result.setMsg("QPS访问超出限制");
                result.setRetryFlag(true);
            } else if (StringUtils.isNotBlank(body) && JSONUtil.isTypeJSONObject(body)) {
                JSONObject entries = JSONUtil.parseObj(body);
                result = entries.toBean(DialogueFilterResult.class);
                result.setRetryFlag(false);
            }
        } catch (Exception e) {
            log.error("error:{}", e.getMessage(), e);
        }
        return result;
    }

    /**
     * 初始化雅意30B接口参数
     * @param contents
     * @param yayiParam
     * @return
     */
    private DialogueFilterParam getDialogParam(List<YayiMessage> contents, DialogueFilterParam yayiParam) {
        DialogueFilterParam dialogueFilterParam = new DialogueFilterParam();
        dialogueFilterParam.setId(IdUtil.simpleUUID());
        DialogueFilterParam.Content content = new DialogueFilterParam.Content();
        dialogueFilterParam.setContent(content);
        List<DialogueFilterParam.Convs> convs = ListUtil.toList();
        content.setConvs(convs);
        for (YayiMessage yayiMessage : contents) {
            convs.add(new DialogueFilterParam.Convs(yayiMessage.getRole(), yayiMessage.getContent()));
        }

        if (StringUtils.isNotBlank(dialogueFilterConfig.getPreprocessType())) {
            content.setPreprocess_type(dialogueFilterConfig.getPreprocessType());
        }
        if (null != dialogueFilterConfig.getCosineThresholdValue()) {
            content.setCosine_threshold_value(dialogueFilterConfig.getCosineThresholdValue());
        }
        if (null != dialogueFilterConfig.getDistanceThresholdValue()) {
            content.setDistance_threshold_value(dialogueFilterConfig.getDistanceThresholdValue());
        }
        if (null != dialogueFilterConfig.getN()) {
            content.setN(dialogueFilterConfig.getN());
        }

        dialogueFilterParam.setRetryTimes(1);
        if (null != dialogueFilterConfig.getRetryTimes()) {
            dialogueFilterParam.setRetryTimes(dialogueFilterConfig.getRetryTimes());
        }
        dialogueFilterParam.setRetryInterval(3000);
        if (null != dialogueFilterConfig.getRetryInterval()) {
            dialogueFilterParam.setRetryInterval(dialogueFilterConfig.getRetryInterval());
        }

        if (StringUtils.isBlank(dialogueFilterParam.getUri())) {
            dialogueFilterParam.setUri(dialogueFilterConfig.getUri());
        }

        if (StringUtils.isBlank(dialogueFilterParam.getAppKey())) {
            dialogueFilterParam.setAppKey(yayiConfig.getAppKey());
        }

        if (StringUtils.isBlank(dialogueFilterParam.getAppSecret())) {
            dialogueFilterParam.setAppSecret(yayiConfig.getAppSecret());
        }

        CopyOptions copyOptions = new CopyOptions();
        copyOptions.ignoreNullValue();
        BeanUtil.copyProperties(yayiParam, dialogueFilterParam, copyOptions);
        DialogueFilterParam.Content contentParam = yayiParam.getContent();
        if (null != contentParam) {
            dialogueFilterParam.setContent(contentParam);
            BeanUtil.copyProperties(contentParam, content, copyOptions);
            BeanUtil.copyProperties(content, contentParam, copyOptions);
        }

        BeanUtil.copyProperties(dialogueFilterParam, yayiParam, copyOptions);
        return dialogueFilterParam;
    }

}
