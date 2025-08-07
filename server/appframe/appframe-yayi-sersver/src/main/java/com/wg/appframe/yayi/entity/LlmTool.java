package com.wg.appframe.yayi.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class LlmTool implements Serializable {

    private static final long serialVersionUID = 6971208402226720900L;

    /**
     * 插件类型
     */
    private String type;

    /**
     * function
     */
    private Function function;

    @Data
    public static class Function implements Serializable {

        private static final long serialVersionUID = -8167363181500139736L;

        private String name;
        private String description;
        private Object parameters;
    }
}
