package com.wg.appframe.yayi.strategy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wg.appframe.yayi.config.WebPromptConfig;
import com.wg.appframe.yayi.config.YayiConfig;
import com.wg.appframe.yayi.param.PromptWebParam;
import com.wg.appframe.yayi.param.YayiParam;
import com.wg.appframe.yayi.result.PromptWebResult;
import com.wg.appframe.yayi.result.YayiResult;
import com.wg.appframe.yayi.utils.YayiApiUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class WebPromptStrategy implements YayiApiStrategy {

    @Autowired(required = false)
    private YayiConfig yayiConfig;

    @Autowired(required = false)
    private WebPromptConfig webPromptConfig;

    @Override
    public YayiResult run(String content, YayiParam yayiParam) {
        YayiResult result = new YayiResult();
        if (yayiParam instanceof PromptWebParam) {
            PromptWebParam param = (PromptWebParam) yayiParam;
            result = runPromptWeb(content, param);

            // 判断是否需要重试
            if (null != result && null != result.getRetryFlag() && result.getRetryFlag()) {
                // 重试次数
                int retryTimes = null != yayiParam.getRetryTimes() ? yayiParam.getRetryTimes() : null != webPromptConfig.getRetryTimes() ? webPromptConfig.getRetryTimes() : 1;
                int retryInterval = null != yayiParam.getRetryInterval() ? yayiParam.getRetryInterval() : null != webPromptConfig.getRetryInterval() ? webPromptConfig.getRetryInterval() : 300;
                for (int i = 0; i < retryTimes; i++) {

                    // 重试等待时间
                    ThreadUtil.safeSleep(retryInterval);
                    log.info("===>runPromptWeb 第{}次重试 ,总重试次数:{} ,重试间隔:{}毫秒, error:{}", i + 1, retryTimes, retryInterval, result.getMsg());
                    result = runPromptWeb(content, param);
                    if (!result.getRetryFlag()) {
                        break;
                    }
                }
            }

        }
        return result;
    }

    private PromptWebResult runPromptWeb(String userMessage, PromptWebParam param) {
        PromptWebResult result = new PromptWebResult();
        try {
            // 创建POST请求
            final Request.Builder builder = new Request.Builder();

            // 封装【YAYI-搜索】接口入参
            PromptWebParam promptWebParam = getPromptWebParam(userMessage, param);

            // 设置雅意【YAYI-搜索】接口请求的头部参数
            YayiApiUtils.setHeader(builder, webPromptConfig.getUri(), yayiConfig, param);

            // 请求雅意【YAYI-搜索】接口
            log.info("===>promptWeb url:{}  ,  param: {}", webPromptConfig.getUri(), JSONUtil.toJsonStr(promptWebParam));
            String body = YayiApiUtils.invokeYaYiApi(builder, promptWebParam);
            log.info("===>promptWeb body: {}", body);

            if (StringUtils.isNotBlank(body) && JSONUtil.isTypeJSONObject(body)) {
                if (body.contains("QPS访问超出限制")) {
                    result.setMsg("QPS访问超出限制");
                    result.setRetryFlag(true);
                    return result;
                }

                JSONObject entries = JSONUtil.parseObj(body);
                result = entries.toBean(PromptWebResult.class);
                result.setRetryFlag(false);

                JSONObject data = entries.getJSONObject("data");
                if (null != data) {
                    PromptWebResult.Data dataDetail = result.getData();
                    if (null == dataDetail) {
                        dataDetail = new PromptWebResult.Data();
                        result.setData(dataDetail);
                    }
                    List<PromptWebResult.ResInfoDetail> resInfoDetailList = ListUtil.toList();
                    dataDetail.setResInfo(resInfoDetailList);

                    JSONObject resInfo = data.getJSONObject("res_info");
                    result.setResInfoDetailList(resInfoDetailList);
                    resInfo.forEach((index, detail) -> {
                        JSONObject detailObj = (JSONObject) detail;
                        PromptWebResult.ResInfoDetail resInfoDetails = new PromptWebResult.ResInfoDetail();
                        resInfoDetails.setContext(detailObj.getStr("context"));
                        resInfoDetails.setAbstracts(detailObj.getStr("abstract"));
                        resInfoDetails.setAbstracts(detailObj.getStr("description"));
                        resInfoDetails.setPubtime(detailObj.getStr("pubtime"));
                        resInfoDetails.setIndex(index);
                        resInfoDetails.setTitle(detailObj.getStr("title"));
                        resInfoDetails.setUrl(detailObj.getStr("url"));
                        resInfoDetailList.add(resInfoDetails);
                    });
                }

            }
        } catch (Exception e) {
            log.error("error:{}", e.getMessage(), e);
        }
        return result;
    }

    /**
     * 初始化PromptWeb参数
     *
     * @param userMessage
     * @param param
     * @return
     */
    private PromptWebParam getPromptWebParam(String userMessage, PromptWebParam param) {
        PromptWebParam promptWebParam = new PromptWebParam();
        promptWebParam.setId(IdUtil.simpleUUID());
        PromptWebParam.Content content = new PromptWebParam.Content();
        promptWebParam.setContent(content);
        content.setUser_message(userMessage);
        if (StringUtils.isNotBlank(webPromptConfig.getFunction())) {
            content.setFunction(webPromptConfig.getFunction());
        }
        if (StringUtils.isNotBlank(webPromptConfig.getUserId())) {
            content.setUser_id(webPromptConfig.getUserId());
        }
        if (null != webPromptConfig.getPromptMaxTokens()) {
            content.setPrompt_max_tokens(webPromptConfig.getPromptMaxTokens());
        }
        if (null != webPromptConfig.getGetNewsNum()) {
            content.setGet_news_num(webPromptConfig.getGetNewsNum());
        }
        if (null != webPromptConfig.getTopK()) {
            content.setTop_k(webPromptConfig.getTopK());
        }

        if (CollectionUtil.isNotEmpty(webPromptConfig.getWebSourceList())) {
            content.setWeb_source_list(webPromptConfig.getWebSourceList());
        }

        if (null != webPromptConfig.getTimeLimit()) {
            content.setTime_limit(webPromptConfig.getTimeLimit());
        }

        if (StringUtils.isNotBlank(webPromptConfig.getDataType())) {
            content.setData_type(webPromptConfig.getDataType());
        }

        if (null != webPromptConfig.getSortLabel()) {
            content.setSort_label(webPromptConfig.getSortLabel());
        }

        if (null != webPromptConfig.getGenMindMap()) {
            content.setGen_mind_map(webPromptConfig.getGenMindMap());
        }
        if (null != webPromptConfig.getGenMaxTokens()) {
            content.setGen_max_tokens(webPromptConfig.getGenMaxTokens());
        }

        if (StringUtils.isBlank(param.getAppKey())) {
            param.setAppKey(yayiConfig.getAppKey());
        }
        if (StringUtils.isBlank(param.getAppSecret())) {
            param.setAppSecret(yayiConfig.getAppSecret());
        }
        if (StringUtils.isBlank(param.getUri())) {
            param.setUri(webPromptConfig.getUri());
        }
        param.setRetryTimes(1);
        param.setRetryInterval(3000);
        if (null == param.getRetryInterval()) {
            param.setRetryInterval(webPromptConfig.getRetryInterval());
        }

        if (null == param.getRetryTimes()) {
            param.setRetryTimes(webPromptConfig.getRetryTimes());
        }

        CopyOptions copyOptions = new CopyOptions();
        copyOptions.ignoreNullValue();
        BeanUtil.copyProperties(param, promptWebParam, copyOptions);

        PromptWebParam.Content content1 = param.getContent();
        if (null != content1) {
            BeanUtil.copyProperties(content1, content, copyOptions);
            promptWebParam.setContent(content);
        }
        BeanUtil.copyProperties(promptWebParam, param, copyOptions);
        BeanUtil.copyProperties(param, promptWebParam, copyOptions);
        return promptWebParam;
    }

}
