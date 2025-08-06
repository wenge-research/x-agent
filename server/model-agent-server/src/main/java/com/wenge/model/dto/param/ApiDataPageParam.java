package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ApiDataPageParam extends WgPageInfo {

    private static final long serialVersionUID = 3514272693195916373L;

    private String knowledgeApiId;

    private String content;

    private String type;

    private List<String> knowledgeIds;
}
