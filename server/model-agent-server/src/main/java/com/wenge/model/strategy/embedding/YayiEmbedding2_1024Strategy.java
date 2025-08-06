package com.wenge.model.strategy.embedding;

import com.wenge.model.entity.DenseVector;
import com.wenge.model.service.DenseVectorService;
import com.wg.appframe.yayi.api.YayiServer;
import com.wg.appframe.yayi.constants.StringConstans;
import com.wg.appframe.yayi.param.EmbeddingParam;
import com.wg.appframe.yayi.result.EmbeddingResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.wenge.model.constants.AnswerStrategyContant.YAYI_EMBEDDING_2_1024;

/**
 * YAYI-Embedding-2，1024维
 */
@Service(YAYI_EMBEDDING_2_1024)
@Slf4j
public class YayiEmbedding2_1024Strategy implements EmbeddingStrategy {

    @Autowired
    private DenseVectorService denseVectorService;

    @Autowired
    private YayiServer yayiServer;

    @Override
    public List<Double> embedding(String text) {
        DenseVector denseVectorCode = denseVectorService.getDenseVectorCode(YAYI_EMBEDDING_2_1024);
        if (null == denseVectorCode || StringUtils.isBlank(text)) {
            return null;
        }
        EmbeddingParam param = new EmbeddingParam();
        param.setUri(denseVectorCode.getUri());
        EmbeddingResult embedding = yayiServer.embedding(text, param);
        if (StringConstans.CODE.equals(embedding.getCode())) {
            EmbeddingResult.EmbeddingData data = embedding.getData();
            if (null != data) {
                List<List<Double>> embeddingList = data.getEmbeddingList();
                return embeddingList.get(0);
            }
        }
        return null;
    }

}
