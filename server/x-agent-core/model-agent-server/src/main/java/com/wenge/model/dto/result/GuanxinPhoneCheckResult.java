package com.wenge.model.dto.result;

import com.wg.appframe.core.dto.results.WGResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GuanxinPhoneCheckResult extends WGResult {

    private static final long serialVersionUID = 262174241954945417L;

    /**
     * 验证结果
     */
    private Object result;
}
