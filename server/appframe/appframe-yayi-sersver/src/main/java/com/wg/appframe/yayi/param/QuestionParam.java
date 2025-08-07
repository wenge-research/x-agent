package com.wg.appframe.yayi.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionParam extends YayiParam implements Serializable  {
    private static final long serialVersionUID = -5689012072438388501L;

    private Content content;

    @Data
    public static class Content implements Serializable {
        private static final long serialVersionUID = 8464824373083192587L;

        private String input_text;
        private String model;
        private Integer max_length;
        private Integer no_repeat_ngram_size;
        private Integer num_beams;
        private Integer questions_num;
    }

}
