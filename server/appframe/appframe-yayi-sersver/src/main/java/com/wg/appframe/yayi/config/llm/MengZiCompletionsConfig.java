package com.wg.appframe.yayi.config.llm;

import com.wg.appframe.yayi.config.GeneralConfig;
import com.wg.appframe.yayi.entity.YayiMessage;

import java.util.List;
import java.util.Optional;

public class MengZiCompletionsConfig extends GeneralConfig {
    /**
     * 	模型名称，示例 mengzi-lite，详细见模型列表。
     */
    private String model;

    /**
     * 聊天上下文信息。
     */
    private List<YayiMessage> messages;

    /**
     * 	基于温度系数的采样系数，值越大，输出越随机。有些模型不支持，详细见模型列表。
     */
    private Float temperature;

    /**
     * 核采样，功能类似 temperature，两个值最好不要同时修改。有些模型不支持，详细见模型列表。
     */
    private Float top_p;

    /**
     * 返回数量，取值范围 [1,3]，默认 1。
     */
    private Integer n;

    /**
     * 是否以流式接口的形式返回数据，默认 false，有些模型不支持，详细见模型列表。
     */
    private Boolean stream;

    /**
     * 	生成 tokens 最大数量，详细见模型列表。
     */
    private Integer max_tokens;

    /**
     * 	用户的标识，可用于检测滥用行为，以防止接口恶意调用。
     */
    private String user;

    public MengZiCompletionsConfig(GeneralConfig param) {
        this.model = param.getModel();
        this.messages = param.getMessages();
        Optional.ofNullable(param.getTemperature()).ifPresent(v -> this.temperature = v.floatValue());
        Optional.ofNullable(param.getTopP()).ifPresent(v -> this.temperature = v.floatValue());
        this.n = param.getN();
        this.stream = param.getStream();
        this.max_tokens = param.getMaxTokens();
        this.user = param.getId();
    }
}
