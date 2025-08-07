package com.wg.appframe.yayi.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class YayiTranslationParam extends YayiParam implements Serializable  {

    private static final long serialVersionUID = -7925643426656388425L;

    private Content content;

    @Data
    public static class Content implements Serializable {
        private static final long serialVersionUID = 8464824373083192587L;

        private String input_text;
        private String src_lang;
        private String tgt_lang;
    }

}
