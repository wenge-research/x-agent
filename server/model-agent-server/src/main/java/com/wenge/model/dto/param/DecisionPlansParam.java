package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DecisionPlansParam extends WGParam {

    private static final long serialVersionUID = -1601723768765655287L;

    /**
     * 会话id
     */
    private String sessionId;

    /**
     * 轮次
     */
    private String round;
}
