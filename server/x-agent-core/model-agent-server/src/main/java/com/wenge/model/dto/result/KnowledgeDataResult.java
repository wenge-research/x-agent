package com.wenge.model.dto.result;

import com.wenge.model.entity.KnowledgeData;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class KnowledgeDataResult extends KnowledgeData {

    private static final long serialVersionUID = -8730222371983444510L;


    /**
     * 数据分类
     */
    private String categoryName;

}
