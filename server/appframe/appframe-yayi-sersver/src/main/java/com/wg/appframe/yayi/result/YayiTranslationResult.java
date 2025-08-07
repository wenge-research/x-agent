package com.wg.appframe.yayi.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class YayiTranslationResult extends YayiResult implements Serializable {

    private static final long serialVersionUID = 1686918755974966131L;

    private TranslationData data;

    @Data
    public static class TranslationData implements Serializable {
        private static final long serialVersionUID = -6908997543969498317L;

        private String model_name;
        private String tgt;
        private String tgt_lang;

    }
}
