package com.wenge.model.strategy.embedding;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wenge.model.entity.DenseVector;
import com.wenge.model.service.DenseVectorService;
import com.wg.appframe.core.constant.StringConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.wenge.model.constants.AnswerStrategyContant.LOCAL_BGE_768;

/**
 * 企商本地化部署的m3e,768维
 */
@Service(LOCAL_BGE_768)
@Slf4j
public class LocalBge768Strategy implements EmbeddingStrategy {

    @Autowired
    private DenseVectorService denseVectorService;

    @Override
    public List<Double> embedding(String text) {
        DenseVector denseVectorCode = denseVectorService.getDenseVectorCode(LOCAL_BGE_768);
        if (null == denseVectorCode || StringUtils.isBlank(text)) {
            return null;
        }
        JSONObject param = new JSONObject();
        param.set("query", text);
        // log.info("modelEncode.param:{}", JSONUtil.toJsonStr(param));
        String body = StringConstant.BLANK;
        for (int i = 0; i < 3; i++) {
            HttpRequest post = HttpUtil.createPost(denseVectorCode.getUri());
            post.header("Content-Type", "application/json");

            post.body(JSONUtil.toJsonStr(param));
            body = post.execute().body();
            if (StringUtils.isNotBlank(body)) {
                break;
            }
            log.info("====>重试第{}次", i + 2);
        }
        // log.info("modelEncode.result:{}", body);
        if (StringUtils.isNotBlank(body) && JSONUtil.isTypeJSONObject(body)) {
            JSONObject jsonObject = JSONUtil.parseObj(body);
            if (jsonObject.containsKey("dataList")) {
                return jsonObject.getJSONArray("dataList").toList(Double.class);
            } else {
                return jsonObject.getJSONArray("data").toList(Double.class);
            }
        }
        return null;
    }

}
