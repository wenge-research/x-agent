package com.wg.appframe.yayi.param;

import com.wg.appframe.yayi.config.MinmaxCompletionsConfig;
import com.wg.appframe.yayi.entity.YayiMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class MinmaxCompletionParam extends MinmaxCompletionsConfig {

    private static final long serialVersionUID = -7159603281598164884L;

    private List<YayiMessage> messages;
    private ResponseFormat response_format;

    private String uri;
    private String appKey;
    private List<Tool> tools;

    @Data
    public static class ResponseFormat implements Serializable {

        private static final long serialVersionUID = -6106592418179411798L;

        private String type;
        private JsonSchema json_schema;
    }

    @Data
    public static class JsonSchema implements Serializable {

        private static final long serialVersionUID = -7946705096309243795L;

        private String description;
        private String name;
        private Schema schema;
    }

    @Data
    public static class Schema implements Serializable {

        private static final long serialVersionUID = -3147866230705760116L;

        private String type;
        private Object properties;
    }

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
