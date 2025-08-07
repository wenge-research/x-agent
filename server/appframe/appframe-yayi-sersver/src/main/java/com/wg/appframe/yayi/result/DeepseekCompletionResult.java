package com.wg.appframe.yayi.result;

import com.wg.appframe.yayi.entity.YayiMessage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DeepseekCompletionResult implements Serializable {

    private static final long serialVersionUID = -5419980225317106883L;

    private String id;
    private String object;
    private Long created;
    private String model;
    private List<GenerateChoice> choices;
    private GenerateUsage usage;
    private Error error;
    private String done_reason;
    private Boolean done;
    private GenerateMessage message;

    @Data
    public static class GenerateChoice implements Serializable {

        private static final long serialVersionUID = -8627622933453511454L;
        private Integer index;
        private GenerateMessage message;
        private String finish_reason;
        private GenerateMessage delta;
    }

    @Data
    public static class Delta implements Serializable {
        private static final long serialVersionUID = 3652203503471739856L;

        private Integer index;
        private GenerateMessage message;
        private String finish_reason;

    }

    @Data
    public static class GenerateMessage implements Serializable {
        private static final long serialVersionUID = -8303785465317357236L;

        private String content;
        private String reasoning_content;
        private String role;
        private List<YayiMessage.ToolCall> tool_calls;
    }
    @Data
    public static class GenerateUsage implements Serializable {

        private static final long serialVersionUID = -4763741427018102704L;

        private String prompt_tokens;
        private String completion_tokens;
        private String total_tokens;
    }

    @Data
    public static class Error implements Serializable {

        private static final long serialVersionUID = -9042450643125617836L;

        private String message;
        private String type;
        private Object param;
        private String code;
    }
}
