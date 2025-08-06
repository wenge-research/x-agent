package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class StatusUpdateParam extends WGParam {

    private static final long serialVersionUID = -314687128332375170L;

    private String knowledgeId;
    private String status;
}
