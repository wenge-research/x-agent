package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class KnowledgeUrlDataParam extends WgPageInfo {

    private static final long serialVersionUID = -6905897953941937944L;

    private String urlId;
    private String content;
}
