package com.wg.appframe.yayi.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class DialogueFilterParam extends YayiParam implements Serializable {

    private static final long serialVersionUID = 1571688366522463472L;

    private Content content;

    private String function;

    @Data
    public static class Content implements Serializable {

        private static final long serialVersionUID = 5092686357094787743L;

        private List<Convs> convs;
        private String preprocess_type;
        private Float cosine_threshold_value;
        private Float distance_threshold_value;
        private Integer n;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Convs implements Serializable {

        private static final long serialVersionUID = 1280979790342201514L;

        private String role;
        private String content;
    }
}
