package com.wenge.oauth.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RoleStatusUpdateParam extends WGParam {

    private static final long serialVersionUID = -7678371759804986592L;

    /**
     * 角色ID
     */
    private String roleId;

    /**
     * 状态
     */
    private String status;
}
