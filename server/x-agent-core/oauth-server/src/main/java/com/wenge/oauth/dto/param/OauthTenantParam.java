package com.wenge.oauth.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OauthTenantParam extends WgPageInfo {

    private static final long serialVersionUID = 1348725222847643268L;

    private String tenantName;

    private String enDate;
    private String startDate;
    private String status;
}
