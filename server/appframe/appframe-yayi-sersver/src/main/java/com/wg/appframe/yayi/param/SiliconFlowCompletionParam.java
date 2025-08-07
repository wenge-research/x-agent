package com.wg.appframe.yayi.param;

import com.wg.appframe.yayi.entity.YayiMessage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SiliconFlowCompletionParam implements Serializable {

    private static final long serialVersionUID = -6979021745605971438L;

    private List<YayiMessage> messages;
    private String model;
    private Integer max_tokens;
    private Float frequency_penalty;
    private Float presence_penalty;
    private Integer n;
    // private String response_format;
    private String[] stop;
    private Boolean stream;
    private Float temperature;
    private Float top_p;
    private Float top_k;
    private Integer retryTimes;
    private Integer retryInterval;
    private String appKey;
    private String uri;
    private DeepseekCompletionParam.ResponseFormat response_format;
    private List<Tool> tools;

    @Data
    public static class Tool implements Serializable {

        private static final long serialVersionUID = -2641132766050380438L;

        /**
         * function
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
        private String strict;
        private Object parameters;

    }
}
