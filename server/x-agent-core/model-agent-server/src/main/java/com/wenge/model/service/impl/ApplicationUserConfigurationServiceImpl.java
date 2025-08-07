package com.wenge.model.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.entity.ApplicationUserConfiguration;
import com.wenge.model.mapper.ApplicationUserConfigurationMapper;
import com.wenge.model.service.ApplicationUserConfigurationService;
import com.wg.appframe.mybatisflex.core.Wrappers;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.wenge.model.entity.table.ApplicationUserConfigurationTableDef.APPLICATION_USER_CONFIGURATION;

@Service
public class ApplicationUserConfigurationServiceImpl extends ServiceImpl<ApplicationUserConfigurationMapper, ApplicationUserConfiguration> implements ApplicationUserConfigurationService {
    @Override
    public List<ApplicationUserConfiguration> selectByUserId(Long currentUserId) {
        return selectByUserIdAndAppId(currentUserId, null);
    }

    @Override
    public List<ApplicationUserConfiguration> selectByAppId(String applicationId) {
        return selectByUserIdAndAppId(null, applicationId);
    }

    @Override
    public List<ApplicationUserConfiguration> selectByUserIdAndAppId(Long currentUserId, String applicationId) {
        Wrappers<Object> where = Wrappers.init().where(currentUserId != null, APPLICATION_USER_CONFIGURATION.USER_ID.eq(currentUserId))
                .and(StringUtils.isNotBlank(applicationId), APPLICATION_USER_CONFIGURATION.APPLICATION_ID.eq(applicationId));
        return list(where);
    }
}
