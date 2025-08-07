package com.wg.appframe.yayi.strategy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wg.appframe.yayi.config.DoubaoConfig;
import com.wg.appframe.yayi.config.DoubaoImageConfig;
import com.wg.appframe.yayi.param.DoubaoImageParam;
import com.wg.appframe.yayi.result.DoubaoImageResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

public class DoubaoImageStrategy implements Serializable {

    private static final long serialVersionUID = 430226217173811832L;

    @Autowired(required = false)
    private DoubaoConfig doubaoConfig;

    @Autowired(required = false)
    private DoubaoImageConfig doubaoImageConfig;

    public DoubaoImageResult run(String content, DoubaoImageParam param) {
        DoubaoImageResult result = new DoubaoImageResult();
        try {
            // 封装接口入参
            param = getParam(content, param);

            // 请求 doubaoCompletions接口
            // log.info("===>doubaoImage url:{}  ,  param: {}", param.getUri(), JSONUtil.toJsonStr(param));

            try {
                HttpRequest post = HttpUtil.createPost(param.getUri());
                post.header("Authorization", "Bearer " + param.getAppKey());
                post.body(JSONUtil.toJsonStr(param));
                String responses = post.execute().body();
                // log.info("===>doubaoImage body: {}", responses);
                if (StringUtils.isNotBlank(responses) && JSONUtil.isTypeJSONObject(responses)) {
                    JSONObject entries = JSONUtil.parseObj(responses);
                    return entries.toBean(DoubaoImageResult.class);
                }
            } catch (Exception e) {
                // log.error("调用doubaoImage图片算法失败，url = {}", param.getUri(), e);
                throw new RuntimeException("调用doubaoImage图片算法失败");
            }
            return new DoubaoImageResult();

        } catch (Exception e) {
            // log.error("error:{}", e.getMessage(), e);
        }
        return result;
    }

    /**
     * 初始化雅意128K接口参数
     * @param content
     * @param param
     * @return
     */
    private DoubaoImageParam getParam(String content, DoubaoImageParam param) {

        DoubaoImageParam paramTemp = new DoubaoImageParam();
        paramTemp.setPrompt(content);

        if (StringUtils.isNotBlank(doubaoImageConfig.getModel())) {
            paramTemp.setModel(doubaoImageConfig.getModel());
        }
        if (null != doubaoImageConfig.getSeed()) {
            paramTemp.setSeed(doubaoImageConfig.getSeed());
        }
        if (null != doubaoImageConfig.getGuidance_scale()) {
            paramTemp.setGuidance_scale(doubaoImageConfig.getGuidance_scale());
        }
        if (null != doubaoImageConfig.getWatermark()) {
            paramTemp.setWatermark(doubaoImageConfig.getWatermark());
        }
        if (StringUtils.isNotBlank(doubaoImageConfig.getResponse_format())) {
            paramTemp.setResponse_format(doubaoImageConfig.getResponse_format());
        }
        if (StringUtils.isNotBlank(doubaoImageConfig.getSize())) {
            paramTemp.setSize(doubaoImageConfig.getSize());
        }

        paramTemp.setUri(doubaoImageConfig.getUri());

        paramTemp.setAppKey(doubaoConfig.getApiKey());

        CopyOptions copyOptions = new CopyOptions();
        copyOptions.ignoreNullValue();
        BeanUtil.copyProperties(param, paramTemp, copyOptions);
        BeanUtil.copyProperties(paramTemp, param, copyOptions);
        return paramTemp;
    }

}
