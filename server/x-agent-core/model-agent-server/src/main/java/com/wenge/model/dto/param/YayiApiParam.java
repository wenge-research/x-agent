package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class YayiApiParam extends WGParam {

    private static final long serialVersionUID = -868841806799836889L;

    private String clientId;
    private String question;

}
