package com.wg.appframe.mybatisflex.factory;

import com.mybatisflex.core.tenant.TenantFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

public class WgTenantFactory implements TenantFactory {

    @Override
    public Object[] getTenantIds() {
        String tenantId = MDC.get("tenantId");
        if (StringUtils.isBlank(tenantId)) {
            return null;
        }
        return new Object[]{tenantId};
    }
}
