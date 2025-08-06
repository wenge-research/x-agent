package com.wenge.oauth.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OauthDeptParam extends WgPageInfo {

    private static final long serialVersionUID = -9189584313397130217L;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 租户id
     */
    private String tenantId;

}
