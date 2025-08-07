package com.wg.appframe.yayi.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionResult extends YayiResult implements Serializable {
    private static final long serialVersionUID = 4102259383310357476L;

    private QuestionData data;

    // Data类
    @Data
    public static class QuestionData implements Serializable {

        private static final long serialVersionUID = 5132157250255496625L;

        private List<String> answers_list;
        private List<String> questions_list;
    }
}
