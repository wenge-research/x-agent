package com.wg.appframe.yayi.result;

import com.wg.appframe.yayi.entity.YayiMessage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SiliconFlowCompletionResult implements Serializable {

    private static final long serialVersionUID = -4815110916213576275L;

    private List<GenerateChoice> choices;


    private String id;
    private String object;
    private Long created;
    private String model;
    private List<YayiMessage.ToolCall> tool_calls;
    private GenerateUsage usage;
    private Error error;

    @Data
    public static class GenerateChoice implements Serializable {

        private static final long serialVersionUID = 8941865186259969898L;
        
        private Integer index;
        private GenerateMessage message;
        private GenerateMessage delta;
        private String finish_reason;

    }

    @Data
    public static class GenerateMessage implements Serializable {
        private static final long serialVersionUID = -8303785465317357236L;

        private String content;
        private String role;

        /**
         * 仅d推理模型支持该返回，该部分返回思维链内容，与content同级。在每一轮对话过程中，模型会输出思维链内容（reasoning_content）和最终回答（content）。在下一轮对话中，之前轮输出的思维链内容不会被拼接到上下文中。
         */
        private String reasoning_content;
    }
    @Data
    public static class GenerateUsage implements Serializable {
        
        private static final long serialVersionUID = -8400218956269406572L;
        
        private String prompt_tokens;
        private String completion_tokens;
        private String total_tokens;
    }
    @Data
    public static class Error implements Serializable {

        private static final long serialVersionUID = -8936345137550730220L;

        private String message;
        private String type;
        private Object param;
        private String code;
    }
}
