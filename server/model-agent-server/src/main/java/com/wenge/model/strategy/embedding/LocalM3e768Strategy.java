package com.wenge.model.strategy.embedding;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.wenge.model.entity.DenseVector;
import com.wenge.model.service.DenseVectorService;
import com.wg.appframe.core.constant.StringConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.wenge.model.constants.AnswerStrategyContant.LOCAL_M3E_768;

/**
 * 企商本地化部署的m3e，768维
 */
@Service(LOCAL_M3E_768)
@Slf4j
public class LocalM3e768Strategy implements EmbeddingStrategy {

    @Autowired
    private DenseVectorService denseVectorService;

    @Override
    public List<Double> embedding(String text) {
        DenseVector denseVectorCode = denseVectorService.getDenseVectorCode(LOCAL_M3E_768);
        if (null == denseVectorCode || StringUtils.isBlank(text)) {
            return null;
        }

        JSONObject param = new JSONObject();
        param.put("query", text);
        String post = StringConstant.BLANK;
        for (int i = 0; i < 3; i++) {
            post = HttpUtil.post(denseVectorCode.getUri(), JSON.toJSONString(param));
            if (StringUtils.isNotBlank(post)) {
                break;
            }
            log.info("====>重试第{}次", i + 2);
        }
        //log.info("modelEncode.result:{}", post);
        if (StringUtils.isNotBlank(post) && JSONUtil.isTypeJSONObject(post)) {
            JSONObject jsonObject = JSON.parseObject(post);
            if (jsonObject.containsKey("dataList")) {
                return jsonObject.getJSONArray("dataList").toList(Double.class);
            } else {
                return jsonObject.getJSONArray("data").toList(Double.class);
            }
        }
        return null;
    }

}
