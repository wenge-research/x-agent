package com.wg.appframe.mybatisflex.config;

import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.tenant.TenantManager;
import com.mybatisflex.spring.boot.MyBatisFlexCustomizer;
import com.wg.appframe.mybatisflex.factory.WgTenantFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlexCustomizerConfig implements MyBatisFlexCustomizer {

    @Value("${mybatis-flex.logicDelete.normalValue:0}")
    private String normalValue;

    @Value("${mybatis-flex.logicDelete.deletedValue:1}")
    private String deletedValue;

    @Override
    public void customize(FlexGlobalConfig flexGlobalConfig) {
        //设置数据库正常时的值
        //flexGlobalConfig.setNormalValueOfLogicDelete(normalValue);
        //
        ////设置数据已被删除时的值
        //flexGlobalConfig.setDeletedValueOfLogicDelete(deletedValue);

        TenantManager.setTenantFactory(new WgTenantFactory());
    }
}
