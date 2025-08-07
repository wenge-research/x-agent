package com.wenge.model.dto.result;

import com.wg.appframe.core.dto.results.WGResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AuthPolicyResult extends WGResult {

    private static final long serialVersionUID = -4490167474426734925L;

    /**
     * 认证渠道
     */
    private String authChannel;

    /**
     * 策略编码
     */
    private String code;
}
