package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AppConfigListParam extends WGParam {

    private static final long serialVersionUID = 480446678615633851L;

    /**
     * 应用id
     */
    private String applicationId;
}
