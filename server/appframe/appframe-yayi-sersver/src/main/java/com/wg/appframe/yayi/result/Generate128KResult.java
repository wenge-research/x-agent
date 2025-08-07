package com.wg.appframe.yayi.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class Generate128KResult extends YayiResult implements Serializable {

    private static final long serialVersionUID = 4162338697695050733L;

    private GenerateData data;

    @Data
    public static class GenerateData implements Serializable {

        private static final long serialVersionUID = 3004133171152686560L;

        private List<GenerateChoice> choices;
        private GenerateUsage usage;

    }

    @Data
    public static class GenerateChoice implements Serializable {

        private static final long serialVersionUID = -8627622933453511454L;

        private GenerateMessage message;
        private String finish_reason;
        private String index;

    }

    @Data
    public static class GenerateMessage implements Serializable {
        private static final long serialVersionUID = -8303785465317357236L;

        private String content;
        private String role;
        private String type;
    }
    @Data
    public static class GenerateUsage implements Serializable {

        private static final long serialVersionUID = -4763741427018102704L;

        private String prompt_tokens;
        private String completion_tokens;
        private String total_tokens;
    }
}
