package com.wenge.model.dto.result;

import com.wg.appframe.core.dto.results.WGResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DecisionInitResult extends WGResult {

    private static final long serialVersionUID = -6744654840160380564L;

    private String session_id;
    private String status;
    private String message;
}
