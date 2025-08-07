package com.wg.appframe.yayi.strategy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wg.appframe.yayi.config.MinimaxConfig;
import com.wg.appframe.yayi.config.MinmaxImageConfig;
import com.wg.appframe.yayi.param.MinmaxImageParam;
import com.wg.appframe.yayi.result.MinmaxImageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

@Slf4j
public class MinmaxImageStrategy implements Serializable {

    private static final long serialVersionUID = 6536664412243190058L;

    @Autowired(required = false)
    private MinimaxConfig minimaxConfig;

    @Autowired(required = false)
    private MinmaxImageConfig minmaxImageConfig;

    public MinmaxImageResult run(String content, MinmaxImageParam param) {
        MinmaxImageResult result = new MinmaxImageResult();
        try {
            // 封装接口入参
            param = getParam(content, param);

            // 请求 minmaxCompletions接口
            log.info("===>minmaxImage url:{}  ,  param: {}", param.getUri(), JSONUtil.toJsonStr(param));

            try {
                HttpRequest post = HttpUtil.createPost(param.getUri());
                post.header("Authorization", "Bearer " + param.getAppKey());
                post.body(JSONUtil.toJsonStr(param));
                String responses = post.execute().body();
                log.info("===>minmaxImage body: {}", responses);
                if (StringUtils.isNotBlank(responses) && JSONUtil.isTypeJSONObject(responses)) {
                    JSONObject entries = JSONUtil.parseObj(responses);
                    return entries.toBean(MinmaxImageResult.class);
                }
            } catch (Exception e) {
                // log.error("调用minmaxImage图片算法失败，url = {}", param.getUri(), e);
                throw new RuntimeException("调用minmaxImage图片算法失败");
            }
            return new MinmaxImageResult();

        } catch (Exception e) {
            e.printStackTrace();
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
    private MinmaxImageParam getParam(String content, MinmaxImageParam param) {
        MinmaxImageParam paramTemp = new MinmaxImageParam();
        paramTemp.setPrompt(content);
        if (StringUtils.isNotBlank(minmaxImageConfig.getModel())) {
            paramTemp.setModel(minmaxImageConfig.getModel());
        }
        if (StringUtils.isNotBlank(minmaxImageConfig.getAspect_ratio())) {
            paramTemp.setAspect_ratio(minmaxImageConfig.getAspect_ratio());
        }
        if (StringUtils.isNotBlank(minmaxImageConfig.getResponse_format())) {
            paramTemp.setResponse_format(minmaxImageConfig.getResponse_format());
        }
        if (null != minmaxImageConfig.getWidth()) {
            paramTemp.setWidth(minmaxImageConfig.getWidth());
        }
        if (null != minmaxImageConfig.getPrompt_optimizer()) {
            paramTemp.setPrompt_optimizer(minmaxImageConfig.getPrompt_optimizer());
        }
        if (null != minmaxImageConfig.getHeight()) {
            paramTemp.setHeight(minmaxImageConfig.getHeight());
        }
        if (null != minmaxImageConfig.getN()) {
            paramTemp.setN(minmaxImageConfig.getN());
        }
        if (null != minmaxImageConfig.getSeed()) {
            paramTemp.setSeed(minmaxImageConfig.getSeed());
        }

        paramTemp.setUri(minmaxImageConfig.getUri());

        paramTemp.setAppKey(minimaxConfig.getApiKey());

        CopyOptions copyOptions = new CopyOptions();
        copyOptions.ignoreNullValue();
        BeanUtil.copyProperties(param, paramTemp, copyOptions);
        BeanUtil.copyProperties(paramTemp, param, copyOptions);
        return paramTemp;
    }

}
