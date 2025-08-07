package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class KnowledgeDataTypeParam extends WgPageInfo {

    private static final long serialVersionUID = 7227141856433360936L;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 类型
     */
    private String type;

    /**
     * 知识库id
     */
    private String knowledgeId;
}
