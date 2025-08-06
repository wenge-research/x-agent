package com.wenge.oauth.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateTenantParam extends WGParam {

    private static final long serialVersionUID = 2449627247389053409L;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 用户id列表
     */
    private List<Long> userIdList;
}
