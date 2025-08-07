package com.wg.appframe.yayi.param;

import com.wg.appframe.yayi.entity.YayiMessage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class DoubaoCompletionParam implements Serializable {
    private static final long serialVersionUID = -440291664768186300L;
    private List<YayiMessage> messages;

    private String uri;
    private String appKey;
    private String model;
    private String modelName;
    private Boolean stream;
    private Integer retryTimes;
    private Integer retryInterval;
    private StreamOptionsParam stream_options;
    private Integer max_tokens;
    private String[] stop;
    private Float frequency_penalty;
    private Float presence_penalty;
    private Float temperature;
    private Float top_p;
    private Boolean logprobs;
    private Integer top_logprobs;
    private Map<String, Integer> logit_bias;
    private List<Tool> tools;

    @Data
    public static class StreamOptionsParam implements Serializable {

        private static final long serialVersionUID = 7361494739864570357L;

        /**
         *
         * 是否包含本次请求的 token 用量统计信息
         * false：不返回 token 用量信息
         * true：在 data: [DONE] 消息之前返回一个额外的块，此块上的 usage 字段代表整个请求的 token 用量，choices 字段为空数组。所有其他块也将包含 usage 字段，但值为 null
         */
        private Boolean include_usage;
    }

    @Data
    public static class Tool implements Serializable {

        private static final long serialVersionUID = 3747396942080551284L;

        /**
         * 插件类型
         */
        private String type;

        /**
         * function
         */
        private Function function;
    }


    @Data
    public static class Function implements Serializable {
        private static final long serialVersionUID = 8648768355886688963L;

        private String name;
        private String description;
        private Object parameters;
    }
}
