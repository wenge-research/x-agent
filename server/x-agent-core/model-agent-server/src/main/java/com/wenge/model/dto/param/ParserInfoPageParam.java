package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ParserInfoPageParam extends WgPageInfo {

    private static final long serialVersionUID = -4583495844404370140L;

    private String knowledgeId;

    private List<String> knowledgeIds;

    private String title;
    private String foldersId;

    private boolean needParam = false;
}
