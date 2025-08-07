package com.wg.appframe.yayi.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode
@NoArgsConstructor
public class YayiMessage implements Serializable {

    private static final long serialVersionUID = -3839742028109375630L;

    /**
     * 角色
     */
    private String role;

    /**
     * 名称
     */
    private String name;

    /**
     * 内容
     */
    private String content;

    public YayiMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }

    /**
     * 工具调用
     */
    @Nullable
    private List<ToolCall> tool_calls;

    public YayiMessage(String role, String content, List<ToolCall> toolCallList) {
        this(role, content);
        this.tool_calls = toolCallList;
    }

    @Data
    public static class ToolCall implements Serializable {

        private static final long serialVersionUID = 7352747013633463005L;

        private Integer index;
        private String id;
        private String type;
        private Function function;
    }

    @Data
    public static class Function implements Serializable {
        private static final long serialVersionUID = -6642655290287374375L;

        private String name;
        private Object arguments;
    }
}
