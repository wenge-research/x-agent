package com.wenge.model.dto.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class NetworkApiResult implements Serializable {

    private static final long serialVersionUID = 2809474177341520712L;

    private List<Datum> data;
    private String message;

    @Data
    public static class Datum implements Serializable{

        private static final long serialVersionUID = 9013804182113977491L;

        /**
         * 文章内容
         */
        private String content;
        private String context;

        /**
         * 文章描述
         */
        private String description;

        /**
         * 是否渲染
         */
        private Long isRender;

        /**
         * 文章发布时间
         */
        private String pubtime;

        /**
         *  文章标题
         */
        private String title;

        /**
         * 文章链接
         */
        private String url;

        /**
         * 重排分数
         */
        private Float score;

        // 重写hashCode方法
        @Override
        public int hashCode() {
            return title.hashCode();
        }

        // 重写equals方法
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Datum other = (Datum) obj;
            return title.equals(other.title);
        }
    }
}
