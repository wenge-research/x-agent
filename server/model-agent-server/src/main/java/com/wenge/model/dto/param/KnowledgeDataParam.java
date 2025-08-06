package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class KnowledgeDataParam extends WGParam {

    private static final long serialVersionUID = -314687128332375170L;

    private String knowledgeId;

    private String knowledgeName;

    private String startTime;

    private String endTime;
}
