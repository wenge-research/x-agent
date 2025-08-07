package com.wg.appframe.yayi.param;

import com.wg.appframe.yayi.config.MinmaxImageConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class MinmaxImageParam extends MinmaxImageConfig {

    private static final long serialVersionUID = 353372764686179458L;

    private String prompt;
    private String appKey;

    private SubjectReference subject_reference;
    private Style style;

    @Data
    public static class SubjectReference implements Serializable {

        private static final long serialVersionUID = 5489919780257890243L;

        private String type;
        private List<String> image_file;
    }

    @Data
    public static class Style implements Serializable {

        private static final long serialVersionUID = 2599164669323858938L;
        private String style_type;
        private Float style_weight;
    }
}
