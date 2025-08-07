package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ApiAppDetailParam extends WGParam {

    private static final long serialVersionUID = -8373815594708798760L;

    private String apiKey;

}
