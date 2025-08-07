package com.wg.appframe.yayi.strategy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wg.appframe.yayi.config.MinimaxConfig;
import com.wg.appframe.yayi.config.MinmaxMusicConfig;
import com.wg.appframe.yayi.param.MinmaxMusicParam;
import com.wg.appframe.yayi.result.MinmaxMusicResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

@Slf4j
public class MinmaxMusicStrategy implements Serializable {

    private static final long serialVersionUID = 5928338604455123561L;

    @Autowired(required = false)
    private MinimaxConfig minimaxConfig;

    @Autowired(required = false)
    private MinmaxMusicConfig minmaxMusicConfig;

    public MinmaxMusicResult run(String content, MinmaxMusicParam param) {
        MinmaxMusicResult result = new MinmaxMusicResult();
        try {
            // 封装接口入参
            param = getParam(content, param);
            log.info("===>minmaxMusic url:{}  ,  param: {}", param.getUri(), JSONUtil.toJsonStr(param));

            try {
                HttpRequest post = HttpUtil.createPost(param.getUri());
                post.header("Authorization", "Bearer " + param.getAppKey());
                post.body(JSONUtil.toJsonStr(param));
                String responses = post.execute().body();
                // log.info("===>minmaxMusic body: {}", responses);
                if (StringUtils.isNotBlank(responses) && JSONUtil.isTypeJSONObject(responses)) {
                    JSONObject entries = JSONUtil.parseObj(responses);
                    return entries.toBean(MinmaxMusicResult.class);
                }
            } catch (Exception e) {
                // log.error("调用minmaxVideo图片算法失败，url = {}", param.getUri(), e);
                throw new RuntimeException("调用minmaxVideo图片算法失败");
            }
            return new MinmaxMusicResult();

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
    private MinmaxMusicParam getParam(String content, MinmaxMusicParam param) {
        MinmaxMusicParam paramTemp = new MinmaxMusicParam();
        paramTemp.setPrompt(content);

        if (StringUtils.isNotBlank(minmaxMusicConfig.getModel())) {
            paramTemp.setModel(minmaxMusicConfig.getModel());
        }
        if (StringUtils.isNotBlank(minmaxMusicConfig.getRefer_voice())) {
            paramTemp.setRefer_voice(minmaxMusicConfig.getRefer_voice());
        }
        if (StringUtils.isNotBlank(minmaxMusicConfig.getRefer_instrumental())) {
            paramTemp.setRefer_instrumental(minmaxMusicConfig.getRefer_instrumental());
        }
        if (StringUtils.isNotBlank(minmaxMusicConfig.getRefer_vocal())) {
            paramTemp.setRefer_vocal(minmaxMusicConfig.getRefer_vocal());
        }

        if (StringUtils.isNotBlank(minmaxMusicConfig.getLyrics())) {
            paramTemp.setLyrics(minmaxMusicConfig.getLyrics());
        }

        paramTemp.setUri(minmaxMusicConfig.getUri());

        paramTemp.setAppKey(minimaxConfig.getApiKey());

        CopyOptions copyOptions = new CopyOptions();
        copyOptions.ignoreNullValue();
        BeanUtil.copyProperties(param, paramTemp, copyOptions);
        BeanUtil.copyProperties(paramTemp, param, copyOptions);
        return paramTemp;
    }

}
