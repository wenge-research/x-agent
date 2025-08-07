package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class StepByDialogIdParam extends WGParam {

    private static final long serialVersionUID = -4272573243762634880L;

    private String dialogId;
}
