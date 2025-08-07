package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class KnowledgeApiPageQueryParam extends WgPageInfo {

    private static final long serialVersionUID = 3037384264619002498L;

    private String name;

    private String knowledgeId;

    private String foldersId;

}
