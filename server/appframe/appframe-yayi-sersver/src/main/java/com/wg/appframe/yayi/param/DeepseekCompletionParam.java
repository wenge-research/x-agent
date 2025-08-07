package com.wg.appframe.yayi.param;

import com.wg.appframe.yayi.entity.YayiMessage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DeepseekCompletionParam implements Serializable {

    private static final long serialVersionUID = 572372925479207306L;
    private List<YayiMessage> messages;
    private String uri;
    private String appKey;
    private String model;
    private Float presence_penalty;
    private Integer max_tokens;
    private Float frequency_penalty;
    private Object stop;
    private Boolean stream;
    private Float temperature;
    private Float top_p;
    private Boolean logprobs;
    private Integer top_logprobs;
    private Integer retryTimes;
    private Integer retryInterval;
    private ResponseFormat response_format;

    /**
     *    "tools": [
     *         {
     *             "type": "function",
     *             "function": {
     *                 "description": "从自然语言文本中提取参数",
     *                 "name": "extractParameters",
     *                 "parameters": {
     *                     "type": "object",
     *                     "properties": {
     *                         "location": {
     *                             "type": "string",
     *                             "description": "The city and state, e.g. San Francisco, CA"
     *                         }
     *                     },
     *                     "required": [
     *                         "location"
     *                     ]
     *                 }
     *             }
     *         }
     *     ]
     */
    private List<Tool> tools;

    @Data
    public static class ResponseFormat implements Serializable {

        private static final long serialVersionUID = -5715448233173777226L;

        private String type;
    }

    @Data
    public static class Tool implements Serializable {

        private static final long serialVersionUID = -2641132766050380438L;

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

        private static final long serialVersionUID = -8167363181500139736L;

        private String name;
        private String description;
        private Object parameters;
    }
}
