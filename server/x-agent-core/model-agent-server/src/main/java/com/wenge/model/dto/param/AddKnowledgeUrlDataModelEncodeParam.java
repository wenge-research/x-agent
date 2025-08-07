package com.wenge.model.dto.param;

import lombok.Data;
import org.dromara.easyes.annotation.IndexId;
import org.dromara.easyes.annotation.IndexName;
import org.dromara.easyes.annotation.rely.IdType;

import java.io.Serializable;
import java.util.List;

@IndexName
@Data
public class AddKnowledgeUrlDataModelEncodeParam implements Serializable {

    private static final long serialVersionUID = 7010729784945401865L;

    private String id;
    private String knowledgeId;

    private String urlId;

    /**
     * 所属URL
     */
    private String content;
}
