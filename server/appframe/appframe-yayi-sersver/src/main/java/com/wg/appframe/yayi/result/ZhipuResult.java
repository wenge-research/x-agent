package com.wg.appframe.yayi.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ZhipuResult implements Serializable {

    private static final long serialVersionUID = -3573959167684853339L;

    private String id;
    private Long created;
    private String model;
    private List<GenerateChoice> choices;
    private GenerateUsage usage;

    @Data
    public static class GenerateChoice implements Serializable {

        private static final long serialVersionUID = -8627622933453511454L;
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
    }
    @Data
    public static class GenerateUsage implements Serializable {

        private static final long serialVersionUID = -4763741427018102704L;

        private String prompt_tokens;
        private String completion_tokens;
        private String total_tokens;
    }
}
