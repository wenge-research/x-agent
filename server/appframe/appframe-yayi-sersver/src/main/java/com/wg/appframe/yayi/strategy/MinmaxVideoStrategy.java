package com.wg.appframe.yayi.strategy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wg.appframe.yayi.config.MinimaxConfig;
import com.wg.appframe.yayi.config.MinmaxVideoConfig;
import com.wg.appframe.yayi.param.MinmaxVideoParam;
import com.wg.appframe.yayi.result.MinmaxFileResult;
import com.wg.appframe.yayi.result.MinmaxVideoResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

@Slf4j
public class MinmaxVideoStrategy implements Serializable {

    private static final long serialVersionUID = 6536664412243190058L;

    @Autowired(required = false)
    private MinimaxConfig minimaxConfig;

    @Autowired(required = false)
    private MinmaxVideoConfig minmaxVideoConfig;

    public MinmaxVideoResult tasks(String id, String apikey) {
        HttpRequest get = HttpUtil.createGet(minmaxVideoConfig.getSearchTaskUri() + "?task_id=" + id);
        if (StringUtils.isBlank(apikey)) {
            apikey = minimaxConfig.getApiKey();
        }
        get.header("Authorization", "Bearer " + apikey);
        String responses = get.execute().body();
        if (StringUtils.isNotBlank(responses) && JSONUtil.isTypeJSONObject(responses)) {
            JSONObject entries = JSONUtil.parseObj(responses);
            return entries.toBean(MinmaxVideoResult.class);
        }
        return new MinmaxVideoResult();
    }

    public MinmaxFileResult download(String fileId, String apikey) {
        if (StringUtils.isNotBlank(fileId)) {
            HttpRequest download = HttpUtil.createGet(minmaxVideoConfig.getDownloadFileUri() + "?file_id=" + fileId);
            download.header("Authorization", "Bearer " + apikey);
            String responses = download.execute().body();
            if (StringUtils.isNotBlank(responses) && JSONUtil.isTypeJSONObject(responses)) {
                JSONObject entries = JSONUtil.parseObj(responses);
                return entries.toBean(MinmaxFileResult.class);
            }
        }
        return new MinmaxFileResult();
    }

    public MinmaxVideoResult run(String content, MinmaxVideoParam param) {
        MinmaxVideoResult result = new MinmaxVideoResult();
        try {
            // 封装接口入参
            param = getParam(content, param);
            log.info("===>minmaxVideo url:{}  ,  param: {}", param.getUri(), JSONUtil.toJsonStr(param));

            try {
                HttpRequest post = HttpUtil.createPost(param.getUri());
                post.header("Authorization", "Bearer " + param.getAppKey());
                post.body(JSONUtil.toJsonStr(param));
                String responses = post.execute().body();
                log.info("===>minmaxVideo body: {}", responses);
                if (StringUtils.isNotBlank(responses) && JSONUtil.isTypeJSONObject(responses)) {
                    JSONObject entries = JSONUtil.parseObj(responses);
                    return entries.toBean(MinmaxVideoResult.class);
                }
            } catch (Exception e) {
                // log.error("调用minmaxVideo图片算法失败，url = {}", param.getUri(), e);
                throw new RuntimeException("调用minmaxVideo图片算法失败");
            }
            return new MinmaxVideoResult();

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
    private MinmaxVideoParam getParam(String content, MinmaxVideoParam param) {
        MinmaxVideoParam paramTemp = new MinmaxVideoParam();
        paramTemp.setPrompt(content);
        if (StringUtils.isNotBlank(minmaxVideoConfig.getModel())) {
            paramTemp.setModel(minmaxVideoConfig.getModel());
        }
        if (StringUtils.isNotBlank(minmaxVideoConfig.getCallback_url())) {
            paramTemp.setCallback_url(minmaxVideoConfig.getCallback_url());
        }
        if (null != minmaxVideoConfig.getPrompt_optimizer()) {
            paramTemp.setPrompt_optimizer(minmaxVideoConfig.getPrompt_optimizer());
        }
        if (null != minmaxVideoConfig.getDuration()) {
            paramTemp.setDuration(minmaxVideoConfig.getDuration());
        }

        if (null != minmaxVideoConfig.getResolution()) {
            paramTemp.setResolution(minmaxVideoConfig.getResolution());
        }
        if (StringUtils.isNotBlank(minmaxVideoConfig.getFirst_frame_image())) {
            paramTemp.setFirst_frame_image(minmaxVideoConfig.getFirst_frame_image());
        }


        paramTemp.setUri(minmaxVideoConfig.getUri());

        paramTemp.setAppKey(minimaxConfig.getApiKey());

        CopyOptions copyOptions = new CopyOptions();
        copyOptions.ignoreNullValue();
        BeanUtil.copyProperties(param, paramTemp, copyOptions);
        BeanUtil.copyProperties(paramTemp, param, copyOptions);
        return paramTemp;
    }

}
