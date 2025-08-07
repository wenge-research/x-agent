package com.wg.appframe.yayi.entity;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * <a href="https://www.volcengine.com/docs/82379/1298454#%E8%AF%B7%E6%B1%82%E4%BD%93">豆包API文档</a>
 */
@Builder
@Getter
public class DoubaoRequestBody implements Serializable {
    private static final long serialVersionUID = 5880949997479583120L;

    private String model;

    private List<MessageParam> messages;

    private Boolean stream;

    private StreamOptionsParam stream_options;

    private Integer max_tokens;

    private List<String> stop;

    private Double frequency_penalty;

    private Double presence_penalty;

    private Double temperature;

    private Double top_p;

    private Boolean logprobs;

    private Integer top_logprobs;

    private Map<Integer, Double> logit_bias;

    private List<ToolParam> tools;

    @Data
    public static class MessageParam {
        private String role;

        private String content;

        private List<MessageToolCallParam> tool_calls;

        private String tool_call_id;

    }

    /**
     * <a href="https://www.volcengine.com/docs/82379/1298454#streamoptionsparam">...</a>
     */
    @Data
    public static class StreamOptionsParam {
        //是否包含本次请求的 token 用量统计信息
        private Boolean include_usage;
    }

    @Data
    public static class ToolParam {
        private String type;

        private FunctionDefinition function;
    }

    @Data
    public static class FunctionDefinition {
        private String name;

        private String description;
        // JSON Schema
        private Object parameters;
    }

    @Data
    public static class MessageToolCallParam {
        private String id;

        private String type;

        private FunctionParam function;
    }

    @Data
    public static class FunctionParam {
        private String name;

        private String arguments;
    }
}
