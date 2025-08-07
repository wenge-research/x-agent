package com.wenge.oauth.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RolePageParam extends WgPageInfo {

    private static final long serialVersionUID = 2327155894200324963L;

    /**
     * 角色名称
     */
    private String roleName;
    private String endDate;
    private String startDate;
    private String status;
}
