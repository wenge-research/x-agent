package com.wg.appframe.yayi.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class VolcengineRearrangeParam extends VolcengineParam implements Serializable {

    private static final long serialVersionUID = 8464824373083192587L;

    private String rerank_model;
    private List<Article> datas;

    @Data
    public static class Article implements Serializable {
        private static final long serialVersionUID = 8464824373083192587L;

        // 标题（注：不能为null）
        private String title;
        // 匹配的内容（注：不能为null）
        private String query;
        // 内容（注：不能为null）
        private String content;
    }

}
