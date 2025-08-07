package com.wenge.oauth.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DeptStreetByTenantParam extends WGParam {

    private static final long serialVersionUID = 4908479063550231505L;

    private String tenantId;
}
