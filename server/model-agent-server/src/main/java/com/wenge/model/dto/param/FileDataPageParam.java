package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class FileDataPageParam extends WgPageInfo {

    private static final long serialVersionUID = 3514272693195916373L;

    private String fileId;

    private String content;

    private String type;

    /**
     * 知识库id
     */
    private String knowledgeId;

    private List<String> knowledgeIds;

    private String keywords;
}
