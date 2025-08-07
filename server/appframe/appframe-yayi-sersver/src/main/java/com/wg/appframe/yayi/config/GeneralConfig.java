package com.wg.appframe.yayi.config;

import com.wg.appframe.yayi.entity.LlmTool;
import com.wg.appframe.yayi.entity.YayiMessage;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 抽象通用服务请求参数
 */
@Getter
@Setter
public class GeneralConfig {
    /**
     * appKey
     */
    protected String appKey;

    /**
     * 模型编码
     */
    protected String modelCode;

    /**
     * 秘钥
     */
    protected String appSecret;

    /**
     * best_of
     */
    protected BigDecimal bestOf;

    /**
     * 1:true，0：false
     */
    protected Boolean disableSearch;

    /**
     * 1:true，0：false
     */
    protected Boolean doSample;

    /**
     * 1:true，0：false
     */
    protected Boolean enableCitation;

    /**
     * 1:true，0：false
     */
    protected Boolean enableSystemMemory;

    /**
     * 1:true，0：false
     */
    protected Boolean enableTrace;

    /**
     * frequency_penalty
     */
    protected BigDecimal frequencyPenalty;

    /**
     * 1:true，0：false
     */
    protected Boolean logprobs;

    /**
     * maxNewTokens
     */
    protected Integer maxNewTokens;

    /**
     * max_output_tokens
     */
    protected Integer maxOutputTokens;



    /**
     * max_tokens
     */
    protected Integer maxTokens;

    /**
     * model
     */
    protected String model;

    /**
     * 模型id，有业务作用
     */
    protected String modelId;

    /**
     * 模型名称
     */
    protected String modelName;

    /**
     * n
     */
    protected Integer n;

    /**
     * penalty_score
     */
    protected BigDecimal penaltyScore;

    /**
     * presence_penalty
     */
    protected BigDecimal presencePenalty;

    /**
     * repetition_penalty
     */
    protected BigDecimal repetitionPenalty;

    /**
     * response_format
     */
    protected String responseFormat;

    /**
     * 重试间隔，毫秒
     */
    protected Integer retryInterval;

    /**
     * 重试次数
     */
    protected Integer retryTimes;

    /**
     * 状态[启用/停用]
     */
    protected String status;

    /**
     * stop
     */
    protected String stop;

    /**
     * 1:true，0：false
     */
    protected Boolean stream;

    /**
     * system
     */
    protected String system;

    /**
     * system_memory_id
     */
    protected String systemMemoryId;

    /**
     * temperature
     */
    protected BigDecimal temperature;

    /**
     * top_k
     */
    protected BigDecimal topK;

    /**
     * topLogprobs
     */
    protected BigDecimal topLogprobs;

    /**
     * top_p
     */
    protected BigDecimal topP;

    /**
     * 大模型接口地址
     */
    protected String uri;

    /**
     * 厂商名称
     */
    protected String manufacturer;

    /**
     * deleteFlag
     */
    protected Integer deleteFlag;


    protected List<YayiMessage> messages;

    protected Integer seed;

    protected List<LlmTool> tools;

    /**
     * uri路径参数
     */
    protected Map<String, String> path;

    /**
     * 区分用户的唯一标识
     */
    protected String id;

    protected List<String> suppressPlugin;

    protected String region;
}
