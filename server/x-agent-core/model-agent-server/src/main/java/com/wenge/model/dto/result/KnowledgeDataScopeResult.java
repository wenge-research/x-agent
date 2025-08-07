package com.wenge.model.dto.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class KnowledgeDataScopeResult extends KnowledgeDataResult implements Serializable {

    private static final long serialVersionUID = -8730222371983444510L;

    /**
     * 相似分数
     */
    private Float similarityScores;

    private Integer groupIndex;
}
