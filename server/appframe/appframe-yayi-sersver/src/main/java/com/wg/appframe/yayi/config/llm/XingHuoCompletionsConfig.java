package com.wg.appframe.yayi.config.llm;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wg.appframe.yayi.config.GeneralConfig;
import com.wg.appframe.yayi.entity.LlmTool;
import com.wg.appframe.yayi.entity.YayiMessage;

import java.util.List;
import java.util.Optional;

public class XingHuoCompletionsConfig extends GeneralConfig {
    /**
     * lite
     * generalv3
     * pro-128k
     * generalv3.5
     * max-32k
     * 4.0Ultra
     */
    private String model;

    /**
     * 用户的唯一id，表示一个用户
     */
    private String user;
    private List<YayiMessage> messages;
    /**
     * 核采样阈值
     */
    private Float temperature;
    private Integer top_k;
    private Boolean stream;
    private Integer max_tokens;
    private Float presence_penalty;
    private Float frequency_penalty;
    private List<LlmTool> tools;
    private JSONObject response_format;
    private List<String> suppress_plugin;

    public XingHuoCompletionsConfig(GeneralConfig param) {
        this.model = param.getModel();
        this.user = param.getId();
        this.messages = param.getMessages();
        Optional.ofNullable(param.getTemperature())
                .ifPresent((temperature) -> this.temperature = temperature.floatValue());
        Optional.ofNullable(param.getTopK())
                .ifPresent((topK) -> this.top_k = topK.intValueExact());
        Optional.ofNullable(param.getPresencePenalty())
                .ifPresent((pp) -> this.presence_penalty = pp.floatValue());
        Optional.ofNullable(param.getFrequencyPenalty())
                .ifPresent((fp) -> this.frequency_penalty = fp.floatValue());
        Optional.ofNullable(param.getResponseFormat())
                .ifPresent((format) -> this.response_format = JSONUtil.parseObj(format));
        this.stream = param.getStream();
        this.max_tokens = param.getMaxTokens();
        this.tools = param.getTools();
        this.suppress_plugin = param.getSuppressPlugin();
    }
}
