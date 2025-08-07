package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DecisionProcessParam extends WGParam {

    private static final long serialVersionUID = -5297183634890795123L;

    private String clientId;
    private String sessionId;
}
