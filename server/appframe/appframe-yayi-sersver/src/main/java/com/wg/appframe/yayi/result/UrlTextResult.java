package com.wg.appframe.yayi.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class UrlTextResult extends YayiResult implements Serializable {

    private static final long serialVersionUID = 7069593485207430642L;

    private TextData data;

    @Data
    public static class TextData implements Serializable {

        private static final long serialVersionUID = 1655086147842008387L;

        private String title;

        private String content;
    }
}
