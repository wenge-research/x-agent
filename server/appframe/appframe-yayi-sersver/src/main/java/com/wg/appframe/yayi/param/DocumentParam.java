package com.wg.appframe.yayi.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class DocumentParam extends YayiParam implements Serializable  {

    private static final long serialVersionUID = 8455476961321687081L;

    private String url;
    private Content content;

    @Data
    public static class Content implements Serializable {
        private static final long serialVersionUID = 8464824373083192587L;

        private String user_id;
        private Integer do_layout;
        private Integer get_bbox;
        private Integer table_html;
        private Integer watermark;
        private Integer faq;
        private Integer mode;

    }

}
