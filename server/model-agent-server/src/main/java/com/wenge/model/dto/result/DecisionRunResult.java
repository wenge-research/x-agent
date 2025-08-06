package com.wenge.model.dto.result;

import com.wg.appframe.core.dto.results.WGResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DecisionRunResult extends WGResult {

    private static final long serialVersionUID = -7358302472990392254L;

    private Integer round;
    private String status;
    private String message;
}
