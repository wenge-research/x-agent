package com.wg.appframe.yayi.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class PromptWebParam extends YayiParam implements Serializable {

    private static final long serialVersionUID = -5965287241751491326L;

    private Content content;


    @Data
    public static class Content implements Serializable {

        private static final long serialVersionUID = 42415838656200510L;

        private String function;
        private String user_id;
        private String user_message;
        private String rewritten_user_message;
        private Integer prompt_max_tokens;
        private Integer get_news_num;
        private Integer top_k;
        private List<String> web_source_list;
        private Float time_limit;
        private String data_type;
        private Boolean sort_label;
        private Boolean gen_mind_map;
        private Integer gen_max_tokens;
    }
}
