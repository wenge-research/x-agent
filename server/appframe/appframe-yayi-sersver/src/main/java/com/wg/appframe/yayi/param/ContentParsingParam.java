package com.wg.appframe.yayi.param;

import com.wg.appframe.yayi.entity.YayiMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ContentParsingParam extends YayiParam implements Serializable  {

    private static final long serialVersionUID = -76730846719747855L;

    private String url;
    private Content content;

    @Data
    public static class Content implements Serializable {
        private static final long serialVersionUID = 8464824373083192587L;

        private Integer mode;
        private Integer get_ocr;
        private Integer watermark;
        private Integer xls_parse;
        private String webmode;
        private Integer get_caption;
        private List<YayiMessage> imgprompt;

    }

}
