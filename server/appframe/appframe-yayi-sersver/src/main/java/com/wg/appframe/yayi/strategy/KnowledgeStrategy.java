package com.wg.appframe.yayi.strategy;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wg.appframe.yayi.config.KnowledgeConfig;
import com.wg.appframe.yayi.config.KnowledgeSplitConfig;
import com.wg.appframe.yayi.config.YayiConfig;
import com.wg.appframe.yayi.param.KnowledgeParam;
import com.wg.appframe.yayi.param.YayiParam;
import com.wg.appframe.yayi.result.KnowledgeResult;
import com.wg.appframe.yayi.result.YayiResult;
import com.wg.appframe.yayi.utils.YayiApiUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class KnowledgeStrategy implements YayiApiStrategy{


    @Autowired(required = false)
    private KnowledgeConfig knowledgeConfig;

    @Autowired(required = false)
    private KnowledgeSplitConfig knowledgeSplitConfig;

    @Autowired(required = false)
    private YayiConfig yayiConfig;

    public YayiResult run(YayiParam yayiParam) {
        KnowledgeResult result = new KnowledgeResult();
        if (yayiParam instanceof KnowledgeParam) {
            KnowledgeParam param = (KnowledgeParam) yayiParam;
            result = runKnowledge(param);

            // 判断是否需要重试
            if (null != result && null != result.getRetryFlag() && result.getRetryFlag()) {
                // 重试次数
                int retryTimes = null != yayiParam.getRetryTimes() ? yayiParam.getRetryTimes() : null != knowledgeSplitConfig.getRetryTimes() ? knowledgeSplitConfig.getRetryTimes() : 1;
                int retryInterval = null != yayiParam.getRetryInterval() ? yayiParam.getRetryInterval() : null != knowledgeSplitConfig.getRetryInterval() ? knowledgeSplitConfig.getRetryInterval() : 300;
                for (int i = 0; i < retryTimes; i++) {
                    // 重试等待时间
                    ThreadUtil.safeSleep(retryInterval);
                    log.info("===>runKnowledge 第{}次重试 ,总重试次数:{} ,重试间隔:{}毫秒, error:{}", i + 1, retryTimes, retryInterval, result.getMsg());
                    result = runKnowledge(param);
                    if (!result.getRetryFlag()) {
                        break;
                    }
                }
            }

        }

        return result;
    }

    /**
     * 调用知识库接口
     * @param yayiParam
     * @return
     */
    public KnowledgeResult runKnowledge(KnowledgeParam yayiParam) {
        KnowledgeResult result = new KnowledgeResult();
        result.setRetryFlag(false);
        try {
            // 创建POST请求
            final Request.Builder builder = new Request.Builder();

            // 封装重排模型接口入参
            KnowledgeParam knowledgeParam = knowledgeParam(yayiParam);

            // 设置雅意接口请求的头部参数
            YayiApiUtils.setHeader(builder, knowledgeParam.getUri(), yayiConfig, yayiParam);

            // 请求重排模型接口
            result = getResult(builder, knowledgeParam);
            result.setRetryFlag(result.getMsg().contains("QPS"));

        } catch (Exception e) {
            log.error("error:{}", e.getMessage(), e);
        }

        return result;
    }

    /**
     * 初始化重排模型接口参数
     *
     * @param knowledgeParam
     * @return
     */
    private KnowledgeParam knowledgeParam(KnowledgeParam knowledgeParam) {
        //KnowledgeParam knowledgeParam = new KnowledgeParam();
        knowledgeParam.setId(IdUtil.simpleUUID());
        knowledgeParam.setRetryTimes(1);
        initParam(knowledgeParam);
        if (StringUtils.isBlank(knowledgeParam.getAppKey())) {
            knowledgeParam.setAppKey(yayiConfig.getAppKey());
        }

        if (StringUtils.isBlank(knowledgeParam.getAppSecret())) {
            knowledgeParam.setAppSecret(yayiConfig.getAppSecret());
        }
        return knowledgeParam;
    }


    /**
     * 获取结果
     *
     * @param builder
     * @return
     */
    private KnowledgeResult getResult(Request.Builder builder, YayiParam yayiParam) {
        KnowledgeResult result = new KnowledgeResult();
        log.info("===>runKnowledge url:{}  ,  param: {}", yayiParam.getUri(), JSONUtil.toJsonStr(yayiParam));
        String body = YayiApiUtils.invokeYaYiApi(builder, yayiParam);
        log.info("===>runKnowledge body: {}", body);
        if (StringUtils.isNotBlank(body) && body.contains("QPS访问超出限制")) {
            result.setMsg("QPS访问超出限制");
        } else if (StringUtils.isNotBlank(body) && JSONUtil.isTypeJSONObject(body)) {
            JSONObject entries = JSONUtil.parseObj(body);
            result = entries.toBean(KnowledgeResult.class);
        }
        return result;
    }

    /**
     * 初始化参数
     * @param knowledgeParam
     */
    private void initParam(KnowledgeParam knowledgeParam) {
        KnowledgeParam.Content content = knowledgeParam.getContent();
        if (null == content) {
            content = new KnowledgeParam.Content();
            knowledgeParam.setContent(content);
        }
        if (KnowledgeParam.KNOWLEDGE_CONFIG.equals(knowledgeParam.getModel())) {
            if (null != knowledgeConfig.getRetryTimes()) {
                knowledgeParam.setRetryTimes(knowledgeConfig.getRetryTimes());
            }
            knowledgeParam.setRetryInterval(3000);
            if (null != knowledgeConfig.getRetryInterval()) {
                knowledgeParam.setRetryInterval(knowledgeConfig.getRetryInterval());
            }

            if (StringUtils.isBlank(knowledgeParam.getUri())) {
                knowledgeParam.setUri(knowledgeConfig.getUri());
            }


            if (CollectionUtil.isEmpty(content.getMerge_split_approach()) && CollectionUtil.isNotEmpty(knowledgeConfig.getMergeSplitApproach())) {
                content.setMerge_split_approach(knowledgeConfig.getMergeSplitApproach());
            }

            //KnowledgeParam.RCTSConfig rctsConfig = content.getRcts_config();
            //if (null == rctsConfig) {
            //    rctsConfig = new KnowledgeParam.RCTSConfig();
            //    content.setRcts_config(rctsConfig);
            //}
            //if (null == rctsConfig.getChunk_size() && null != knowledgeConfig.getRctsChunkSize()) {
            //    rctsConfig.setChunk_size(knowledgeConfig.getRctsChunkSize());
            //}
            //if (null == rctsConfig.getChunk_overlap() && null != knowledgeConfig.getRctsChunkOverlap()) {
            //    rctsConfig.setChunk_overlap(knowledgeConfig.getRctsChunkOverlap());
            //}
            //
            //KnowledgeParam.RCTSConfig ctsConfig = content.getCts_config();
            //if (null == ctsConfig) {
            //    ctsConfig = new KnowledgeParam.RCTSConfig();
            //    content.setCts_config(ctsConfig);
            //}
            //if (null == ctsConfig.getChunk_overlap() && null != knowledgeConfig.getCtsChunkOverlap()) {
            //    ctsConfig.setChunk_overlap(knowledgeConfig.getCtsChunkOverlap());
            //}
            //if (null == ctsConfig.getChunk_size() && null != knowledgeConfig.getCtsChunkSize()) {
            //    ctsConfig.setChunk_size(knowledgeConfig.getCtsChunkSize());
            //}

            KnowledgeParam.RCTSConfig rctsConfigUp = content.getRCTS_config();
            if (null == rctsConfigUp) {
                rctsConfigUp = new KnowledgeParam.RCTSConfig();
                content.setRCTS_config(rctsConfigUp);
            }
            if (null == rctsConfigUp.getChunk_size() && null != knowledgeConfig.getRctsChunkSize()) {
                rctsConfigUp.setChunk_size(knowledgeConfig.getRctsChunkSize());
            }
            if (null == rctsConfigUp.getChunk_overlap() && null != knowledgeConfig.getRctsChunkOverlap()) {
                rctsConfigUp.setChunk_overlap(knowledgeConfig.getRctsChunkOverlap());
            }

            KnowledgeParam.RCTSConfig ctsConfigUp = content.getCTS_config();
            if (null == ctsConfigUp) {
                ctsConfigUp = new KnowledgeParam.RCTSConfig();
                content.setCTS_config(ctsConfigUp);
            }
            if (null == ctsConfigUp.getChunk_overlap() && null != knowledgeConfig.getCtsChunkOverlap()) {
                ctsConfigUp.setChunk_overlap(knowledgeConfig.getCtsChunkOverlap());
            }
            if (null == ctsConfigUp.getChunk_size() && null != knowledgeConfig.getCtsChunkSize()) {
                ctsConfigUp.setChunk_size(knowledgeConfig.getCtsChunkSize());
            }
        } else if (KnowledgeParam.KNOWLEDGE_SPLIT_CONFIG.equals(knowledgeParam.getModel())) {
            if (null != knowledgeSplitConfig.getRetryTimes()) {
                knowledgeParam.setRetryTimes(knowledgeSplitConfig.getRetryTimes());
            }
            knowledgeParam.setRetryInterval(3000);
            if (null != knowledgeSplitConfig.getRetryInterval()) {
                knowledgeParam.setRetryInterval(knowledgeSplitConfig.getRetryInterval());
            }

            if (StringUtils.isBlank(knowledgeParam.getUri())) {
                knowledgeParam.setUri(knowledgeSplitConfig.getUri());
            }

            if (null == content.getReturn_chunk() && null != knowledgeSplitConfig.getReturn_chunk()) {
                content.setReturn_chunk(knowledgeSplitConfig.getReturn_chunk());
            }

            if (CollectionUtil.isEmpty(content.getMerge_split_approach()) && CollectionUtil.isNotEmpty(knowledgeSplitConfig.getMerge_split_approach())) {
                content.setMerge_split_approach(knowledgeSplitConfig.getMerge_split_approach());
            }

            //KnowledgeParam.RCTSConfig rctsConfig = content.getRcts_config();
            //if (null == rctsConfig) {
            //    rctsConfig = new KnowledgeParam.RCTSConfig();
            //    content.setRcts_config(rctsConfig);
            //}
            //if (null == rctsConfig.getChunk_size() && null != knowledgeSplitConfig.getRctsChunkSize()) {
            //    rctsConfig.setChunk_size(knowledgeSplitConfig.getRctsChunkSize());
            //}
            //if (null == rctsConfig.getChunk_overlap() && null != knowledgeSplitConfig.getRctsChunkOverlap()) {
            //    rctsConfig.setChunk_overlap(knowledgeSplitConfig.getRctsChunkOverlap());
            //}
            //
            //KnowledgeParam.RCTSConfig ctsConfig = content.getCts_config();
            //if (null == ctsConfig) {
            //    ctsConfig = new KnowledgeParam.RCTSConfig();
            //    content.setCts_config(ctsConfig);
            //}
            //if (null == ctsConfig.getChunk_overlap() && null != knowledgeSplitConfig.getCtsChunkOverlap()) {
            //    ctsConfig.setChunk_overlap(knowledgeSplitConfig.getCtsChunkOverlap());
            //}
            //if (null == ctsConfig.getChunk_size() && null != knowledgeSplitConfig.getCtsChunkSize()) {
            //    ctsConfig.setChunk_size(knowledgeSplitConfig.getCtsChunkSize());
            //}


            KnowledgeParam.RCTSConfig rctsConfigUp = content.getRCTS_config();
            if (null == rctsConfigUp) {
                rctsConfigUp = new KnowledgeParam.RCTSConfig();
                content.setRCTS_config(rctsConfigUp);
            }
            if (null == rctsConfigUp.getChunk_size() && null != knowledgeSplitConfig.getRctsChunkSize()) {
                rctsConfigUp.setChunk_size(knowledgeSplitConfig.getRctsChunkSize());
            }
            if (null == rctsConfigUp.getChunk_overlap() && null != knowledgeSplitConfig.getRctsChunkOverlap()) {
                rctsConfigUp.setChunk_overlap(knowledgeSplitConfig.getRctsChunkOverlap());
            }

            KnowledgeParam.RCTSConfig ctsConfigUp = content.getCTS_config();
            if (null == ctsConfigUp) {
                ctsConfigUp = new KnowledgeParam.RCTSConfig();
                content.setCTS_config(ctsConfigUp);
            }
            if (null == ctsConfigUp.getChunk_overlap() && null != knowledgeSplitConfig.getCtsChunkOverlap()) {
                ctsConfigUp.setChunk_overlap(knowledgeSplitConfig.getCtsChunkOverlap());
            }
            if (null == ctsConfigUp.getChunk_size() && null != knowledgeSplitConfig.getCtsChunkSize()) {
                ctsConfigUp.setChunk_size(knowledgeSplitConfig.getCtsChunkSize());
            }
        }
    }
}
