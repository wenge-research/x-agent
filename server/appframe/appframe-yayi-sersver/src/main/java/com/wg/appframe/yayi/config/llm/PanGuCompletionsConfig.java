package com.wg.appframe.yayi.config.llm;

import com.wg.appframe.yayi.config.GeneralConfig;
import com.wg.appframe.yayi.entity.YayiMessage;

import java.util.List;
import java.util.Optional;

public class PanGuCompletionsConfig extends GeneralConfig {
    /**
     * 多轮对话问答对
     */
    private List<YayiMessage> messages;

    /**
     * 用于代表客户的唯一标识符,最小长度:1,最大长度64
     */
    private String user;

    /**
     * 流式调用的开启开关,true为开启流式调用
     */
    private Boolean stream;

    /**
     * 采样温度
     */
    private Float temperature;

    /**
     * 一种替代温度采样的方法,称为nucleus sampling,取值范围:(0, 1],其中模型考虑具有top_p 概率质量的标记的结果。
     */
    private float top_p;

    /**
     * 用于控制聊天回复的长度和质量。
     */
    private Integer max_tokens;

    /**
     * 表示对每个问题生成多少条答案。n参数的默认值是1,表示只生成一个答案。
     */
    private Integer n;

    /**
     * 用于控制生成文本中的重复程度。
     */
    private Float presence_penalty;

    /**
     * 用于调整模型对频繁出现的Token的处理方式。
     */
    private Float frequency_penalty;

    public PanGuCompletionsConfig(GeneralConfig generalConfig) {
        this.messages = generalConfig.getMessages();
        this.user = generalConfig.getId();
        this.stream = generalConfig.getStream();
        Optional.ofNullable(generalConfig.getTemperature())
                .ifPresent(temperature -> this.temperature = temperature.floatValue());
        Optional.ofNullable(generalConfig.getTopP()).ifPresent(topP -> this.top_p = topP.floatValue());
        this.max_tokens = generalConfig.getMaxTokens();
        this.n = generalConfig.getN();
        Optional.ofNullable(generalConfig.getPresencePenalty())
                .ifPresent(presencePenalty -> this.presence_penalty = presencePenalty.floatValue());
        Optional.ofNullable(generalConfig.getFrequencyPenalty())
                .ifPresent(frequencyPenalty -> this.frequency_penalty = frequencyPenalty.floatValue());
    }
}
