package com.wenge.model.dto.param;

import lombok.Data;
import org.dromara.easyes.annotation.IndexId;
import org.dromara.easyes.annotation.IndexName;
import org.dromara.easyes.annotation.rely.IdType;

import java.io.Serializable;
import java.util.List;

@IndexName
@Data
public class AddKnowledgeUrlDataParam implements Serializable {

    private static final long serialVersionUID = 7010729784945401865L;

    @IndexId(type = IdType.UUID)
    private String id;
    private String knowledgeId;

    /**
     * 所属URL
     */
    private String url;

    private List<String> urls;

    private String urlId;

    private String content;

    private String effectiveStartTime;

    private String effectiveEndTime;

    private String foldersId;
}
