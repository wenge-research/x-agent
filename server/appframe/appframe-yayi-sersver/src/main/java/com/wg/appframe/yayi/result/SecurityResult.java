package com.wg.appframe.yayi.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class SecurityResult extends YayiResult implements Serializable {
    private static final long serialVersionUID = -75954427063438215L;

    private Data data;

    @lombok.Data
    public static class Data implements Serializable{

        private static final long serialVersionUID = -7085514384805783850L;

        private String danger_prob;

        /**
         * 敏感词信息
         */
        private String danger_words;

        /**
         * 是否敏感:1-是，0-不是
         */
        private Integer is_danger;

    }
}
