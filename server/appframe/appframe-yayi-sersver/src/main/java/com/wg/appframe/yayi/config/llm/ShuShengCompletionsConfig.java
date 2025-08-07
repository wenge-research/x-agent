package com.wg.appframe.yayi.config.llm;

import com.wg.appframe.yayi.config.GeneralConfig;
import com.wg.appframe.yayi.entity.LlmTool;
import com.wg.appframe.yayi.entity.YayiMessage;
import lombok.Data;

import java.util.List;
import java.util.Optional;

public class ShuShengCompletionsConfig extends GeneralConfig {

    /**
     * 调用的模型名称
     */
    private String model;

    /**
     * 对话历史及本次提问目前 role (角色) 支持：user/assistant/system/tool
     */
    private List<YayiMessage> messages;

    /**
     * 	一个数组包含所有模型可以调用的 functions，根据这个数组模型会回复调用相应 function 的 json 对象。
     */
    private List<LlmTool> tools;

    /**
     * 支持 none(强制不调用)/auto(自动调用)/required(必须调用一个 function)，或者如参考示例传入某个必须让模型调用的 function 对象
     */
    private List<ToolChoice> tool_choice;

    /**
     * 采样温度
     */
    private float temperature;
    /**
     * 核采样的概率阈值，控制模型生成文本的多样性。
     * top_p越高，生成的文本更多样。反之，生成的文本更确定。
     * 取值范围：（0,1.0]
     */
    private float top_p;

    /**
     * 本次请求返回的最大 Token 数
     */
    private Integer max_tokens;

    /**
     * 是否流式返回
     */
    private Boolean stream;

    public ShuShengCompletionsConfig(GeneralConfig param) {
        this.model = param.getModel();
        this.messages = param.getMessages();
        this.tools = param.getTools();
        this.tool_choice = null;
        Optional.ofNullable(param.getTemperature()).ifPresent(v -> this.temperature = v.floatValue());
        Optional.ofNullable(param.getTopP()).ifPresent(v -> this.temperature = v.floatValue());
        this.max_tokens = param.getMaxTokens();
        this.stream = param.getStream();
    }

    @Data
    public static class ToolChoice {

        private String type;

        private Function function;
    }

    @Data
    public static class Function {
        private String name;
    }
}
