package com.wg.appframe.yayi.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class YayiContentIndexParam extends YayiParam implements Serializable {

    private static final long serialVersionUID = -3431598570338584117L;

    private Content content;

    @Data
    public static class Content implements Serializable {

        private static final long serialVersionUID = -3545036856478162189L;

        private String text;
        private String mode;
        private List<Doc> doc_list;
    }

    @Data
    public static class Doc implements Serializable {
        private static final long serialVersionUID = -3269120719252468732L;

        private String context;
        private String title;
        private String url;
        private String chunk_url;
    }
}

