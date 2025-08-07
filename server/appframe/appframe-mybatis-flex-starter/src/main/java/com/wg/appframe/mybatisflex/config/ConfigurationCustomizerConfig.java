package com.wg.appframe.mybatisflex.config;

import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.mybatis.FlexConfiguration;
import com.mybatisflex.core.tenant.TenantFactory;
import com.mybatisflex.spring.boot.ConfigurationCustomizer;
import com.wg.appframe.mybatisflex.factory.WgTenantFactory;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigurationCustomizerConfig implements ConfigurationCustomizer {

    @Value("${mybatisFlex.auditEnable:false}")
    private boolean auditEnable;

    @Override
    public void customize(FlexConfiguration flexConfiguration) {
        // SQL审计开关
        AuditManager.setAuditEnable(auditEnable);

        if (auditEnable) {
            flexConfiguration.setLogImpl(StdOutImpl.class);
        }
    }

    //@Bean
    //public TenantFactory tenantFactory(){
    //    return new WgTenantFactory();
    //}
}
