package com.wg.appframe.yayi.config.llm;

import com.wg.appframe.yayi.config.GeneralConfig;
import com.wg.appframe.yayi.entity.YayiMessage;

import java.util.List;
import java.util.Optional;

public class HunYuanCompletionsConfig extends GeneralConfig {
    private String Action;
    private String Version;
    private String Region;
    private String Model;
    private List<YayiMessage> Messages;
    private Float Temperature;
    private Float TopP;
    private Integer MaxTokens;

    public HunYuanCompletionsConfig(GeneralConfig param) {
        this.Action = "ChatCompletion";
        this.Version = "2021-11-11";
        this.Region = param.getRegion();
        this.Model = param.getModel();
        this.Messages = param.getMessages();
        Optional.ofNullable(param.getTemperature()).ifPresent((temperature) -> this.Temperature = temperature.floatValue());
        Optional.ofNullable(param.getTopP()).ifPresent((topP) -> this.TopP = topP.floatValue());
        this.MaxTokens = param.getMaxTokens();
    }
}
