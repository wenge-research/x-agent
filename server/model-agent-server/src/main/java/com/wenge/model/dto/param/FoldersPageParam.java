package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FoldersPageParam extends WgPageInfo {

    private static final long serialVersionUID = 3243028416015926211L;

    private String folderName;
    private String knowledgeId;
    private Integer type;
    private String foldersId;
}
