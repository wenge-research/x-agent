package com.wenge.model.dto.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RecommendQuestionData implements Serializable {

    private static final long serialVersionUID = -826248558154588966L;

    /**
     * 推荐问题策略名称
     */
    private String recommendStrategyName;

    /**
     * 推荐问题列表
     */
    List<RecommendQuestionResult> recommendQuestionResultList;
}
