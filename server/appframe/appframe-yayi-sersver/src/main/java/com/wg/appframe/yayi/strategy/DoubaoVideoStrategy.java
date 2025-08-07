package com.wg.appframe.yayi.strategy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wg.appframe.yayi.config.DoubaoConfig;
import com.wg.appframe.yayi.config.DoubaoVideoConfig;
import com.wg.appframe.yayi.param.DoubaoVideoParam;
import com.wg.appframe.yayi.result.DoubaoVideoResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;

@Slf4j
public class DoubaoVideoStrategy implements Serializable {

    private static final long serialVersionUID = -6520218035920843759L;

    @Autowired(required = false)
    private DoubaoConfig doubaoConfig;

    @Autowired(required = false)
    private DoubaoVideoConfig doubaoVideoConfig;

    public DoubaoVideoResult tasks(String id, String apiKey) {
        HttpRequest get = HttpUtil.createGet(doubaoVideoConfig.getUri() + "/" + id);
        if (StringUtils.isBlank(apiKey)) {
            apiKey = doubaoConfig.getApiKey();
        }
        get.header("Authorization", "Bearer " + apiKey);
        String responses = get.execute().body();
        if (StringUtils.isNotBlank(responses) && JSONUtil.isTypeJSONObject(responses)) {
            JSONObject entries = JSONUtil.parseObj(responses);
            return entries.toBean(DoubaoVideoResult.class);
        }
        return new DoubaoVideoResult();
    }

    public DoubaoVideoResult run(String content, DoubaoVideoParam param) {
        DoubaoVideoResult result = new DoubaoVideoResult();
        try {
            // 封装接口入参
            param = getParam(content, param);

            // 请求 doubaoCompletions接口
            log.info("===>doubaoVideo url:{}  ,  param: {}", param.getUri(), JSONUtil.toJsonStr(param));

            try {
                HttpRequest post = HttpUtil.createPost(param.getUri());
                post.header("Authorization", "Bearer " + param.getAppKey());
                post.body(JSONUtil.toJsonStr(param));
                String responses = post.execute().body();
                log.info("===>doubaoVideo body: {}", responses);
                if (StringUtils.isNotBlank(responses) && JSONUtil.isTypeJSONObject(responses)) {
                    JSONObject entries = JSONUtil.parseObj(responses);
                    return entries.toBean(DoubaoVideoResult.class);
                }
            } catch (Exception e) {
                log.error("调用doubaoVideos视频算法失败，url = {}", param.getUri(), e);
                throw new RuntimeException("调用doubaoVideos视频算法失败");
            }
            return new DoubaoVideoResult();

        } catch (Exception e) {
            log.error("error:{}", e.getMessage(), e);
        }
        return result;
    }

    /**
     * 初始化雅意128K接口参数
     * @param content
     * @param param
     * @return
     */
    private DoubaoVideoParam getParam(String content, DoubaoVideoParam param) {
        DoubaoVideoParam paramTemp = new DoubaoVideoParam();
        if (StringUtils.isNotBlank(doubaoVideoConfig.getModel())) {
            paramTemp.setModel(doubaoVideoConfig.getModel());
        }

        if (StringUtils.isNotBlank(doubaoVideoConfig.getCallback_url())) {
            paramTemp.setCallback_url(doubaoVideoConfig.getCallback_url());
        }
        if (StringUtils.isNotBlank(doubaoVideoConfig.getResolution())) {
            paramTemp.setResolution(doubaoVideoConfig.getResolution());
        }

        if (StringUtils.isNotBlank(doubaoVideoConfig.getRatio())) {
            paramTemp.setRatio(doubaoVideoConfig.getRatio());
        }

        if (null != doubaoVideoConfig.getDuration()) {
            paramTemp.setDuration(doubaoVideoConfig.getDuration());
        }
        if (null != doubaoVideoConfig.getFramepersecond()) {
            paramTemp.setFramepersecond(doubaoVideoConfig.getFramepersecond());
        }

        if (null != doubaoVideoConfig.getCamerafixed()) {
            paramTemp.setCamerafixed(doubaoVideoConfig.getCamerafixed());
        }

        if (null != doubaoVideoConfig.getSeed()) {
            paramTemp.setSeed(doubaoVideoConfig.getSeed());
        }
        if (null != doubaoVideoConfig.getWatermark()) {
            paramTemp.setWatermark(doubaoVideoConfig.getWatermark());
        }

        paramTemp.setUri(doubaoVideoConfig.getUri());

        paramTemp.setAppKey(doubaoConfig.getApiKey());

        List<DoubaoVideoParam.Content> contentList = param.getContent();
        CopyOptions copyOptions = new CopyOptions();
        copyOptions.ignoreNullValue();
        BeanUtil.copyProperties(param, paramTemp, copyOptions);

        if (CollectionUtil.isEmpty(contentList)) {
            contentList = ListUtil.toList();
            DoubaoVideoParam.Content contentTemp = new DoubaoVideoParam.Content();
            contentTemp.setType("text");
            content = buildContent(content, paramTemp);
            contentTemp.setText(content);
            contentList.add(contentTemp);
        } else {
            for (DoubaoVideoParam.Content detail : contentList) {
                content = buildContent(detail.getText(), paramTemp);
                detail.setText(content);
            }
        }
        paramTemp.setContent(contentList);
        BeanUtil.copyProperties(paramTemp, param, copyOptions);
        param.setContent(contentList);
        return paramTemp;
    }

    /**
     * 获取参数
     *
     * @param content
     * @param param
     * @return
     */
    private String buildContent(String content, DoubaoVideoParam param) {
        if (StringUtils.isBlank(content)) {
            return content;
        }

        if (!content.contains(" --resolution ") && StringUtils.isNotBlank(param.getResolution())) {
            content = content + " --resolution " + param.getResolution();
        }
        if (!content.contains(" --ratio ") && StringUtils.isNotBlank(param.getRatio())) {
            content = content + " --ratio " + param.getRatio();
        }
        if (!content.contains(" --duration ") && null != param.getDuration()) {
            content = content + " --duration " + param.getDuration();
        }
        if (!content.contains(" --framepersecond ") && null != param.getFramepersecond()) {
            content = content + " --framepersecond " + param.getFramepersecond();
        }
        if (!content.contains(" --watermark ") && null != param.getWatermark()) {
            content = content + " --watermark " + param.getWatermark();
        }
        if (!content.contains(" --camerafixed ") && null != param.getCamerafixed()) {
            content = content + " --camerafixed " + param.getCamerafixed();
        }
        if (!content.contains(" --seed ") && null != param.getSeed()) {
            content = content + " --seed " + param.getSeed();
        }
        return content;
    }

}
