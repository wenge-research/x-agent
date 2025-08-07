package com.wg.appframe.yayi.param;

import com.wg.appframe.yayi.entity.YayiMessage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class KimiCompletionParam implements Serializable {

    private static final long serialVersionUID = 8636590019076380908L;

    private List<YayiMessage> messages;
    private String model;
    private Integer max_tokens;
    private Float temperature;
    private Float top_p;
    private Integer n;
    private Float presence_penalty;
    private Float frequency_penalty;
    private String stop;
    private Boolean stream;
    private Integer retryTimes;
    private Integer retryInterval;
    private String uri;
    private String appKey;
    private ResponseFormat response_format;
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
        private Object parameters;

    }
}
