package com.wenge.model.dto.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
public class ArticleFilterParam implements Serializable {

    private static final long serialVersionUID = 4469352386608523523L;

    /**
     * 用户问题
     */
    private String original_question;

    /**
     * 文章列表
     */
    private List<Article> article_list;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Article implements Serializable {

        private static final long serialVersionUID = 7568031666514413362L;

        /**
         * 文章索引
         */
        private Integer index;

        /**
         * 文章内容
         */
        private String content;
    }
}
