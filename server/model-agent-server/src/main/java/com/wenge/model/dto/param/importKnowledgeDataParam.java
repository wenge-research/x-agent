package com.wenge.model.dto.param;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Data
public class importKnowledgeDataParam implements Serializable {

    private static final long serialVersionUID = -6536474977857884195L;

    /**
     * 文件
     */
    private MultipartFile file;

    /**
     * 知识库id
     */
    private String knowledgeId;

    /**
     * 是否覆盖
     */
    private Boolean replaceFlag;

}
