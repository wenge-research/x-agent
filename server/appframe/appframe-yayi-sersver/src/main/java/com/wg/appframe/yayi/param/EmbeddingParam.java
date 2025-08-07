package com.wg.appframe.yayi.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class EmbeddingParam extends YayiParam implements Serializable {

    private static final long serialVersionUID = 633625636965384064L;

    private Content content;

    @Data
    public static class Content implements Serializable {

        private static final long serialVersionUID = 4456701047945568073L;

        private String function;
        private String model;
        private String data_type;
        private List<String> data_list;

    }

}
