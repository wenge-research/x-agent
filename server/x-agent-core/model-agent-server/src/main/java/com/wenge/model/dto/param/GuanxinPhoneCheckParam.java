package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GuanxinPhoneCheckParam extends WGParam {

    private static final long serialVersionUID = 1866756396102417014L;

    /**
     * 手机号
     */
    private String phone;
}
