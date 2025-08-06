package com.wenge.model.dto.param;

import lombok.Data;


@Data
public class ApplicationQuestionRecommendParam {

    /**
     * 应用id
     */
    private String applicationId;

    /**
     * 问题
     */
    private String question;

    /**
     * 前N项，默认10
     */
    private Integer count = 10;


}
