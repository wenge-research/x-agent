package com.wg.appframe.yayi.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class RearrangeParam extends YayiParam implements Serializable  {

    private static final long serialVersionUID = 8464824373083192587L;

    private Content content;

    @Data
    public static class Content implements Serializable {
        private static final long serialVersionUID = 8464824373083192587L;

        private Integer n;
        private String query;
        private String function;
        private Integer batch_size;
        private List<Articles> articles;
    }

    // articles类
    @Data
    public static class Articles implements Serializable {
        private static final long serialVersionUID = 8464824373083192587L;

        private String title;
        private String para;
    }

}
