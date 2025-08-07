package com.wenge.oauth.dto.param;

import lombok.Data;

@Data
public class TenantUserParam {

    private String tenantId;

    // 是否绑定了商户
    private boolean isBind;

}
