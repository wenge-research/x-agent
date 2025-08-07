package com.wg.appframe.yayi.config;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class DoubaoCompletionsConfig implements Serializable {
    private static final long serialVersionUID = 5920694522443822016L;
    private String uri;
    private String model;
    private Boolean include_usage;
//    private List<ChatMessage> messages;
    private Float temperature;
    private Float topP;
//    private Boolean stream;
//    private ChatCompletionRequest.ChatCompletionRequestStreamOptions streamOptions;
    private String stop;
    private Integer maxTokens;
    private Float presencePenalty;
    private Float frequencyPenalty;
//    private Map<String, Integer> logitBias;
//    private String user;
//    private List<ChatTool> tools;
//    private ChatCompletionRequest.ChatCompletionRequestFunctionCall functionCall;
    private Boolean logprobs;
    private Integer topLogprobs;
    private Float repetitionPenalty;
    private Integer n;

    private Integer retryTimes;
    private Integer retryInterval;
    private Map<String, Integer> logit_bias;

//    private Object toolChoice;
//    private ChatCompletionRequest.ChatCompletionRequestResponseFormat responseFormat;
}
