package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SensitiveWordCheck extends WGParam {

    private static final long serialVersionUID = 7661723606374694097L;

    private String applicationId;

    private String content;
}
