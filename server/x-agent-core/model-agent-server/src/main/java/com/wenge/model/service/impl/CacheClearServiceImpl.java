package com.wenge.model.service.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wenge.model.event.SceneEventListener;
import com.wenge.model.service.CacheClearService;
import com.wenge.oauth.constants.AppConfigContant;
import com.wenge.oauth.dto.param.CacheClearParam;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.config.CoreContextProvider;
import com.wg.appframe.core.dto.params.StringParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CacheClearServiceImpl implements CacheClearService {

    @Override
    public Result cleanWord() {
        SceneEventListener.clear();
        return Result.success();
    }


    @Override
    public Result clearFlowCache(StringParam param) {
        ComponentServiceImpl.clear(param);
        return Result.success();
    }

    @Override
    public Result clearDenseVector() {
        KnowledgeInfoServiceImpl.clear();
        return Result.success();
    }

    @Override
    public Result clearAppConfig(CacheClearParam param) {
        AppConfigContant.clear(param);
        return Result.success();
    }

    /**
     * 清除其他节点的缓存
     * @param api
     * @param param
     */
    public static void clearComCache(String api, Object param) {
        Environment environment = CoreContextProvider.getContext().getEnvironment();
        String cluster = environment.getProperty("agentX.cluster");
        // 不仅要关闭当前节点，还要关闭其他节点
        if (StringUtils.isNotBlank(cluster)) {
            String[] ipArray = cluster.split(",");
            for (String host : ipArray) {
                try {
                    String url = "http://" + host + api;
                    log.info("关闭其他节点连接：{}", url);
                    if (param == null) {
                        param = new JSONObject();
                    }
                    String post = HttpUtil.post(url, JSONUtil.toJsonStr(param));
                    log.info("关闭其他节点连接返回：{}", post);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
