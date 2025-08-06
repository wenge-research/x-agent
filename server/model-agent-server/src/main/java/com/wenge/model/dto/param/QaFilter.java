package com.wenge.model.dto.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
public class QaFilter implements Serializable {

    private static final long serialVersionUID = -6396157109389909532L;

    /**
     * 原始问题
     */
    private String originalQuestion;

    /**
     * 场景列表
     */
    private List<Scene> scenes;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Scene implements Serializable {

        private static final long serialVersionUID = -2636425579131022714L;

        /**
         * 问题
         */
        private String question;

        /**
         * 回答
         */
        private String answer;

        /**
         * 序号
         */
        private Integer index;

    }
}
